# Give My Secret configuration examples

Here are some samples how you can configure Give My Secret with numerous databases and authentication providers.

# Use case 1: PostgreSQL with database authentication

Prerequisite:

- Preconfigure a self-signed keystore (<u>details can be found in "HTTPS Configuration" section!</u>)
- A running PostgreSQL database instance (sample configuration can be found [**HERE**](../db/postgresql/README.md))

In order to run a GMS instance with PostgresSQL and db based authentication, you can use the preconfigured

- [env file](db-authentication/env-postgresql.txt),
- [Docker compose file](db-authentication/docker-compose.yml),
- [Windows batch file](db-authentication/compose-app-postgresql-standalone.bat)

that you can find in the [**db-authentication**](db-authentication/) folder.

# Use case 2: PostgreSQL with LDAP authentication

Prerequisite:

- Preconfigure a self-signed keystore (<u>details can be found in "HTTPS Configuration" section!</u>)
- A running PostgreSQL database instance (sample configuration can be found [**HERE**](../db/postgresql/README.md))

In order to run a GMS instance with PostgresSQL and LDAP based authentication, you can use the preconfigured

- [env file](ldap-authentication/env-postgresql-and-ldap.txt),
- [Docker compose file](ldap-authentication/docker-compose.yml),
- [Windows batch file](ldap-authentication/compose-app-postgresql-and-ldap.bat)

that you can find in the [**ldap-authentication**](ldap-authentication/) folder.

# Use case 3: Keycloak SSO authentication with PostgreSQL database

Prerequisite:

- Preconfigure a self-signed keystore (<u>details can be found in "HTTPS Configuration" section!</u>)
- A running PostgreSQL database instance (sample configuration can be found [**HERE**](../db/postgresql/README.md))

The simplest way to get a development-ready Keycloak instance is to run the following command:

> docker run --name keycloak -p 7000:8080 -d -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:24.0.1 start-dev

In order to run a GMS instance with PostgresSQL and Keycloak based authentication, you can use the preconfigured

- [env file](keycloak-sso/env.txt),
- [Docker compose file](keycloak-sso/docker-compose.yml),
- [Windows batch file](keycloak-sso/compose-app.bat)

that you can find in the [**keycloak-sso**](keycloak-sso/) folder.

# Use case 4: ELK stack configuration

Please read the separate **[guide](elk-sample-configuration/README.md)** how to configure an ELK stack for Give My Secret application!

# Use case 5: Redis caching

Prerequisite:

- Preconfigure a self-signed keystore (<u>details can be found in "HTTPS Configuration" section!</u>)
- A running PostgreSQL database instance (sample configuration can be found [**HERE**](../db/postgresql/README.md))

The simplest way to get a development-ready Redis instance is to run the following command:

> docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest

In order to run a GMS instance with PostgresSQL and Redis caching, you can use the preconfigured

- [env file](redis-cache/env-postgresql-with-redis.txt),
- [Docker compose file](redis-cache/docker-compose.yml),
- [Windows batch file](redis-cache/compose-app-postgresql-with-redis-standalone.bat)

that you can find in the [**redis-cache**](redis-cache/) folder.

# Use case N+1: Setting up with Kubernetes on Windows

Prerequisite:

- Preconfigure a self-signed keystore (<u>details can be found in "HTTPS Configuration" section!</u>)
- A running PostgreSQL database instance (sample configuration can be found [**HERE**](../db/postgresql/README.md))

Let's open the [**k8s-windows-configuration**](k8s-windows-configuration/) folder, and run the following commands in alphabetical order:

> kubectl apply -f kubernetes-001-volume-keystores-local.yml

> kubectl apply -f kubernetes-002-volume-pvc.yml

> kubectl apply -f kubernetes-003-deployment-local.yml

> kubectl apply -f kubernetes-004-service-local.yml

> kubectl apply -f kubernetes-005-network-policy.yml

To remove this setup, run these commands:

> kubectl delete -f kubernetes-003-deployment-local.yml

> kubectl delete -f kubernetes-005-network-policy.yml

> kubectl delete -f kubernetes-004-service-local.yml

> kubectl delete -f kubernetes-002-volume-pvc.yml

> kubectl delete -f kubernetes-001-volume-keystores-local.yml

# HTTPS Configuration

To make the application HTTPS ready, you need a keystore that will be loaded by the application. To create a new one, run the following command:

> keytool -genkeypair -alias gms -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore gms.p12 -validity 3650

You need to enter a keystore password, and a few more additional parameters:

![keystore1](assets/keystore1.png)

With this command you generated a self-signed certificate that will be valid for 10 years.

To use the keystore, you have to configure it with a few environment properties (you can find a sample in all batch file configurations).

> SSL_KEYSTORE_TYPE=PKCS12
> SSL_KEYSTORE_PATH=/usr/share/ssl/
> SSL_KEYSTORE=${SSL_KEYSTORE_PATH}test.p12
> SSL_KEYSTORE_PASSWORD=$your_password$
> SSL_KEYSTORE_ALIAS=$alias$
