package main

import (
	"crypto/rsa"
	"crypto/tls"
	"encoding/base64"
	"encoding/json"
	"log"
	"net/http"
	"os"
	"strings"

	"software.sslmate.com/src/go-pkcs12"
)

// CONSTANTS ////////////////////////////////////////////////
const server_url = "https://localhost:8443/api/secret/"
const secret_id = "secret2"
const api_key = "xBpFOEEjfZWpSXSuOXpGoGt3PVvuGTYq"

/////////////////////////////////////////////////////////////

func readKeyStore(filename string, password string) *rsa.PrivateKey {
	p12_data, err := os.ReadFile(filename)
	if err != nil {
		log.Fatal(err)
	}

	key, cert, err := pkcs12.Decode(p12_data, password) // Note the order of the return values.
	if err != nil {
		log.Fatal(err)
	}

	log.Println("- Cert info:")
	log.Printf("    - Subject: %s\n", cert.Subject)

	priv, ok := key.(*rsa.PrivateKey)
	if !ok {
		log.Fatal("P12 keystore entry does not have a private key entry!")
	}

	return priv
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

	rawByteValue, err := base64.RawStdEncoding.DecodeString(value)
	if err != nil {
		log.Fatal("Base64 decode issue occurred!")
	}

	log.Println("- Value Base64 decoded")

	privateKey := readKeyStore("../test.p12", "test")
	log.Println("- P12 keystore with private key entry loaded")
	log.Println("- Decryption started")
	plaintext, err := privateKey.Decrypt(nil, rawByteValue, nil)

	if err != nil {
		log.Fatal(err.Error())
	}

	log.Println("- Decryption finished")

	resultmap := make(map[string]string)

	for _, item := range strings.Split(string(plaintext), ";") {
		values := strings.Split(item, ":")
		resultmap[values[0]] = values[1]
	}

	log.Printf("Decoded value: %s\n\n", resultmap)
}
