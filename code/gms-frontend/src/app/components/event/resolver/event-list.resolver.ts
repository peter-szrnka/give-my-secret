import { Injectable } from "@angular/core";
import { Event, PAGE_CONFIG_EVENT } from "../model/event.model";
import { EventList } from "../model/event-list.model";
import { EventService } from "../service/event-service";
import { SplashScreenStateService } from "../../../common/service/splash-screen-service";
import { SharedDataService } from "../../../common/service/shared-data-service";
import { ListResolverV2 } from "../../../common/components/abstractions/resolver/list-data.resolver";
import { PageConfig } from "../../../common/model/common.model";

/**
 * @author Peter Szrnka
 */
@Injectable()
export class EventListResolver extends ListResolverV2<Event, EventList, EventService> {

    constructor(sharedData : SharedDataService, protected override splashScreenStateService: SplashScreenStateService, protected override service : EventService) {
        super(sharedData, splashScreenStateService, service);
    }

    override getPageConfig(): PageConfig {
        return PAGE_CONFIG_EVENT;
    }

    override getOrderProperty(): string {
        return "eventDate";
    }
}