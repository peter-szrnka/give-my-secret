import { ArrayDataSource } from "@angular/cdk/collections";
import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseSaveableDetailComponent } from "../../common/components/abstractions/component/base-saveable-detail.component";
import { PageConfig } from "../../common/model/common.model";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { Event } from "../event/model/event.model";
import { EventService } from "../event/service/event-service";
import { PAGE_CONFIG_USER, UserData } from "./model/user-data.model";
import { UserService } from "./service/user-service";

const EVENT_LIST_FILTER = {
  direction: "DESC",
  property: "eventDate",
  page: 0,
  size: 10
};

const ALL_ROLES: string[] = ['ROLE_USER', 'ROLE_VIEWER', 'ROLE_ADMIN','ROLE_TECHNICAL'];
const ALL_STATUS: string[] = [ 'ACTIVE', 'BLOCKED', 'DISABLED', 'DELETE_REQUESTED', 'TO_BE_DELETED', 'ANONYMIZATION_REQUESTED', 'ANONYMIZED' ];

@Component({
    selector: 'user-detail-component',
    templateUrl: './user-detail.component.html',
    styleUrls: ['./user-detail.component.scss'],
    standalone: false
})
export class UserDetailComponent extends BaseSaveableDetailComponent<UserData, UserService> {

  userColumns: string[] = [ 'id', 'operation', 'target', 'eventDate' ];

  auto = true;
  selectableRoles = ALL_ROLES;
  selectableStatuses = ALL_STATUS;
  eventList : Event[] = [];
  public datasource : ArrayDataSource<Event>;
  editEnabled: boolean = true;
  showPassword: boolean = false;

  public tableConfig = {
    pageSize : 20
  };

  constructor(
    protected override router: Router,
    protected override sharedData: SharedDataService,
    protected override service: UserService,
    public override dialogService: DialogService,
    protected override activatedRoute: ActivatedRoute,
    public eventService : EventService,
    protected override splashScreenStateService: SplashScreenStateService) {
    super(router, sharedData, service, dialogService, activatedRoute, splashScreenStateService);
  }

  override ngOnInit(): void {
    super.ngOnInit();

    this.sharedData.authModeSubject$.subscribe(authMode => this.editEnabled = authMode === 'db');
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  override dataLoadingCallback(data: UserData) {

    if (data.id === undefined) {
      return;
    }

    this.eventService.listByUserId(EVENT_LIST_FILTER, data.id).subscribe(eventList => {
      this.eventList = eventList;
      this.datasource = new ArrayDataSource<Event>(this.eventList);
    });
  }

  getPageConfig(): PageConfig {
    return PAGE_CONFIG_USER;
  }

  togglePasswordDisplay(): void {
    this.showPassword = !this.showPassword;
  }
}