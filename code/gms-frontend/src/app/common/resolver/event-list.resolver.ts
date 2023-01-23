import { Injectable } from "@angular/core";
import { Event } from "../model/event.model";
import { EventList } from "../model/event-list.model";
import { EventService } from "../service/event-service";
import { SplashScreenStateService } from "../service/splash-screen-service";
import { ListResolver } from "./list-data.resolver";
import { SharedDataService } from "../service/shared-data-service";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class EventListResolver extends ListResolver<Event, EventList, EventService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : EventService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getOrderProperty(): string {
        return "eventDate";
    }
}