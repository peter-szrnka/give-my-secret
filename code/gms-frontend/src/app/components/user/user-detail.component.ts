import { ArrayDataSource } from "@angular/cdk/collections";
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, ElementRef, ViewChild } from "@angular/core";
import { MatAutocompleteSelectedEvent } from "@angular/material/autocomplete";
import { MatChipInputEvent } from '@angular/material/chips';
import { MatDialog } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseSaveableDetailComponent } from "../../common/components/abstractions/component/base-saveable-detail.component";
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { PageConfig } from "../../common/model/common.model";
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

const ALL_ROLES: string[] = ['ROLE_USER', 'ROLE_VIEWER', 'ROLE_ADMIN'];

@Component({
  selector: 'user-detail-component',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.scss']
})
export class UserDetailComponent extends BaseSaveableDetailComponent<UserData, UserService> {

  userColumns: string[] = [ 'id', 'operation', 'target', 'eventDate' ];

  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  @ViewChild('roleInput') roleInput: ElementRef<HTMLInputElement>;
  addOnBlur = true;
  auto = true;
  selectableRoles = ALL_ROLES;
  eventList : Event[] = [];
  public datasource : ArrayDataSource<Event>;
  editEnabled: boolean = true;

  public tableConfig = {
    pageSize : 20
  };

  constructor(
    protected override router: Router,
    protected override sharedData: SharedDataService,
    protected override service: UserService,
    public override dialog: MatDialog,
    protected override activatedRoute: ActivatedRoute,
    public eventService : EventService,
    protected override splashScreenStateService: SplashScreenStateService) {
    super(router, sharedData, service, dialog, activatedRoute, splashScreenStateService);
  }

  override async ngOnInit(): Promise<void> {
    super.ngOnInit();

    this.sharedData.authModeSubject$.subscribe(authMode => this.editEnabled = authMode === 'db');
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  override dataLoadingCallback(data: UserData) {
    this.refreshSelectableRoles();

    if (data.id === undefined) {
      return;
    }

    this.eventService.listByUserId(EVENT_LIST_FILTER, data.id).subscribe(eventList => {
      this.eventList = eventList;
      this.datasource = new ArrayDataSource<Event>(this.eventList);
    });
  }

  private refreshSelectableRoles(): void {
    this.selectableRoles = ALL_ROLES;
    this.selectableRoles = this.selectableRoles.filter((item) => {
      return !this.data.roles.includes(item);
    });
  }

  getPageConfig(): PageConfig {
    return PAGE_CONFIG_USER;
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    if (this.data.roles.length === 1) {
      this.showTooManyElementsDialog();
      return;
    }

    this.data.roles.push(event.option.viewValue);
    this.roleInput.nativeElement.value = '';
    this.refreshSelectableRoles();
  }

  add(event: MatChipInputEvent): void {
    if (this.data.roles.length === 1) {
      this.showTooManyElementsDialog();
      return;
    }

    const value = event.value.trim();

    if (value) {
      this.data.roles.push(value);
    }

    this.refreshSelectableRoles();
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    event.chipInput.clear();
  }

  remove(role: string): void {
    if (this.data.roles.includes(role)) {
      this.data.roles.splice(this.data.roles.indexOf(role), 1);
    }

    this.refreshSelectableRoles();
  }

  private showTooManyElementsDialog() {
    this.dialog.open(InfoDialog, { data: { text: "Only one role can be added to the user!", type : 'warning' },});
  }
}