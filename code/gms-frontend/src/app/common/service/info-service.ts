import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { getHeaders } from "../utils/header-utils";
import { environment } from "../../../environments/environment";
import { User } from "../../components/user/model/user.model";
import { firstValueFrom, Observable } from "rxjs";
import { VmOption } from "../model/common.model";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class InformationService {

    constructor(private readonly http : HttpClient) {}

    healthCheck() : Promise<string> {
        return firstValueFrom(this.http.get<string>(environment.baseUrl + 'healthcheck', {}));
    }

    public getVmOptions() : Observable<VmOption[]> {
        return this.http.get<VmOption[]>(environment.baseUrl + 'info/vm_options', { withCredentials : true, headers : getHeaders() });
    }

    getUserInfo() : Promise<User> {
        return firstValueFrom(this.http.get<User>(environment.baseUrl + 'info/me', { withCredentials : true, headers : getHeaders() }));
    }
}