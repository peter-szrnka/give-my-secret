import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { HomeData } from "../model/home-data.model";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { getHeaders } from "../../../common/utils/header-utils";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class HomeService {
    
    constructor(private http : HttpClient) {
    }

    getData(): Observable<HomeData> {
        return this.http.get<HomeData>(environment.baseUrl + 'secure/home/', { withCredentials: true, headers : getHeaders() });
    }
}