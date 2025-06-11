import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Secret } from "../model/secret.model";
import { SecretList } from "../model/secret-list.model";
import { environment } from "../../../../environments/environment";
import { getHeaders } from "../../../common/utils/header-utils";
import { SaveServiceBase } from "../../../common/components/abstractions/service/save-service-base";
import { BooleanValue } from "../../../common/model/boolean-value.model";
import { SecretValueInput } from "../model/secret-value-input";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root'})
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

    public override toggle(id: number, enabled : boolean): Observable<string> {
        return this.http.post<string>(environment.baseUrl + "secure/" + this.scope + '/' + id + "?enabled=" + enabled, {}, { withCredentials : true, headers : getHeaders(), responseType : 'text' as 'json' });
    }

    public validateLength(input: SecretValueInput): Observable<BooleanValue> {
        return this.http.post<BooleanValue>(environment.baseUrl + "secure/" + this.scope + '/validate_value_length', input, { withCredentials : true, headers : getHeaders()/*, responseType : 'text' as 'json'*/ });
    }
}