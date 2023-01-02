import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ApiKey } from "../model/apikey.model";
import { ApiKeyList } from "../model/apikey-list.model";
import { SaveServiceBase } from "./save-service-base";
import { Observable, map } from "rxjs";
import { IdNamePair } from "../model/id-name-pair.model";
import { IdNamePairList } from "../model/id-name-pair-list.model";
import { environment } from "../../../environments/environment";
import { getHeaders } from "../utils/header-utils";

@Injectable({providedIn : "root"})
export class ApiKeyService extends SaveServiceBase<ApiKey, ApiKeyList> {

    constructor(http : HttpClient) {
        super(http, "apikey");
    }

    public getAllApiKeyNames() : Observable<IdNamePair[]> {
        return this.http.get<IdNamePairList>(environment.baseUrl + "secure/" + this.scope + '/list_names', { withCredentials : true, headers : getHeaders() }).pipe(map(value => value.resultList));
    }
}