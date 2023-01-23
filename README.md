# Give My Secret

Give My Secret is a credential storage application, that stores secrets, passwords and other text based credentials in a secure and reliable way by encrypting them with keystores that you provide. It's easy to configure, numerous ready-to-go configurations available for all supported databases.

| Code QL                                                      | Code coverage                                                | Sonarcloud                                                   |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [![CodeQL](https://github.com/peter-szrnka/give-my-secret/actions/workflows/codeql.yml/badge.svg)](https://github.com/peter-szrnka/give-my-secret/actions/workflows/codeql.yml) | Backend:&nbsp; ![Code coverage](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_give-my-secret-backend&metric=coverage) <br /> Frontend: [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_give-my-secret-frontend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=szrnka-peter_give-my-secret-frontend) | Backend:&nbsp; [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_give-my-secret-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=peter-szrnka_give-my-secret-backend) <br/>Frontend: [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_give-my-secret-frontend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=peter-szrnka_give-my-secret-frontend) |

# Tech stack

- Java 11
- Maven
- Spring Boot 2.7.3
- Angular 12
- Docker
- Flyway (DB migration)

# Usage

Give My Secret is available as a Docker image, you can easily pill it from GitHub Container Registry by running the following command:
> docker pull ghcr.io/peter-szrnka/give-my-secret:latest

To start a new container, you have to set up a bunch of environment properties. I recommend to not start the configuration from scratch, if you open the "**[batch-files](batch-files)**" sub folder, you can find many ready-to-go configurations for all available database providers written in Docker Compose format.

# Supported databases & authentication

## Protocol

- HTTPS (by default)
- HTTP

## DB

- [MariaDb](db/mariadb/README.md)
- [PostgreSql](db/postgresql/README.md)
- [MySql](db/mysql/README.md)
- [MsSql](db/mssql/README.md)

For further information & sample configurations, please check the sub folders under the "db" folder.

## Authentication

- [Database](batch-files/db-authentication)
- [LDAP](batch-files/ldap-authentication)

Sample configurations can be found under "batch-files" folder.

## Encryption

Currently only AES encryption is supported.  In the future this will be configured by an environment property.

## Support matrix

| DB provider |      Db auth       |     ldap auth      |
| ----------- | :----------------: | :----------------: |
| MariaDb     | :white_check_mark: | :white_check_mark: |
| PostgreSql  | :white_check_mark: | :white_check_mark: |
| MySql       | :white_check_mark: | :white_check_mark: |
| MsSql       | :white_check_mark: | :white_check_mark: |

# Code samples

You can find client code examples for Java, Python and Node.js [here](client-samples/README.md).
