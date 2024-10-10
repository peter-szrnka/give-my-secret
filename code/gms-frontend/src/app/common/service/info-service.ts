import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { getHeaders } from "../utils/header-utils";
import { environment } from "../../../environments/environment";
import { User } from "../../components/user/model/user.model";
import { firstValueFrom } from "rxjs";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class InformationService {

    constructor(private readonly http : HttpClient) {}

    getUserInfo() : Promise<User> {
        return firstValueFrom(this.http.get<User>(environment.baseUrl + 'info/me', { withCredentials : true, headers : getHeaders() }));
    }
}