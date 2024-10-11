import { Injectable } from "@angular/core";
import * as CryptoJS from 'crypto-js';

const secret_key = CryptoJS.enc.Utf8.parse('12345678876543211234567887654321');
const options = { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 };  

/**
 * @author Peter Szrnka
 */
@Injectable()
export class SecureStorageService {

    getItem(username: string, key : string) : string {
        return CryptoJS.AES.decrypt(CryptoJS.lib.CipherParams.create({ciphertext: CryptoJS.enc.Base64.parse(localStorage.getItem(username + key) ?? '')}), secret_key, options)
            .toString(CryptoJS.enc.Utf8)
    }

    setItem(username: string, key : string, data : string) : void {
        const encryptedData = CryptoJS.AES.encrypt(data, secret_key, options).toString();
        console.info('Encrypted data: ' + encryptedData);
        localStorage.setItem(username + key, encryptedData);
    }
}