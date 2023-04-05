import { Injectable } from '@nestjs/common';
import fs from 'fs';
import jks from 'jks-js';
import crypto from 'crypto';
import { HttpService } from 'nestjs-http-promise';

const BASE_URL : string = "https://localhost:8443/api/secret/";
// Your variables //////////////////////////////////////////////////
const SECRET_ID1 : string = "secret1";
const SECRET_ID2 : string = "secret2";
const SECRET_ID3 : string = "secret3";
const API_KEY : string = "xBpFOEEjfZWpSXSuOXpGoGt3PVvuGTYq";
////////////////////////////////////////////////////////////////////

@Injectable()
export class AppService {

  constructor(private readonly httpService: HttpService) {}

  async getSimpleStringValue(filename : string): Promise<any> {
    const httpResponse = await this.getHttpResponse(SECRET_ID1);

    if (httpResponse.status != 200) {
      return Promise.reject("Failure!");
    }

    const decryptedData : Buffer = this.decryptData(httpResponse.data.value, filename);
    return Promise.resolve(decryptedData.toString("utf-8"));
  }

  async getMultipleStringValues(filename : string): Promise<any> {
    const httpResponse = await this.getHttpResponse(SECRET_ID3);

    if (httpResponse.status != 200) {
      return Promise.reject("Failure!");
    }

    if (!httpResponse.data.type) {
      return Promise.resolve(httpResponse.data);
    }

    const decryptedData : Buffer = this.decryptData(httpResponse.data.value, filename);
    const valueString = decryptedData.toString("utf-8");
    let finalObj = {};

    valueString.split(";").forEach(item => {
      let elements = item.split(":");
      finalObj[elements[0]] = elements[1];
    });

    return Promise.resolve(finalObj);
  }

  private getHttpResponse(secretId : string) {
    return this.httpService.get(BASE_URL + secretId, {headers: { 'x-api-key': API_KEY }});
  }

  private decryptData(value : string, filename : string) : Buffer {
    const keystore = jks.toPem(fs.readFileSync(filename), 'test');
    const decryptedData = crypto.privateDecrypt(
      {
        key: keystore['test'].key,
        // We need to specify the same hashing function and padding scheme 
        // that we used to encrypt the data on the backend side
        padding: crypto.constants.RSA_PKCS1_PADDING,
        oaepHash: "sha256",
      },
      Buffer.from(value, "base64"),
    );

    return decryptedData;
  }
}
