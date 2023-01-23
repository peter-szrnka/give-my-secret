import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Login } from '../model/login.model';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { getHeaders } from '../utils/header-utils';

/**
 * @author Peter Szrnka
 */
@Injectable()
export class AuthService {

    constructor(private http : HttpClient) { }

    login(data : Login) : Observable<Response> {
        return this.http.post<Response>(environment.baseUrl + 'authenticate', data, { withCredentials : true, headers : getHeaders() });
    }

    logout() : Observable<Response> {
        return this.http.post<Response>(environment.baseUrl + 'logoutUser', {}, { withCredentials : true, headers : getHeaders() });
    }

    refreshToken() : Observable<Response> {
        return this.http.post<Response>(environment.baseUrl + 'refresh', {}, { withCredentials : true, headers : getHeaders() });
    }
}