# Give My Secret credential manager application
## What is Give My Secret?
Sometimes your company has a lot of distributed systems that has it's own credentials and it can be exhausting to manage these passwords. Yes, there're a bunch of out-of-the-box solutions, like AWS Secrets Manager, HashiCorp Vault, CyberArk, etc., but sometimes you might need a simpler and smaller solution :)

Give My Secret( or GMS) helps you to handle these text based passwords in one application. All passwords will be stored in a reliable and secure way, because all credentials will be encrypted (and decrypted) with the keystore that YOU configure for GMS.

GMS provides a simple REST API that you can call with your other systems to query your secrets.

![image](https://user-images.githubusercontent.com/8747465/215268993-9b8c0a67-48bf-45f8-8676-2df79bfaaf2e.png)

## Main features
- Text based secret/password/credential storage
- Complex key/value pairs
- REST API to query the secrets from your local instance
- 4 supported database providers (PostgreSQL, MSSQL, MySQL, MariaDb)
- 2 supported authentications (DB and LDAP)
- 2 supported protocols (HTTPS and HTTP)

## Ingredients
All entity has it's own status, so if one of them is compromised, you can easily disable them.

### Keystore
It will be used to encrypt/decrypt your passwords. You can define multiple aliases in a keystore, so you can select different aliases for different secrets.

### API key
This is kind of an ID for API calls that identifies the unique GMS user. You can create API keys as much as you want.

### Secret
This is the entity where your secrets/passwords/credentials are stored. You can define:
- which keystore + alias you want to use, 
- should the value return encrypted/decrypted in API calls,
- should the value rotated,
- rotation period,
- API key restriction can be added to limit which API key can be used for which secret.

#### Secret types
- Simple credential: One secret can be stored
- Multiple strings: More than one key-value pairs can be stored in this type of secret

### Announcement
As an admin you can publish application maintenance related news by creating announcements.

### Message
In some certain cases system generated messages will be created, for instance: when a keystore has been disabled, all related secrets will be disabled as well.

## Configuration examples
You can find the examples [here](usecases.MD).
