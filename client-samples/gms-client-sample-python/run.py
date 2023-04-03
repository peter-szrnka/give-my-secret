import base64
import http.client
import json
import textwrap

from Crypto import Random
from Crypto.Cipher import PKCS1_v1_5
from Crypto.PublicKey import RSA
from javaobj.utils import to_str
import jks
from jks.jks import PrivateKeyEntry

secret_id = 'secret1'
api_key = 'P2jFEjUs0KtHZjBOiFWUlrmw38NRI1J2'

def get_encrypted_value():
    connection = http.client.HTTPConnection('localhost', 8080, timeout=10)
    connection.request("GET", "/api/secret/" + secret_id, headers ={"X-API-KEY" : api_key})
    
    httpresponse = connection.getresponse()
    response_body = httpresponse.read()
    
    response : str = base64.b64decode(json.loads(response_body.decode())['value'] + "==")
    print("Status: ", format(httpresponse.status))
    
    connection.close()
    return response

def load_private_key():
    keystore = jks.KeyStore.load('test.jks', 'test')
    private_key_entry: PrivateKeyEntry = keystore.private_keys['test']

    if not private_key_entry.is_decrypted():
        private_key_entry.decrypt("test")
        
    return private_key_entry

def format_pem(der_bytes, key_type="PRIVATE KEY"):
    return "\n".join([
        "-----BEGIN %s-----" % key_type,
        "\n".join(textwrap.wrap(base64.b64encode(der_bytes).decode("utf-8"), 64)),
        "-----END %s-----\n" % key_type
    ])


my_encrypted_value = get_encrypted_value()
priv_key = load_private_key()

rsa_key = RSA.importKey(format_pem(priv_key._pkey_pkcs8))
cipher = PKCS1_v1_5.new(rsa_key)
print("Decrypted value: ", to_str(cipher.decrypt(my_encrypted_value, Random.new().read(256))))