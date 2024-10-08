# Give My Secret - Client samples

The page contains the list of available samples for several programming languages.

**It's really important that if you set your secret's "return decrypted" setting to TRUE, then you don't have to implement decryption for your secret!**

# Java

**IMPORTANT: For Java I recommend to use the official Maven artifact**!

## Prerequisites

- Java 17
- Maven 3
- JKS/PKCS12 based keystore with an RSA private key

Client implementations:

- **[Official Maven client library example](gms-client-sample-java-client-lib/README.md)**
- [Pure Java client sample (DEPRECATED)](gms-client-sample-java/README.md)

# Python

### Prerequisites

- Python >= 3
- PyJKS: https://pypi.org/project/pyjks/
- JKS/PKCS12 based keystore with an RSA private key

[Details](gms-client-sample-python/README.md)

# Node.js

### Prerequisites

- JKS-JS: https://www.npmjs.com/package/jks-js (In case of a JKS based keystore)
- Crypto
- Nest.js (Optional)

[Details](gms-client-sample-node/README.md)

# Go

### Prerequisites

- Go 1.x+ interpreter
- JKS/PKCS12 based keystore with an RSA private key

[Details](gms-client-sample-go/README.md)
