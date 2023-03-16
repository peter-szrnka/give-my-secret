import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Event } from "../model/event.model";
import { EventList } from "../model/event-list.model";
import { ServiceBase } from "../../../common/components/abstractions/service/service-base";
import { Observable, map, tap } from "rxjs";
import { environment } from "../../../../environments/environment";
import { Paging } from "../../../common/model/paging.model";
import { getHeaders } from "../../../common/utils/header-utils";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class EventService extends ServiceBase<Event, EventList> {

    constructor(http : HttpClient) {
        super(http, "event");
    }

    listByUserId(paging: Paging, userId?: number): Observable<Event[]> {
        return this.http.post<EventList>(environment.baseUrl + 'secure/event/list/' + userId, paging, { withCredentials: true, headers : getHeaders() })
            .pipe(tap(), map(value => value.resultList));
    }
}