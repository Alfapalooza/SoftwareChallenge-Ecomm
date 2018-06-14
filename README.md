# Ecomm Bundle Discount

Will find the lowest possible price given a set of active product bundle discount and a basket.
Based off Byrde AkkaHttp Seed project, see: https://github.com/Byrde/akka-http-seed

## Running

Run this using [sbt](akka.http://www.scala-sbt.org/).

```
sbt run
```

And then go to akka.http://localhost:8080 to see the running web application.

To calculate totals *POST* to `/basket/total`, request body schema:
```
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "definitions": {
    "item": {
      "properties": {
        "upc": {
          "type": "string"
        },
        "quantity": {
          "type": "number"
        }
      },
      "required": ["upc", "quantity"],
      "type": "object"
    }
  },
  "properties": {
    "items": {
      "items": {
        "allOf": [
          {
            "$ref": "#/definitions/item"
          }
        ]
      },
      "type": "array"
    }
  },
  "required": ["items"],
  "type": "object"
}
```

Sample:
```
{
  "items": [
    {
      "upc": "1234567890",
      "quantity": 1
    }
  ]
}
```

All items:
```
1234567890
0987654321
2345678901
9876532101
3456789012
8765321098
4567801234
7890123456
5678901234
6789012345
```

## Adding items & bundles

- To add items go to ```Items.scala```, under ```val priceMap: Map[UPC, Price]``` add a new entry with desired UPC and Price.

- To add a bundle go to ```BundleDiscount.scala```, under ```private val bundleDiscountList: Seq[BundleDiscount]``` add a new entry, this consists of:
    - Creating a sequence of ```BundleDiscountItemRequirements``` objects. The first field is the upc this requirement applies to,
    the second field is the quantity required to meet this requirement, and the last field is the operator this indicates
    whether this requirement is an OR or AND with other requirements (defaults to ```BundleDiscountOperator.OR```)
    - Creating a new ```BundleDiscount``` object, the first field is the one mentioned above, the second is the discount amount
    in the case that the bundle discount requirements are met.

## Notes

- This implementation does not run the full combination range because of the factorial complexity. Rather we sort the items
and calculate the best deal for each in order, this makes sure that the discounts are consistent when applied since the discounts
are calculated in the same order, even when the cart items are shuffled. In almost all cases it will return the best possible discount,
except in highly complex baskets with lots of different overlapping potential discounts.

- When faced with multiple OR condition requirements we could do a better job of calculating the best one to choose. In
most cases the current implementation will do fine, but like above, to pick the absolute best OR condition would require
running through the full range of combinations to know for sure.
