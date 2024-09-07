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
|      | **Logging**                       |              |                                               |                                                              |
| 30   | LOG_TYPE                          | String       | console, file, logstash                       | You can configure where should Logback logging library send the logs: to a file, or to Logstash (**You can find more info in [ELK configuration](batch-files/elk-sample-configuration/README.md)**) |
| 31   | LOG_FOLDER                        | String       | -                                             | Output folder of the logs in case of file based logging.     |
| 32   | LOGSTASH_URL                      | String       |                                               | Logstash instance URL. **You can find more info in [ELK configuration](batch-files/elk-sample-configuration/README.md)** |
| 33   | HTTP_CLIENT_LOGGING_ENABLED       | Boolean      | false, true                                   | This flag determines whether HTTP client calls logged or not. |
| 34   | ARCHIVED_LOG_FOLDER               | String       | -                                             | Folder where the archived logs should be moved.              |
|      | **Caching / Redis**               |              |                                               |                                                              |
| 35   | ENABLE_REDIS_CACHE                | Boolean      | false, true                                   | This flag determines whether Redis caching enabled or not.   |
| 36   | REDIS_HOST                        | String       |                                               | Redis host URL.                                              |
| 37   | REDIS_PORT                        | Number       |                                               | Redis host port.                                             |
|      | **Job settings**                  |              |                                               |                                                              |
| 38   | ENABLE_SECRET_ROTATION            | Boolean      | false, true (default true)                    | This flag determines whether secret rotator job enabled or not. |
| 39   | ENABLE_EVENT_MAINTENANCE          | Boolean      | false, true (default true)                    | This flag determines whether event maintenance job enabled or not. |
| 40   | ENABLE_MESSAGE_CLEANUP            | Boolean      | false, true (default true)                    | This flag determines whether message cleanup job enabled or not. |
| 41   | ENABLE_GENERATE_KEYSTORE_CLEANUP  | Boolean      | false, true (default true)                    | This flag determines whether temp keystore cleanup job enabled or not. |
| 42   | ENABLE_LDAP_SYNC                  | Boolean      | false, true (default false)                   | This flag determines whether LDAP sync job enabled or not.   |
| 43   | ENABLE_USER_DELETION              | Boolean      | false, true (default true)                    | This flag determines whether GDPR user deletion job enabled or not. |
|      | **Tracing / Zipkin**              |              |                                               |                                                              |
| 44   | ZIPKIN_URL                        | String       | -                                             | Zipkin host url.                                             |
|      | **Other**                         |              |                                               |                                                              |
| 45   | CONFIG_RESOURCE_HANDLER_DISABLED  | Boolean      | true, false                                   | *This parameter is not necessary for production Docker images.* |
| 46   | COMPOSE_CONVERT_WINDOWS_PATHS     | Number       | 0, 1                                          | Required only by Windows!                                    |

