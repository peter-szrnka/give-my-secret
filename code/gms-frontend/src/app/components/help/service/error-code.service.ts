import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { getHeaders } from "../../../common/utils/header-utils";
import { ErrorCodeList } from "../model/error-code-list.model";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class ErrorCodeService {

    constructor(protected http : HttpClient) {}

    list(): Observable<ErrorCodeList> {
        return this.http.get<ErrorCodeList>(environment.baseUrl + 'error_codes', { withCredentials: true, headers : getHeaders() });
    }
}