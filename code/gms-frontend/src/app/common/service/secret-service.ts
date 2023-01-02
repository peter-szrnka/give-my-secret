import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { Secret } from "../model/secret.model";
import { SecretList } from "../model/secret-list.model";
import { SaveServiceBase } from "./save-service-base";
import { getHeaders } from "../utils/header-utils";

@Injectable()
export class SecretService extends SaveServiceBase<Secret, SecretList> {

    constructor(http : HttpClient,) {
        super(http, "secret");
    }

    public getValue(id? : number) : Observable<string> {
        return this.http.get<string>(environment.baseUrl + "secure/secret/value/" + id, { withCredentials: true, headers : getHeaders(), responseType : 'text' as 'json' });
    }

    public rotate(id? : number) : Observable<string> {
        return this.http.post<string>(environment.baseUrl + "secure/" + 'secret/rotate/' + id, {}, { withCredentials: true, headers : getHeaders() });
    }
}