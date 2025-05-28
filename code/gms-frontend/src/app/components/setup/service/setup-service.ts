import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { SystemStatus } from "../../../common/model/system-status.model";
import { getHeaders } from "../../../common/utils/header-utils";
import { SystemProperty } from "../../system_property/model/system-property.model";
import { UserData } from "../../user/model/user-data.model";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn : "root" })
export class SetupService {

    constructor(private readonly http : HttpClient) { }

    public checkReady() : Observable<SystemStatus> {
        return this.http.get<SystemStatus>(environment.baseUrl + 'info/status', { headers : getHeaders() });
    }

    public getAdminUserData() : Observable<UserData> {
        return this.http.get<UserData>(environment.baseUrl + 'setup/current_super_admin', { headers : getHeaders() });
    }

    public stepBack() : Observable<any> {
        return this.http.get<any>(environment.baseUrl + 'setup/step_back', { headers : getHeaders(), responseType: 'text' as 'json' });
    }

    public saveInitialStep() : Observable<any> {
        return this.http.post<any>(environment.baseUrl + 'setup/initial', {}, { headers : getHeaders() });
    }

    public saveAdminUser(adminUserData : UserData) : Observable<IEntitySaveResponseDto> {
        return this.http.post<IEntitySaveResponseDto>(environment.baseUrl + 'setup/user', adminUserData, { headers : getHeaders() });
    }

    public saveSystemProperties(properties: SystemProperty[]) : Observable<any> {
        return this.http.post<any>(environment.baseUrl + 'setup/properties', { properties: properties }, { headers : getHeaders() });
    }

    public saveOrganizationData(properties: SystemProperty[]) : Observable<any> {
        return this.http.post<any>(environment.baseUrl + 'setup/org_data', { properties: properties }, { headers : getHeaders() });
    }

    public completeSetup() : Observable<any> {
        return this.http.post<any>(environment.baseUrl + 'setup/complete', {}, { headers : getHeaders() });
    }
}