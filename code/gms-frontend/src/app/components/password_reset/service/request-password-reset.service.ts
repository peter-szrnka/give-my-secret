import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";


@Injectable({providedIn : "root"})
export class ResetPasswordRequestService {

    constructor(protected http : HttpClient) {
    }

    public requestPasswordReset(username: string) : Observable<boolean> {
        return this.http.post<boolean>(environment.baseUrl + 'reset_password', { username: username });
    }
}