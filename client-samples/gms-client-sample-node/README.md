# Give My Secret Client - Node

The page contains details about the Node.js client.

**It's really important that if you set your secret's "return decrypted" setting to TRUE, then you don't have to implement decryption for your secret!**

# Prerequisites

- JKS-JS: https://www.npmjs.com/package/jks-js
- Crypto
- Nest.js (Optional)

# Configuration & Run

## Step 1: Install modules

```
npm install
```

## Step 2: Replace the JKS keystore with yours

Replace the test keystore with your JKS keystore.

## Step 3: Configure the decryption

You have to specify the same hashing function and padding scheme that you used to encrypt the data on the backend side.

## Step 4: Run the app

```
npm run start:dev
```
