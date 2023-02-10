import { HttpClient, } from "@angular/common/http";
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
@Injectable()
export class UserService extends SaveServiceBase<UserData, UserDataList> {

    constructor(http : HttpClient) {
        super(http, "user");
    }

    public changeCredentials(dto : any) : Observable<void> {
        return this.http.post<void>(environment.baseUrl + "secure/" + this.scope + '/change_credential', dto, { headers : getHeaders() });
    }
}