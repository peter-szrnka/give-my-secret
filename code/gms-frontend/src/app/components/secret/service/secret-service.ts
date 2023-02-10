import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Secret } from "../model/secret.model";
import { SecretList } from "../model/secret-list.model";
import { environment } from "../../../../environments/environment";
import { getHeaders } from "../../../common/utils/header-utils";
import { SaveServiceBase } from "../../../common/components/abstractions/service/save-service-base";
/**
 * @author Peter Szrnka
 */
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