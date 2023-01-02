import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { IEntitySaveResponseDto } from "../model/entity-save-response.model";
import { UserData } from "../model/user-data.model";
import { SystemStatusDto } from "../model/system-status.model";
import { getHeaders } from "../utils/header-utils";

@Injectable({
    providedIn : "root"
})
export class SetupService {

    constructor(private http : HttpClient) { }

    public checkReady() : Observable<SystemStatusDto> {
        return this.http.get <SystemStatusDto>(environment.baseUrl + 'system/status', { headers : getHeaders() });
    }

    public saveAdminUser(adminUserData : UserData) : Observable<IEntitySaveResponseDto> {
        return this.http.post<IEntitySaveResponseDto>(environment.baseUrl + 'setup/user', adminUserData, { headers : getHeaders() });
    }
}