import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { getHeaders } from "../../../common/utils/header-utils";
import { HomeData } from "../model/home-data.model";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root'})
export class HomeService {
    
    constructor(private readonly http : HttpClient) {
    }

    getData(): Observable<HomeData> {
        return this.http.get<HomeData>(environment.baseUrl + 'secure/home/', { withCredentials: true, headers : getHeaders() });
    }
}