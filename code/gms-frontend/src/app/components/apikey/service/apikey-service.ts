import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ApiKey } from "../model/apikey.model";
import { ApiKeyList } from "../model/apikey-list.model";
import { Observable, map } from "rxjs";
import { IdNamePair } from "../../../common/model/id-name-pair.model";
import { IdNamePairList } from "../../../common/model/id-name-pair-list.model";
import { environment } from "../../../../environments/environment";
import { getHeaders } from "../../../common/utils/header-utils";
import { SaveServiceBase } from "../../../common/components/abstractions/service/save-service-base";

/**
 * @author Peter Szrnka
 */
@Injectable({providedIn : "root"})
export class ApiKeyService extends SaveServiceBase<ApiKey, ApiKeyList> {

    constructor(http : HttpClient) {
        super(http, "apikey");
    }

    public getAllApiKeyNames() : Observable<IdNamePair[]> {
        return this.http.get<IdNamePairList>(environment.baseUrl + "secure/" + this.scope + '/list_names', { withCredentials : true, headers : getHeaders() }).pipe(map(value => value.resultList));
    }

    public override toggle(id: number, enabled : boolean): Observable<string> {
        return this.http.post<string>(environment.baseUrl + "secure/" + this.scope + '/' + id + "?enabled=" + enabled, {}, { withCredentials : true, headers : getHeaders(), responseType : 'text' as 'json' });
    }
}