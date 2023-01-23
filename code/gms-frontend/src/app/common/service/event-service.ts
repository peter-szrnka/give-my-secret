import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Event } from "../model/event.model";
import { EventList } from "../model/event-list.model";
import { ServiceBase } from "./service-base";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class EventService extends ServiceBase<Event, EventList> {

    constructor(http : HttpClient) {
        super(http, "event");
    }
}