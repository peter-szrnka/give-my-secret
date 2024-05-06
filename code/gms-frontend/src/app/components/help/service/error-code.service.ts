import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../../environments/environment";
import { getHeaders } from "../../../common/utils/header-utils";
import { ErrorCode } from "../model/error-code.model";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class ErrorCodeService {

    constructor(protected http : HttpClient) {}

    list(): Observable<ErrorCode[]> {
        return this.http.get<ErrorCode[]>(environment.baseUrl + 'error_codes', { withCredentials: true, headers : getHeaders() });
    }
}