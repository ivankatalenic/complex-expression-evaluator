# Complex Expression Evaluator

Evaluates expressions defined with a custom grammar.
Uses [ANTLR](https://www.antlr.org/index.html) library to generate a parser.
Expressions can use paths to values contained in the provided JSON document.

Implementation details:
- Java 21
- Spring Boot Framework
- JPA with H2 database
- Maven

## Endpoints
- `POST /expression`
    ```json
    {
      "name": "Expression name",
      "value": "user.last_name == \"Katalenić\""
    }
    ```
- `GET /expressions`
- `POST /evaluate?id=<expression_id>`
    ```json
    {
      "json document containing values": true,
      "user": {
        "first_name": "Ivan",
        "last_name": "Katalenić",
        "addresses": [
          {
            "label": "home",
            "address": "127.0.0.1"
          }
        ]
      }
    }
    ```

## Run

```shell
mvn clean spring-boot:run
```

---
_WIP_