# Give My Secret Client - Go

The page contains details about the Node.js client.

**It's really important that if you set your secret's "return decrypted" setting to TRUE, then you don't have to implement decryption for your secret!**

# Prerequisites

Go interpreter

# Configuration & Run

## Step 1: Install modules

```
go mod init main
go mod tidy
```

## Step 2: Replace the JKS/P12 keystore with yours

Replace the test keystore with your JKS/PKCS12 keystore.

## Step 3: Configure the decryption

You have to specify the same hashing function and padding scheme that you used to encrypt the data on the backend side.

## Step 4: Run the app

```
go run jks/sample_jks.go
```

or

```
go run p12/sample_p12.go
```
