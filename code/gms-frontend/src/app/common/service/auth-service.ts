import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Login, VerifyLogin, LoginResponse } from '../model/login.model';
import { getHeaders } from '../utils/header-utils';

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

    constructor(private http : HttpClient) { }

    login(data : Login) : Observable<LoginResponse> {
        return this.http.post<LoginResponse>(environment.baseUrl + 'authenticate', data, { withCredentials : true, headers : getHeaders() });
    }

    verifyLogin(data : VerifyLogin) : Observable<LoginResponse> {
        return this.http.post<LoginResponse>(environment.baseUrl + 'verify', data, { withCredentials : true, headers : getHeaders() });
    }

    logout() : Observable<Response> {
        return this.http.post<Response>(environment.baseUrl + 'logoutUser', {}, { withCredentials : true, headers : getHeaders() });
    }

    refreshToken() : Observable<Response> {
        return this.http.post<Response>(environment.baseUrl + 'refresh', {}, { withCredentials : true, headers : getHeaders() });
    }
}