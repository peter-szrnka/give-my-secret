import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, map, tap } from "rxjs";
import { environment } from "../../../../environments/environment";
import { ServiceBase } from "../../../common/components/abstractions/service/service-base";
import { Paging } from "../../../common/model/paging.model";
import { getHeaders } from "../../../common/utils/header-utils";
import { EventList } from "../model/event-list.model";
import { Event } from "../model/event.model";

/**
 * @author Peter Szrnka
 */
@Injectable({ providedIn: 'root' })
export class EventService extends ServiceBase<Event, EventList> {

    constructor(http : HttpClient) {
        super(http, "event");
    }

    listByUserId(paging: Paging, userId?: number): Observable<Event[]> {
        return this.http.get<EventList>(environment.baseUrl + 'secure/event/list/' + userId + `?direction=${paging.direction}&property=${paging.property}&page=${paging.page}&size=${paging.size}`,
             { withCredentials: true, headers : getHeaders() })
            .pipe(tap(), map(value => value.resultList));
    }

    getUnprocessedEventsCount(): Observable<number> {
        return this.http.get<number>(`${environment.baseUrl}secure/event/unprocessed`, { withCredentials: true, headers : getHeaders() });
    }
}