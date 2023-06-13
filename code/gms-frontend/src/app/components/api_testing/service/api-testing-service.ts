import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { CredentialApiResponse } from "../../secret/model/credential-api-response.model";

/**
 * @author Peter Szrnka
 */
@Injectable({providedIn : "root"})
export class ApiTestingService {

    constructor(private http : HttpClient) {}

    getSecretValue(secretId : string, apiKey : string): Observable<CredentialApiResponse | { [key:string] : string } > {
        return this.http.get<CredentialApiResponse | { [key:string] : string }>(environment.baseUrl + 'api/secret/' + secretId, { 
            withCredentials: true, headers : new HttpHeaders().set('x-api-key', [ apiKey ])
        });
    }
}