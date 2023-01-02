import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { ApiResponseDto } from "../model/api-response.model";

@Injectable({providedIn : "root"})
export class ApiTestingService {

    constructor(private http : HttpClient) {}

    getSecretValue(secretId : string, apiKey : string): Observable<ApiResponseDto> {
        return this.http.get<ApiResponseDto>(environment.baseUrl + 'api/secret/' + secretId, { 
            withCredentials: true, headers : new HttpHeaders().set('x-api-key', [ apiKey ])
        });
    }
}