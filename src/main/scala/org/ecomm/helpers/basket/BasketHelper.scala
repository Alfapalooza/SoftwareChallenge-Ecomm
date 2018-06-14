package org.ecomm.helpers.basket

import org.ecomm.controllers.requests.BasketItems
import org.ecomm.models.{ BundleId, Price, UPC }
import org.ecomm.models.basket.{ BasketTotal, Items }
import org.ecomm.models.basket.bundles.{ BundleDiscountItemRequirements, BundleDiscount }
import org.ecomm.models.basket.bundles.BundleDiscountOperator.{ AND, OR }
import org.ecomm.models.responses.exceptions.{ ItemNotFoundException, BundleRequirementNotMetException }

import scala.annotation.tailrec
import scala.collection.mutable

object BasketHelper {
  private case class IntermediateItem(upc: UPC, bundleDiscounts: Seq[BundleDiscount])
  private case class IntermediateBundleDiscount(id: BundleId, subtract: Seq[(UPC, Long)], discountAmount: Price) {
    // In this case, Greater Than, means better than. It's a better bundle discount if:
    // - The discount price is greater
    // - It requires fewer items to satisfy the discount
    def >(intermediateBundleDiscount: IntermediateBundleDiscount): Boolean =
      discountAmount > intermediateBundleDiscount.discountAmount ||
        (discountAmount == intermediateBundleDiscount.discountAmount && subtract.map(_._2).sum <= intermediateBundleDiscount.subtract.map(_._2).sum)

    def <(intermediateBundleDiscount: IntermediateBundleDiscount): Boolean =
      ! >(intermediateBundleDiscount)
  }

  //O(N) with branches on bundle discount and item requirements. So more like O(A + B + C)
  def calculateTotal(basketItems: BasketItems): BasketTotal = {
    var total: BigDecimal =
      0

    val items =
      basketItems
        .items
        // Grouping items in the cart that have the same UPC, but
        // may be separated.
        .groupBy(_.upc)
        .toSeq
        // Since we're not running over the full combination range
        // it's crucial that there be some ordering for discount reproducibility.
        .sortWith(_._1 > _._1)

    // Need mutable quantity Map to subtract items already in
    // play for applied bundleDiscounts discounts.
    val (intermediateItems, mutableItemQuantityMap) = {
      val innerIntermediateItems: mutable.ArrayBuffer[IntermediateItem] =
        mutable.ArrayBuffer.empty[IntermediateItem]

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

          val itemBundleDiscounts =
            BundleDiscount
              .bundleDiscountsMap
              .getOrElse(upc, Nil)

          val intermediateItem =
            IntermediateItem(upc, itemBundleDiscounts)

          total += itemPrice * quantity
          innerIntermediateItems += intermediateItem
          mutableItemQuantityMap.update(upc, quantity)
      }

      innerIntermediateItems -> mutableItemQuantityMap
    }

    val discounts =
      intermediateItems.foldLeft(BigDecimal(0)) {
        case (acc, item) =>
          // Keep applying discounts for the current item until there are no more eligible discounts
          @tailrec
          def keepApplyingBestDiscount(runningDiscount: BigDecimal): BigDecimal = {
            val innerBestDiscount =
              bestDiscount(item.bundleDiscounts, mutableItemQuantityMap)

            innerBestDiscount match {
              case Some(bundleDiscount) =>
                bundleDiscount.subtract.foreach {
                  case (upc, quantityToSubtract) =>
                    // Get the current quantity of the items in play for UPCs
                    // that are unaccounted for in a bundle discount.
                    val currentQuantity =
                      mutableItemQuantityMap
                        .getOrElse(upc, throw BundleRequirementNotMetException(upc, bundleDiscount.id))

                    // Quantity after accounting a certain quantity of
                    // items for the current bundleDiscounts discount.
                    val newQuantity =
                      currentQuantity - quantityToSubtract

                    // If there isn't enough items to satisfy the requirement do not proceed.
                    if (newQuantity < 0)
                      throw BundleRequirementNotMetException(upc, bundleDiscount.id)
                    else
                      mutableItemQuantityMap.update(upc, newQuantity)
                }

                keepApplyingBestDiscount(runningDiscount + bundleDiscount.discountAmount)

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
  private def bestDiscount(bundleDiscounts: Seq[BundleDiscount], itemsToWorkWith: mutable.Map[UPC, Long]): Option[IntermediateBundleDiscount] = {
    var innerBestDiscount: Option[IntermediateBundleDiscount] =
      Option.empty[IntermediateBundleDiscount]

    bundleDiscounts.foreach { bundleDiscount =>
      // Partition the bundle discount requirements by operator to properly
      // evaluate that the requirements are met.
      val (orRequirements, andRequirements) =
        bundleDiscount
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
      def requirementMet(bundleDiscountItemRequirements: BundleDiscountItemRequirements): Boolean =
        itemsToWorkWith
          .get(bundleDiscountItemRequirements.upc)
          .fold(false)(_ >= bundleDiscountItemRequirements.quantity)

      // If we have the choice to pick the OR requirement, we want
      // to abide by the same rules that dictate the best discount, hence
      // the OR requirement that requires the least amount of items wins.
      var bestOrRequirement: Option[BundleDiscountItemRequirements] =
        Option.empty[BundleDiscountItemRequirements]

      orRequirements.foreach { requirement =>
        // Check to see that the requirement is met and that it is a better requirement than the current one.
        if (requirementMet(requirement) && bestOrRequirement.fold(true)(requirement > _))
          bestOrRequirement =
            Some(requirement)
      }

      // Easy, all AND requirements need to be met
      val andRequirementsMet =
        andRequirements
          .forall(requirementMet)

      // If both the OR & AND requirements are met proceed
      if ((orRequirements.isEmpty || bestOrRequirement.nonEmpty) && andRequirementsMet) {
        // Combine all requirements for this discount
        val aggregateItemRequirements =
          bestOrRequirement
            .fold(andRequirements) { andRequirements :+ _ }
            .map { requirement =>
              requirement.upc -> requirement.quantity
            }

        val intermediateBundleDiscount =
          IntermediateBundleDiscount(bundleDiscount.id, aggregateItemRequirements, bundleDiscount.discountAmount)

        // Check that there is no current best discount, and that if there is, the current one is a better discount
        if (innerBestDiscount.fold(true)(intermediateBundleDiscount > _))
          // Update the best discount
          innerBestDiscount =
            Some(intermediateBundleDiscount)
      }
    }

    innerBestDiscount
  }
}
