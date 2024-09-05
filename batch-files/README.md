# Give My Secret configuration examples

Here are some samples how you can configure Give My Secret with numerous databases and authentication providers.

# Scenario 1: PostgreSQL with database authentication

Prerequisite:

- Preconfigure a self-signed keystore (<u>details can be found in "HTTPS Configuration" section!</u>)

TODO

# Scenario 2: MySQL with LDAP authentication

Prerequisite:

- Preconfigure a self-signed keystore (<u>details can be found in "HTTPS Configuration" section!</u>)

TODO

# Scenario 3: Keycloak SSO authentication with PostgreSQL database

Prerequisite:

- Preconfigure a self-signed keystore (<u>details can be found in "HTTPS Configuration" section!</u>)

TODO

# Scenario N+1: Setting up with Kubernetes on Windows

Prerequisite:

- Preconfigure a self-signed keystore (<u>details can be found in "HTTPS Configuration" section!</u>)

TODO

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
