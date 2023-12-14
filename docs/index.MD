# Give My Secret credential manager application
## What is Give My Secret?
Sometimes your company has a lot of distributed systems that has it's own credentials and it can be exhausting to manage these passwords. Yes, there're a bunch of out-of-the-box solutions, like AWS Secrets Manager, HashiCorp Vault, CyberArk, etc., but sometimes you might need a simpler and smaller solution :)

<u>Give My Secret( or GMS) helps you to handle these text based passwords in one application.</u> All passwords will be stored in a reliable and secure way, because all credentials will be encrypted (and decrypted) with the keystore that YOU configure.

GMS provides a simple REST API that you can call with your other systems to query your secrets.

![image](https://user-images.githubusercontent.com/8747465/215268993-9b8c0a67-48bf-45f8-8676-2df79bfaaf2e.png)

## Main features
- Text based secret/password/credential storage
- Simple of even complex key/value pairs can be stored
- REST API to query the secrets from your local instance
- 4 supported database providers (PostgreSQL, MSSQL, MySQL, MariaDb)
- 2 supported authentications (DB and LDAP)
- 2 supported protocols (HTTPS and HTTP)
- 2FA supported

## Basic ingredients
Let's talk about what are the key parts of Give My Secret application, let's call these parts "entities". All entity has it's own status, it can be ACTIVE or BLOCKED, so if one of them is compromised, you can easily disable them. For instance your API key has disclosed, then you can immediately block it in GMS, then nobody can use it.

### Keystore
A keystore will be used to encrypt/decrypt your passwords. You can define multiple aliases in a keystore, so you can configure different aliases(private+public keys) for different secrets.

**<u>TODO Explain expiration</u>**

### API key
With an API key you can identify your user in a request to GMS. You can create API keys as much as you want, then you can assign these keys to secrets to limit the usage, which API key can be used for which secret.

### Secret
This is the entity where your secrets/passwords/credentials are stored. You can define:
- which keystore + alias you want to use, 
- should the value return encrypted/decrypted in API calls,
- should the value rotated,
- rotation period,
- API key restriction can be added to limit which API key can be used for which secret.

#### Secret types
- Simple credential: One text based credential can be stored
- Multiple strings: More than one key-value pairs can be stored in this type of secret, for instance database credentials (username + password)

## Admin features

### Announcement

As an admin you can publish application maintenance related news by creating announcements, then it'll appear on the user's dashboard.

### Message
In some certain cases system generated messages will be created, for instance: when a keystore has been disabled, all related secrets will be disabled as well.

### Event

Many functions are observed by the application and event records will be created, and these can be browsed by administrators, like: a user changed/created/deleted a keystore. Events time-to-live can be customized by system properties.

### System properties

You can customize some of the features of Give My Secret:

- Access and/or Refresh JWT expiration time
- Organization name
- Time limit for deleting old events
- Enable MFA for users
- Enable global MFA
- etc.

## Configuration examples
You can find the examples [here](usecases.MD).
