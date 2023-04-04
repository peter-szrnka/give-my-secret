package main

import (
	"crypto/rsa"
	"crypto/tls"
	"crypto/x509"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"

	"github.com/pavel-v-chernykh/keystore-go/v4"
)

func readKeyStore(filename string, password []byte) keystore.KeyStore {
	f, err := os.Open(filename)
	if err != nil {
		log.Fatal(err)
	}

	defer func() {
		if err := f.Close(); err != nil {
			log.Fatal(err)
		}
	}()

	ks := keystore.New()
	if err := ks.Load(f, password); err != nil {
		log.Fatal(err) // nolint: gocritic
	}

	return ks
}

func getHttpResponse() string {
	http.DefaultTransport.(*http.Transport).TLSClientConfig = &tls.Config{InsecureSkipVerify: true}
	client := &http.Client{}
	req, _ := http.NewRequest("GET", "https://localhost:8443/api/secret/secret1", nil)

	req.Header.Add("x-api-key", "xBpFOEEjfZWpSXSuOXpGoGt3PVvuGTYq")

	res, _ := client.Do(req)

	var data map[string]string
	err := json.NewDecoder(res.Body).Decode(&data)
	if err != nil {
		fmt.Printf("client: could not read response body: %s\n", err)
		os.Exit(1)
	}
	fmt.Printf("value: %s\n", data["value"])

	return data["value"]
}

func main() {
	value := string(getHttpResponse())
	fmt.Println("- Value loaded from server")
	ks := readKeyStore("D:/dev/projects/open-source/github/give-my-secret/client-samples/gms-client-sample-go/test.jks", []byte("test"))
	fmt.Println("- JKS keystore loaded")

	pke, err := ks.GetPrivateKeyEntry("test", []byte("test"))
	if err != nil {
		log.Fatal(err) // nolint: gocritic
	}

	fmt.Println("- Entry found in keystore")

	parsedKey, err := x509.ParsePKCS8PrivateKey(pke.PrivateKey)
	if err != nil {
		log.Fatal(err.Error())
	}

	fmt.Println("- Entry parsed")

	var privateKey *rsa.PrivateKey
	var ok bool
	privateKey, ok = parsedKey.(*rsa.PrivateKey)
	if !ok {
		log.Printf("Unable to parse RSA private key, generating a temp one : %s", err.Error())
	}

	fmt.Println("- Private key loaded")

	fmt.Println("- Decryption started")

	plaintext, err := privateKey.Decrypt(nil, []byte(value), nil /*&rsa.OAEPOptions{Hash: crypto.SHA512_256}*/)

	if err != nil {
		log.Fatal(err.Error())
	}

	fmt.Println("- Decryption finished")
	log.Print(string(plaintext))
}
