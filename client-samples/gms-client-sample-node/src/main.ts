import fs from 'fs';
import jks from 'jks-js';
import crypto from 'crypto';

const BASE_URL : string = "https://localhost:8443/api/secret/";
// Your variables //////////////////////////////////////////////////
const SECRET_ID1 : string = "secret1";
const SECRET_ID2 : string = "secret2";
const API_KEY : string = "xBpFOEEjfZWpSXSuOXpGoGt3PVvuGTYq";
////////////////////////////////////////////////////////////////////

const fetchApi = (secretId: string, filename: string) => {
    fetch(`${BASE_URL}${secretId}`, { method: 'GET', headers: { "Content-Type": "application/json",'x-api-key': API_KEY }})
        .then(async (resp: any) => {
          const response = await resp;
          if (response.status !== 200) {
            throw new Error('Invalid response!');
          }

          const value = (await response.json())['value'];
          let decryptedData : Buffer = decryptData(value, filename);
          console.info("Finally decrypted value is = ", decryptedData.toString('utf8'));
        });
};

const decryptData = (value : string, filename : string) : Buffer => {
    const keystore = jks.toPem(fs.readFileSync(filename), 'test');
    const key = keystore['test'].key;
    const decryptedData = crypto.privateDecrypt(
      {
        key: key,
        passphrase: 'test',
        padding: crypto.constants.RSA_PKCS1_PADDING
      },
      Buffer.from(value, "base64"),
    );

    return decryptedData;
};

fetchApi(SECRET_ID1, 'test.jks');
fetchApi(SECRET_ID2, 'test.p12');