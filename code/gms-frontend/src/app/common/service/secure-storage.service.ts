import { Injectable } from "@angular/core";
import * as CryptoJS from 'crypto-js';

const secret_key = CryptoJS.enc.Utf8.parse('12345678876543211234567887654321');
const options = { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 };  

@Injectable()
export class SecureStorageService {

    getItem(key : string) : string {
        return CryptoJS.AES.decrypt(CryptoJS.lib.CipherParams.create({ciphertext: CryptoJS.enc.Base64.parse(localStorage.getItem(key) || '')}), secret_key, options).toString(CryptoJS.enc.Utf8)
    }

    setItem(key : string, data : string) : void {
        const encryptedData = CryptoJS.AES.encrypt(data, secret_key, options).toString();
        localStorage.setItem(key, encryptedData);
    }
}