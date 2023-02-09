import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { CredentialPairApiResponse } from "../model/credential-pair-api-response.model";
import { CredentialApiResponse } from "../model/credential-api-response.model";

/**
 * @author Peter Szrnka
 */
@Injectable({providedIn : "root"})
export class ApiTestingService {

    constructor(private http : HttpClient) {}

    getSecretValue(secretId : string, apiKey : string): Observable<CredentialApiResponse|CredentialPairApiResponse> {
        return this.http.get<CredentialApiResponse|CredentialPairApiResponse>(environment.baseUrl + 'api/secret/' + secretId, { 
            withCredentials: true, headers : new HttpHeaders().set('x-api-key', [ apiKey ])
        });
    }
}