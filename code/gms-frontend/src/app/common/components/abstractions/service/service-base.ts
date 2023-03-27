import { HttpClient } from "@angular/common/http";
import { map, Observable, of } from "rxjs";
import { environment } from "../../../../../environments/environment";
import { BaseList } from "../../../model/base-list";
import { LongValue } from "../../../model/long-value.model";
import { Paging } from "../../../model/paging.model";
import { getHeaders } from "../../../utils/header-utils";
import { Service } from "./service-if";

/**
 * @author Peter Szrnka
 */
export abstract class ServiceBase<T, L extends BaseList<T>> implements Service<T> {

    constructor(protected http : HttpClient, protected scope : string) {}

    delete(id: number): Observable<string> {
        return this.http.delete<string>(environment.baseUrl + 'secure/'  + this.scope + '/' + id, { withCredentials: true, headers : getHeaders() });
    }

    list(paging: Paging): Observable<L> {
        return this.http.post<L>(environment.baseUrl + 'secure/' + this.scope + '/list', paging, { withCredentials: true, headers : getHeaders() });
    }

    getById(id: number): Observable<T> {
        return this.http.get<T>(environment.baseUrl + 'secure/'  + this.scope + '/' + id, { withCredentials: true, headers : getHeaders() });
    }

    count(): Observable<number> {
        return this.http.get<LongValue>(environment.baseUrl + 'secure/' + this.scope + '/count', { withCredentials: true, headers : getHeaders() }).pipe(map(dto => dto.value));
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    toggle(id: number, enabled: boolean): Observable<string> {
        return of();
    }
}