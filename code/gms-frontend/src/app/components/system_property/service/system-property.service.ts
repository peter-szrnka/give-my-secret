import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../../../environments/environment";
import { Observable } from "rxjs";
import { SystemProperty } from "../model/system-property.model";
import { getHeaders } from "../../../common/utils/header-utils";
import { Paging } from "../../../common/model/paging.model";
import { SystemPropertyList } from "../model/system-property-list.model";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class SystemPropertyService {

    constructor(protected http : HttpClient) {}

    save(item : SystemProperty) : Observable<string> {
        return this.http.post<string>(environment.baseUrl + 'secure/system_property', item, { withCredentials: true, headers : getHeaders() });
    }

    delete(key: string): Observable<string> {
        return this.http.delete<string>(environment.baseUrl + 'secure/system_property/' + key, { withCredentials: true, headers : getHeaders() });
    }

    list(paging: Paging): Observable<SystemPropertyList> {
        return this.http.get<SystemPropertyList>(environment.baseUrl + 'secure/system_property/list' + 
            `?direction=${paging.direction}&property=${paging.property}&page=${paging.page}&size=${paging.size}`, 
            { withCredentials: true, headers : getHeaders() });
    }
}