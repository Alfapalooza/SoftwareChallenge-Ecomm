# Ecomm Multisave

This is a starter application that gives you everything you need to start building an AkkaHttp application.
Based off Byrde AkkaHttp Seed project, see: https://github.com/Byrde/akka-akka.http-seed

## Running

Run this using [sbt](akka.http://www.scala-sbt.org/).

```
sbt run
```

And then go to akka.http://localhost:8080 to see the running web application.

To calculate totals POST to `/basket/total`, request body schema:
```
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "definition": {
      "item": {
          "properties": {
              "upc": {
                  "type": "string"
              },
              "quantity": {
                  "type": "number"
              }
          },
          "required": ["upc", "quantity"]
          "type": "object"
      }
  }
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