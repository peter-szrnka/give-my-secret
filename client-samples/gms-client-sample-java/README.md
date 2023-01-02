# Give My Secret Client - Java

The page contains details about the Java client.

**It's really important that if you set your secret's "return decrypted" setting to TRUE, then you don't have to implement decryption for your secret!**

# Prerequisites

- Java 11
- Maven 3
- JKS/PKCS12 based keystore with an RSA private key

A simple implementation created with Java 11(HTTP client, Keystore readers, etc.)

# Configuration & Run

## Step 1: Install modules

Download & resolve Maven dependencies with your preferred IDE.

## Step 2: Replace the keystore with yours

Replace the test keystore with your keystore.

## Step 3: Configure the decryption

You have to specify the same hashing function and padding scheme that you used to encrypt the data on the backend side.

## Step 4: Run the app

Just simply start **Main.java** as a Java application in your preferred IDE.
