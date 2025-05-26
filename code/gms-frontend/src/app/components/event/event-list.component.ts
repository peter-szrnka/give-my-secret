
import { Component } from "@angular/core";

import { ActivatedRoute, Router } from "@angular/router";
import { map } from "rxjs";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { ButtonConfig } from "../../common/components/nav-back/button-config";
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
    templateUrl: './event-list.component.html',
    standalone: false
})
export class EventListComponent extends BaseListComponent<Event, EventService> {

    userColumns: string[] = ['id', 'username', 'entityId', 'operation', 'source', 'target', 'eventDate'];
    unprocessedEventsCount: number = 0;

    buttonConfig: ButtonConfig[] = [
        { primary: true, type: 'TEXT', customText: `${this.unprocessedEventsCount}` }
    ];

    constructor(
        override router: Router,
        override sharedData: SharedDataService,
        override service: EventService,
        public override dialogService: DialogService,
        override activatedRoute: ActivatedRoute) {
        super(router, sharedData, service, dialogService, activatedRoute);
    }

    override ngOnInit(): void {
        super.ngOnInit();
        this.service.getUnprocessedEventsCount().subscribe(count => this.unprocessedEventsCount = count)
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_EVENT;
    }
}