package org.ecomm.helpers.basket

import org.ecomm.controllers.requests.BasketItems
import org.ecomm.models.{ MultisaveId, Price, UPC }
import org.ecomm.models.basket.{ BasketTotal, Items }
import org.ecomm.models.basket.multisave.{ MultiSaveItemRequirements, Multisave }
import org.ecomm.models.basket.multisave.MultisaveOperator.{ AND, OR }
import org.ecomm.models.responses.exceptions.{ ItemNotFoundException, MultisaveRequirementNotMetException }

import akka.http.scaladsl.server.directives.HttpRequestWithEntity

import scala.annotation.tailrec
import scala.collection.mutable

object BasketHelper {
  private case class IntermediateItem(upc: UPC, multisave: Seq[Multisave])
  private case class IntermediateMultisave(id: MultisaveId, subtract: Seq[(UPC, Long)], discountAmount: Price) {
    // In this case, Greater Than, means better than. It's a better multisave deal if:
    // - The discount price is greater
    // - It requires fewer items to satisfy the deal
    def >(intermediateMultisave: IntermediateMultisave): Boolean =
      discountAmount > intermediateMultisave.discountAmount ||
        (discountAmount == intermediateMultisave.discountAmount && subtract.map(_._2).sum <= intermediateMultisave.subtract.map(_._2).sum)

    def <(intermediateMultisave: IntermediateMultisave): Boolean =
      ! >(intermediateMultisave)
  }

  //O(N) with branches on Multisave deals and item requirements. So more like O(A + B + C)
  def calculateTotal()(implicit req: HttpRequestWithEntity[BasketItems]): BasketTotal = {
    var total: BigDecimal =
      0

    val items =
      req
        .body
        .items
        // Grouping items in the cart that have the same UPC, but
        // may be separated.
        .groupBy(_.upc)
        .toSeq
        // Since we're not running over the full combination range
        // it's crucial that there be some ordering for discount reproducibility.
        .sortWith(_._1 > _._1)

    // Need mutable quantity Map to subtract items already in
    // play for applied multisave discounts.
    val (intermediateItems, mutableItemQuantityMap) = {
      val intermediateItems: mutable.Seq[IntermediateItem] =
        mutable.Seq.empty[IntermediateItem]

      val mutableItemQuantityMap =
        mutable.Map.empty[UPC, Long]

      // Using `foreach` to avoid multiple N + N + N + ...
      items.foreach {
        case (upc, item) =>
          val quantity =
            item.foldLeft(0L) {
              case (acc, innerItem) =>
                acc + innerItem.quantity
            }

          val itemPrice =
            Items
              .priceMap
              .getOrElse(upc, throw ItemNotFoundException(upc))

          val itemMultisave =
            Multisave
              .multisaveMap
              .getOrElse(upc, Nil)

          val intermediateItem =
            IntermediateItem(upc, itemMultisave)

          total += itemPrice * quantity
          intermediateItems :+ intermediateItem
          mutableItemQuantityMap.update(upc, quantity)
      }

      intermediateItems -> mutableItemQuantityMap
    }

    val discounts =
      intermediateItems.foldLeft(BigDecimal(0)) {
        case (acc, item) =>
          // Keep applying deals for the current item until there are no more eligible deals
          @tailrec
          def keepApplyingBestDiscount(runningDiscount: BigDecimal): BigDecimal = {
            val innerBestDiscount =
              bestDiscount(item.multisave, mutableItemQuantityMap)

            innerBestDiscount match {
              case Some(multisave) =>
                multisave.subtract.foreach {
                  case (upc, quantityToSubtract) =>
                    // Get the current quantity of the items in play for UPCs
                    // that are unaccounted for in a multisave deal.
                    val currentQuantity =
                      mutableItemQuantityMap
                        .getOrElse(upc, throw MultisaveRequirementNotMetException(upc, multisave.id))

                    // Quantity after accounting a certain quantity of
                    // items for the current multisave deal.
                    val newQuantity =
                      currentQuantity - quantityToSubtract

                    // If there isn't enough items to satisfy the requirement do not proceed.
                    if (newQuantity < 0)
                      throw MultisaveRequirementNotMetException(upc, multisave.id)
                    else
                      mutableItemQuantityMap.update(upc, newQuantity)
                }

                keepApplyingBestDiscount(runningDiscount + multisave.discountAmount)

              case None =>
                runningDiscount
            }
          }

          acc + keepApplyingBestDiscount(0)
      }

    BasketTotal(total, discounts)
  }

  // Find the best discount given a set of possible applicable discounts and items to work with
  // Best discount calculated (in that order):
  // - Yields the greatest saving
  // - Requires minimal amount of items
  private def bestDiscount(multisaves: Seq[Multisave], itemsToWorkWith: mutable.Map[UPC, Long]): Option[IntermediateMultisave] = {
    var innerBestDeal: Option[IntermediateMultisave] =
      Option.empty[IntermediateMultisave]

    multisaves.foreach { multisave =>
      // Partition the multisave deal requirements by operator to properly
      // evaluate that the requirements are met.
      val (orRequirements, andRequirements) =
        multisave
          .requirements
          .partition {
            _.operator match {
              case OR =>
                false
              case AND =>
                true
            }
          }

      // Requirement met if the if the item exists in the `itemsToWorkWith` map
      // && the requirement quatity is smaller or equal to the quantity from `itemsToWorkWith` map
      def requirementMet(multiSaveItemRequirements: MultiSaveItemRequirements): Boolean =
        itemsToWorkWith
          .get(multiSaveItemRequirements.upc)
          .fold(false)(_ >= multiSaveItemRequirements.quantity)

      // If we have the choice to pick the OR requirement, we want
      // to abide by the same rules that dictate the best deal, hence
      // the OR requirement that requires the least amount of items wins.
      var bestOrRequirement: Option[MultiSaveItemRequirements] =
        Option.empty[MultiSaveItemRequirements]

      if (orRequirements.nonEmpty) {
        orRequirements.foreach { requirement =>
          // Check to see that the requirement is met and that it is a better requirement than the current one.
          if (requirementMet(requirement) && bestOrRequirement.fold(true)(requirement > _))
            bestOrRequirement =
              Some(requirement)
        }
      }

      // Easy, all AND requirements need to be met
      val andRequirementsMet =
        andRequirements
          .forall(requirementMet)

      // If both the OR & AND requirements are met proceed
      if ((orRequirements.isEmpty || bestOrRequirement.nonEmpty) && andRequirementsMet) {
        // Combine all requirements for this deal
        val aggregateItemRequirements =
          bestOrRequirement
            .fold(andRequirements) { andRequirements :+ _ }
            .map { requirement =>
              requirement.upc -> requirement.quantity
            }

        val intermediateMultisave =
          IntermediateMultisave(multisave.id, aggregateItemRequirements, multisave.discountAmount)

        // Check that there is no current best deal, and that if there is, the current one is a better deal
        if (innerBestDeal.fold(true)(intermediateMultisave > _))
          // Update the best deal
          innerBestDeal =
            Some(intermediateMultisave)
      }
    }

    innerBestDeal
  }
}
