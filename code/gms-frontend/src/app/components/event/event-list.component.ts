
import { Component } from "@angular/core";

import { FormsModule } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { takeUntil } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { BaseListComponent } from "../../common/components/abstractions/component/base-list.component";
import { InformationMessageComponent } from "../../common/components/information-message/information-message.component";
import { NavBackComponent } from "../../common/components/nav-back/nav-back.component";
import { MomentPipe } from "../../common/components/pipes/date-formatter.pipe";
import { TranslatorModule } from "../../common/components/pipes/translator/translator.module";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { TranslatorService } from "../../common/service/translator-service";
import { Event, PAGE_CONFIG_EVENT } from "./model/event.model";
import { EventService } from "./service/event-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'event-list-component',
    templateUrl: './event-list.component.html',
    imports: [
        AngularMaterialModule,
        FormsModule,
        MomentPipe,
        NavBackComponent,
        TranslatorModule,
        InformationMessageComponent
    ]
})
export class EventListComponent extends BaseListComponent<Event, EventService> {

    userColumns: string[] = ['id', 'username', 'entityId', 'operation', 'source', 'target', 'eventDate'];
    unprocessedEventsLabel: string = '';

    constructor(
        override router: Router,
        override sharedData: SharedDataService,
        override service: EventService,
        public override dialogService: DialogService,
        override activatedRoute: ActivatedRoute,
        private readonly translatorService: TranslatorService) {
        super(router, sharedData, service, dialogService, activatedRoute);
    }

    override ngOnInit(): void {
        super.ngOnInit();
        this.service.getUnprocessedEventsCount()
        .pipe(takeUntil(this.destroy$))
        .subscribe((response: any) => this.unprocessedEventsLabel = this.translatorService.translate('event.unprocessed', response.value));
    }

    getPageConfig(): PageConfig {
        return PAGE_CONFIG_EVENT;
    }
}