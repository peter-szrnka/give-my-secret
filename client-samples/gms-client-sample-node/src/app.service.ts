import { Injectable } from '@nestjs/common';
import fs from 'fs';
import jks from 'jks-js';
import crypto from 'crypto';
import { HttpService } from 'nestjs-http-promise';

const BASE_URL : string = "http://localhost:8080/api/secret/";
// Your variables //////////////////////////////////////////////////
const SECRET_ID : string = "TestSecret1";
const API_KEY : string = "IntTestApiKey";
////////////////////////////////////////////////////////////////////

@Injectable()
export class AppService {

  constructor(private readonly httpService: HttpService) {}

  async getHello(): Promise<string> {
    const httpResponse = await this.getHttpResponse();

    if (httpResponse.status != 200) {
      return Promise.reject("Failure!");
    }

    const decryptedData : Buffer = this.decryptData(httpResponse.data.value);
    return Promise.resolve(decryptedData.toString("utf-8"));
  }

  private getHttpResponse() {
    return this.httpService.get(BASE_URL + SECRET_ID, {headers: { 'x-api-key': API_KEY }});
  }

  private decryptData(value : string) : Buffer {
    const keystore = jks.toPem(fs.readFileSync('test.jks'), 'test');
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
