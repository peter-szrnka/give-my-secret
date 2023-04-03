# Give My Secret Client - Python

# INFO: This client is not working currently due to the pyjks and Crypto dependency issues!

The page contains details about the Python client.

**It's really important that if you set your secret's "return decrypted" setting to TRUE, then you don't have to implement decryption for your secret!**

# Prerequisites

- Python >= 3
- PyJKS: https://pypi.org/project/pyjks/
- JKS based keystore with an RSA private key

Currently only JKS based keystores are supported in GMS, so PyJKS is mandatory to use!

# Configuration & Run

## Step 1: Install dependencies

```
pip install pyjks
pip install crypto
```

## Step 2: Replace the JKS keystore with yours

Replace the test keystore with your JKS keystore.

## Step 3: Configure the decryption

You have to specify the same hashing function and padding scheme that you used to encrypt the data on the backend side.

## Step 4: Run the app

Run, run!!!