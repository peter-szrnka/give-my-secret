import { Injectable } from '@nestjs/common';
import fs from 'fs';
import jks from 'jks-js';
import crypto from 'crypto';
import { HttpService } from 'nestjs-http-promise';

const BASE_URL : string = "https://localhost:8443/api/secret/";
// Your variables //////////////////////////////////////////////////
const SECRET_ID : string = "secret1";
const API_KEY : string = "P2jFEjUs0KtHZjBOiFWUlrmw38NRI1J2";
////////////////////////////////////////////////////////////////////

@Injectable()
export class AppService {

  constructor(private readonly httpService: HttpService) {}

  async getHello(filename : string): Promise<string> {
    const httpResponse = await this.getHttpResponse();

    if (httpResponse.status != 200) {
      return Promise.reject("Failure!");
    }

    const decryptedData : Buffer = this.decryptData(httpResponse.data.value, filename);
    console.info(decryptedData.toString("utf-8").split(";").map(item => item.split(":")));
    return Promise.resolve(decryptedData.toString("utf-8"));
  }

  private getHttpResponse() {
    return this.httpService.get(BASE_URL + SECRET_ID, {headers: { 'x-api-key': API_KEY }});
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
