import { Component, OnInit } from "@angular/core";
import { MatDialog } from '@angular/material/dialog';
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { UserService } from "../../common/service/user-service";
import { getErrorMessage } from "../../common/utils/error-utils";

export interface PasswordSettings {
  oldCredential: string | undefined,
  newCredential1: string | undefined,
  newCredential2: string | undefined
}

@Component({
  selector: 'settings-summary-component',
  templateUrl: './settings-summary.component.html',
  styleUrls: ['./settings-summary.component.scss']
})
export class SettingsSummaryComponent implements OnInit {

  panelOpenState = false;
  credentialData: PasswordSettings = {
    oldCredential: undefined,
    newCredential1: undefined,
    newCredential2: undefined
  };
  passwordEnabled = true;

  constructor(
    private sharedData : SharedDataService,
    private userService: UserService,
    public dialog: MatDialog,
    private splashScreenService : SplashScreenStateService) { }

  ngOnInit(): void {
    this.passwordEnabled = this.sharedData.authMode === 'db';
  }

  save() {
    this.splashScreenService.start();

    this.userService.changeCredentials({
      oldCredential: this.credentialData.oldCredential,
      newCredential: this.credentialData.newCredential1
    }).subscribe({
      next: () => {
        this.dialog.open(InfoDialog, { data: { text : "Password has been updated successfully!", type : "information" } });
      },
      error: (err) => {
        this.dialog.open(InfoDialog, { data: { text : "Unexpected error occurred: " + getErrorMessage(err), type : "warning" } });
        this.splashScreenService.stop();
      },
      complete: () => {
        this.splashScreenService.stop();
      }
    });
  }
}