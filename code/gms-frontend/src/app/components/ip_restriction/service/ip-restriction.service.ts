import { Injectable } from "@angular/core";
import { IpRestriction } from "../model/ip-restriction.model";
import { IpRestrictionList } from "../model/ip-restriction-list.model";
import { HttpClient } from "@angular/common/http";
import { SaveServiceBase } from "../../../common/components/abstractions/service/save-service-base";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { getHeaders } from "../../../common/utils/header-utils";

/**
 * @author Peter Szrnka
 */
@Injectable({providedIn : "root"})
export class IpRestrictionService extends SaveServiceBase<IpRestriction, IpRestrictionList> {

    constructor(http : HttpClient) {
        super(http, "ip_restriction");
    }

    public override toggle(id: number, enabled : boolean): Observable<string> {
        return this.http.post<string>(environment.baseUrl + "secure/" + this.scope + '/' + id + "?enabled=" + enabled, {}, { withCredentials : true, headers : getHeaders(), responseType : 'text' as 'json' });
    }
}