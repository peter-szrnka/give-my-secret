package main

import (
	"crypto/rsa"
	"crypto/tls"
	"crypto/x509"
	"encoding/base64"
	"encoding/json"
	"log"
	"net/http"
	"os"

	"github.com/pavel-v-chernykh/keystore-go/v4"
)

// CONSTANTS ////////////////////////////////////////////////
const server_url = "https://localhost:8443/api/secret/"
const secret_id = "secret1"
const api_key = "xBpFOEEjfZWpSXSuOXpGoGt3PVvuGTYq"

/////////////////////////////////////////////////////////////

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
	req, _ := http.NewRequest("GET", server_url+secret_id, nil)
	req.Header.Add("x-api-key", api_key)

	res, _ := client.Do(req)

	var data map[string]string
	err := json.NewDecoder(res.Body).Decode(&data)
	if err != nil {
		log.Printf("client: could not read response body: %s\n", err)
		os.Exit(1)
	}

	log.Printf("- Value received from server: %s\n", data["value"])
	return data["value"]
}

func main() {
	value := string(getHttpResponse())
	log.Println("- Value loaded from server")

	rawByteValue, _ := base64.RawStdEncoding.DecodeString(value)
	log.Println("- Value Base64 decoded")

	ks := readKeyStore("test.jks", []byte("test"))
	log.Println("- JKS keystore loaded")

	if !ks.IsPrivateKeyEntry("test") {
		log.Fatal("This entry does not contain a private key")
	}

	pke, err := ks.GetPrivateKeyEntry("test", []byte("test"))
	if err != nil {
		log.Fatal(err) // nolint: gocritic
	}

	log.Println("- Entry found in keystore")

	parsedKey, err := x509.ParsePKCS8PrivateKey(pke.PrivateKey)
	if err != nil {
		log.Fatal(err.Error())
	}

	log.Println("- Entry parsed")

	var privateKey *rsa.PrivateKey
	var ok bool
	privateKey, ok = parsedKey.(*rsa.PrivateKey)
	if !ok {
		log.Printf("Unable to parse RSA private key, generating a temp one : %s", err.Error())
	}

	log.Println("- Private key loaded")
	log.Println("- Decryption started")
	plaintext, err := privateKey.Decrypt(nil, rawByteValue, nil)

	if err != nil {
		log.Fatal(err.Error())
	}

	log.Println("- Decryption finished")
	log.Printf("Decoded value: %s\n\n", string(plaintext))
}
