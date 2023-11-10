# Complex Expression Evaluator

API that evaluates expressions defined with a custom grammar.
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
		"name": "Last name and home implies 127.0.0.1",
		"value": "user.last_name == \"Katalenić\" && (user.addresses[0].label != \"home\" OR user.addresses[0].address == \"127.0.0.1\")"
	}
    ```
- `GET /expressions`
- `POST /evaluate?expressionId=<expression_id>`
	```json
	{
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

Swagger UI for the OpenAPI documentation is available at http://localhost:8080/swagger-ui/index.html, 
while documentation in JSON format is available at http://localhost:8080/api-docs.

## Commands

### Run

```shell
mvn clean spring-boot:run
```

### Generate the parser code

```shell
mvn generate-sources
```

This isn't needed specifically as it will be done automatically when running. The code will be in the `target` directory.
