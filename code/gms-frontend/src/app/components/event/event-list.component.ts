
import { Component } from "@angular/core";

import { ActivatedRoute, Router } from "@angular/router";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { Event, PAGE_CONFIG_EVENT } from "./model/event.model";
import { EventService } from "./service/event-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'event-list-component',
    templateUrl: './event-list.component.html'
})
export class EventListComponent extends BaseListComponent<Event, EventService> {

    userColumns: string[] = ['id', 'username', 'operation', 'target', 'eventDate'];

    constructor(
        override router: Router,
        override sharedData: SharedDataService,
        override service: EventService,
        public override dialogService: DialogService,
        override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialogService, activatedRoute);
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_EVENT;
    }
}