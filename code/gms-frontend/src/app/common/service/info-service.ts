import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { getHeaders } from "../utils/header-utils";
import { environment } from "../../../environments/environment";
import { User } from "../../components/user/model/user.model";
import { firstValueFrom, Observable } from "rxjs";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class InformationService {

    constructor(private readonly http : HttpClient) {}

    healthCheck() : Promise<string> {
        return firstValueFrom(this.http.get<string>(environment.baseUrl + 'healthcheck', {}));
    }

    public getVmOptions() : Observable<{ [key: string]: string }> {
        return this.http.get<{ [key: string]: string }>(environment.baseUrl + 'info/vm_options', { withCredentials : true, headers : getHeaders() });
    }

    getUserInfo() : Promise<User> {
        return firstValueFrom(this.http.get<User>(environment.baseUrl + 'info/me', { withCredentials : true, headers : getHeaders() }));
    }
}