import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { UserData } from "../model/user-data.model";
import { UserDataList } from "../model/user-list.model";
import { getHeaders } from "../../../common/utils/header-utils";
import { SaveServiceBase } from "../../../common/components/abstractions/service/save-service-base";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class UserService extends SaveServiceBase<UserData, UserDataList> {

    constructor(http : HttpClient) {
        super(http, "user");
    }

    public changeCredentials(dto : any) : Observable<void> {
        return this.http.post<void>(environment.baseUrl + "secure/" + this.scope + '/change_credential', dto, { withCredentials : true, headers : getHeaders() });
    }

    public override toggle(id: number, enabled : boolean): Observable<string> {
        return this.http.post<string>(environment.baseUrl + "secure/" + this.scope + '/' + id + "?enabled=" + enabled, {}, { withCredentials : true, headers : getHeaders(), responseType : 'text' as 'json' });
    }

    public toggleMfa(enabled : boolean): Observable<string> {
        return this.http.post<string>(environment.baseUrl + "secure/" + this.scope + "/toggle_mfa?enabled=" + enabled, {}, { withCredentials : true, headers : getHeaders(), responseType : 'text' as 'json' });
    }

    public isMfaActive(): Observable<boolean> {
        return this.http.get<boolean>(environment.baseUrl + "secure/" + this.scope + "/mfa_active", { withCredentials : true, headers : getHeaders() });
    }

    public manualLdapUserSync() : Observable<void> {
        return this.http.get<void>(environment.baseUrl + "secure/" + this.scope + '/sync_ldap_users', { withCredentials : true, headers : getHeaders() });
    }
}