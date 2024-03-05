# Give My Secret (under development!)

Give My Secret is a credential storage application, that stores secrets, passwords and other text based credentials in a secure and reliable way by encrypting them with keystores that you provide. It's easy to configure, numerous ready-to-go configurations available for all supported databases.

| Code QL                                                      | Code coverage                                                | Sonarcloud                                                   |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [![CodeQL](https://github.com/peter-szrnka/give-my-secret/actions/workflows/codeql.yml/badge.svg)](https://github.com/peter-szrnka/give-my-secret/actions/workflows/codeql.yml) | Backend:&nbsp;&nbsp;&nbsp; ![Code coverage](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_give-my-secret-backend&metric=coverage) <br /> Frontend:&nbsp;&nbsp; [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_give-my-secret-frontend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=szrnka-peter_give-my-secret-frontend) | Backend:&nbsp;&nbsp;&nbsp; [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_give-my-secret-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=peter-szrnka_give-my-secret-backend) <br/>Frontend:&nbsp;&nbsp; [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=peter-szrnka_give-my-secret-frontend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=peter-szrnka_give-my-secret-frontend) |

# Tech stack

- Java 21 (Eclipse Temurin)
- Maven
- Spring Boot 3.2.3
- Angular 15
- Docker
- Flyway (DB migration)

# Usage

Give My Secret is available as a Docker image, you can easily pull it from GitHub Container Registry by running the following command:
> docker pull ghcr.io/peter-szrnka/give-my-secret:latest

To start a new container, you have to set up a bunch of environment properties. I recommend to not start the configuration from scratch, if you open the "**[batch-files](batch-files)**" sub folder, you can find many ready-to-go configurations for all available database providers written in Docker Compose format.

# Supported

## Protocol

- HTTPS (by default)
- HTTP

## DB

- [MariaDb](db/mariadb/README.md)
- [PostgreSql](db/postgresql/README.md)
- [MySql](db/mysql/README.md)
- [MsSql](db/mssql/README.md)

For further information & sample configurations, please check the sub folders under the "db" folder.

## Caching

2 types of caches are provided:

- Built-in cache
- Redis

## Authentication

- [Database](batch-files/db-authentication)
- [LDAP](batch-files/ldap-authentication)

Sample configurations can be found under "batch-files" folder.

## Two Factor Authentication
The following authenticator applications are supported:
- Google Authenticator
- Microsoft Authenticator

## Encryption

Currently only AES encryption is supported.  In the future this will be configured by an environment property.

## Support matrix

| DB provider |      Db auth       |     ldap auth      |
| ----------- | :----------------: | :----------------: |
| MariaDb     | :white_check_mark: | :white_check_mark: |
| PostgreSql  | :white_check_mark: | :white_check_mark: |
| MySql       | :white_check_mark: | :white_check_mark: |
| MsSql       | :white_check_mark: | :white_check_mark: |

## Observation
- Zipkin
- Prometheus (with Grafana)

# Code samples

You can find client code examples for Java, Go, Python and Node.js [here](client-samples/README.md).


# Donate
[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7YEPKTQRNK5YA)
