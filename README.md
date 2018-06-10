# Ecomm Multisave

Will find the lowest possible price given a set of active product deals and a basket.
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
      "quantity": "1"
    }
  ]
}
```