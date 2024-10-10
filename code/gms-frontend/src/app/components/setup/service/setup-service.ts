import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { SystemStatus } from "../../../common/model/system-status.model";
import { getHeaders } from "../../../common/utils/header-utils";
import { UserData } from "../../user/model/user-data.model";

/**
 * @author Peter Szrnka
 */
@Injectable({
    providedIn : "root"
})
export class SetupService {

    constructor(private readonly http : HttpClient) { }

    public checkReady() : Observable<SystemStatus> {
        return this.http.get <SystemStatus>(environment.baseUrl + 'system/status', { headers : getHeaders() });
    }

    public saveAdminUser(adminUserData : UserData) : Observable<IEntitySaveResponseDto> {
        return this.http.post<IEntitySaveResponseDto>(environment.baseUrl + 'setup/user', adminUserData, { headers : getHeaders() });
    }
}