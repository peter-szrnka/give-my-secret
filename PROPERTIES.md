# Environment properties

Give My Secret (GMS) provides more than 40 external properties that you can use to customize your GMS instance. In the following list you can find all available environment properties:

| #    | Name                              | Type         | Possible values                               | Description                                                  |
| ---- | --------------------------------- | ------------ | --------------------------------------------- | ------------------------------------------------------------ |
| 1    | CONTAINER_HOST_TYPE               | String       | DOCKER, KUBERNETES, OPENSHIFT, SWARM, UNKNOWN | Running container host type. It's necessary to determine the container ID. |
| 2    | SELECTED_DB                       | String       | postgres, mysql, mssql, db2, oracle, mariadb  | Preferred database.                                          |
| 3    | SELECTED_AUTH                     | String       | db, ldap, keycloak-sso                        | Selected authentication mode.                                |
| 4    | SPRING_PROFILES_ACTIVE            | List(String) | ${SELECTED_DB}, ${SELECTED_AUTH}, https       | The list of active Spring profiles. (If you do not need HTTPS, you can remove "https" from the list.) |
|      | **Keystore settings**             |              |                                               |                                                              |
| 5    | HTTPS_PORT                        | String       | 8443 (by default)                             | Optional parameter to customize HTTPS port.                  |
| 6    | SSL_KEYSTORE_TYPE                 | String       | PKCS12 or JKS                                 | Type of the keystore                                         |
| 7    | SSL_KEYSTORE_PATH                 | String       | /usr/share/ssl/                               | Path of the keystore in the Docker container.                |
| 8    | SSL_KEYSTORE                      | String       | -                                             | Exact file name of the keystore.                             |
| 9    | SSL_KEYSTORE_PASSWORD             | String       | -                                             | Keystore password.                                           |
| 10   | SSL_KEYSTORE_ALIAS                | String       | -                                             | Keystore entry alias.                                        |
| 11   | KEYSTORE_PATH                     | String       | /usr/share/keystore/                          | -                                                            |
| 12   | KEYSTORE_TEMP_DEFAULT_PATH        | String       | -                                             | Path for temporarily generated keystores.                    |
|      | **JWT generation & verification** |              |                                               |                                                              |
| 13   | CONFIG_SECRET_JWT                 | String       | -                                             | A Base64 encoded string that is used to verify and generate JWT tokens. |
| 14   | CONFIG_SECRET_CRYPTO              | String       | -                                             | Encryption secret value that is used to verify and generate keystores. |
| 15   | CONFIG_ENCRYPTION_IV              | String       | -                                             | Initialization Vector(IV) value for encryption.              |
|      | **LDAP**                          |              |                                               |                                                              |
| 16   | LDAP_BASE_DN                      | String       | -                                             | LDAP base DN.                                                |
| 17   | LDAP_URL                          | String       | -                                             | LDAP host URL.                                               |
| 18   | LDAP_USERNAME                     | String       | -                                             | LDAP username.                                               |
| 19   | LDAP_CREDENTIAL                   | String       | -                                             | LDAP password.                                               |
| 20   | LDAP_PASSWORD_ENCODER             | String       | CRYPT (default), PLAIN_TEXT                   | The name of the used password encoder to decode credentials stored in LDAP. |
|      | **Keycloak SSO**                  |              |                                               |                                                              |
| 21   | KEYCLOAK_BASE_URL                 | String       |                                               | Keycloak host base URL                                       |
| 22   | KEYCLOAK_REALM                    | String       |                                               | Keycloak realm.                                              |
| 23   | KEYCLOAK_CLIENT_ID                | String       |                                               | Client identifier for Keycloak.                              |
| 24   | KEYCLOAK_CLIENT_SECRET            | String       |                                               | Client secret for Keycloak.                                  |
|      | **Database settings**             |              |                                               |                                                              |
| 25   | DB_SHOW_SQL                       | Boolean      | true, false                                   | This flag determines whether the executed SQL queries displayed in logs |
| 26   | DB_JDBC_URL                       | String       | -                                             | Database URL in JDBC format.                                 |
| 27   | DB_USERNAME                       | String       | -                                             | Database user.                                               |
| 28   | DB_CREDENTIAL                     | String       | -                                             | Database user credentials                                    |
| 29   | DB_MAX_LIFETIME                   | Number       | 580000 (by default)                           |                                                              |
|      | **Hikari Connection Pool**        |              |                                               |                                                              |
| 30   | DS_HIKARI_MAX_POOL_SIZE           | Number       | 10 (by default)                               | Determines the maximum number of connections that the HikariCP pool can maintain. |
| 31   | DS_HIKARI_MIN_IDLE                | Number       | 10 (by default)                               | Specifies the minimum number of idle connections that HikariCP tries to maintain in the pool. |
| 32   | DS_HIKARI_IDLE_TIMEOUT            | Number       | 60000 (by default)                            | The maximum amount of time (in milliseconds) that a connection is allowed to sit idle in the pool. |
| 33   | DS_HIKARI_MAX_LIFETIME            | Number       | 1800000 (by default)                          | The maximum lifetime (in milliseconds) of a connection in the pool. |
| 34   | DS_HIKARI_CONNECTION_TIMEOUT      | Number       | 30000 (by default)                            | The maximum time (in milliseconds) that a client will wait for a connection to become available before throwing an exception. |
|      | **Logging**                       |              |                                               |                                                              |
| 35   | LOG_TYPE                          | String       | console, file, logstash                       | You can configure where should Logback logging library send the logs: to a file, or to Logstash (**You can find more info in [ELK configuration](batch-files/elk-sample-configuration/README.md)**) |
| 36   | LOG_FOLDER                        | String       | -                                             | Output folder of the logs in case of file based logging.     |
| 37   | LOGSTASH_URL                      | String       |                                               | Logstash instance URL. **You can find more info in [ELK configuration](batch-files/elk-sample-configuration/README.md)** |
| 38   | HTTP_CLIENT_LOGGING_ENABLED       | Boolean      | false, true                                   | This flag determines whether HTTP client calls logged or not. |
| 39   | ARCHIVED_LOG_FOLDER               | String       | -                                             | Folder where the archived logs should be moved.              |
| 40   | REQUEST_LOGGING_ENABLED           | Boolean      | false (default), true                         | The flag determines whether logging of incoming request bodies enabled or not. |
| 41   | RESPONSE_LOGGING_ENABLED          | Boolean      | false (default), true                         | The flag determines whether logging of outgoing response bodies enabled or not. |
| 42   | SENSITIVE_DATA_MASKING_ENABLED    | Boolean      | false, true (default)                         | The flag determines whether the sensitive data masking in request and response bodies enabled or not. |
| 43   | RESPONSE_TIME_LOGGING_DISABLED    | Boolean      | false, true (default)                         | The flag determines whether the response time is logged or not. |
| 44   | ENABLE_DETAILED_AUDIT             | Boolean      | false (default), true                         | The flag determines whether the detailed audit is enabled or not. |
|      | **Caching / Redis**               |              |                                               |                                                              |
| 45   | ENABLE_REDIS_CACHE                | Boolean      | false, true                                   | This flag determines whether Redis caching enabled or not.   |
| 46   | REDIS_HOST                        | String       |                                               | Redis host URL.                                              |
| 47   | REDIS_PORT                        | Number       |                                               | Redis host port.                                             |
|      | **Tracing / Zipkin**              |              |                                               |                                                              |
| 48   | ZIPKIN_URL                        | String       | -                                             | Zipkin host url.                                             |
|      | **Other**                         |              |                                               |                                                              |
| 49   | CONFIG_RESOURCE_HANDLER_DISABLED  | Boolean      | true, false                                   | *This parameter is not necessary for production Docker images.* |
| 50   | COMPOSE_CONVERT_WINDOWS_PATHS     | Number       | 0, 1                                          | Required only by Windows!                                    |

