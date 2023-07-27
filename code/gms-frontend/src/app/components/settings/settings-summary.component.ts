import { Component, OnInit } from "@angular/core";
import { MatDialog } from '@angular/material/dialog';
import { InfoDialog } from "../../common/components/info-dialog/info-dialog.component";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { UserService } from "../user/service/user-service";
import { environment } from "../../../environments/environment";

export interface PasswordSettings {
  oldCredential: string | undefined,
  newCredential1: string | undefined,
  newCredential2: string | undefined
}

/**
 * @author Peter Szrnka
 */
@Component({
  selector: 'settings-summary-component',
  templateUrl: './settings-summary.component.html',
  styleUrls: ['./settings-summary.component.scss']
})
export class SettingsSummaryComponent implements OnInit {

  imageBaseUrl: string = environment.baseUrl;
  panelOpenState = false;
  credentialData: PasswordSettings = {
    oldCredential: undefined,
    newCredential1: undefined,
    newCredential2: undefined
  };
  passwordEnabled = true;
  mfaEnabled = false;

  constructor(
    private sharedData : SharedDataService,
    private userService: UserService,
    public dialog: MatDialog,
    private splashScreenService : SplashScreenStateService) { }

  ngOnInit(): void {
    this.sharedData.authModeSubject$.subscribe(authMode => this.passwordEnabled = authMode === 'db');
    this.userService.isMfaActive().subscribe(response => this.mfaEnabled = response);
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

  toggleMfa() {
    this.splashScreenService.start();
    this.userService.toggleMfa(this.mfaEnabled).subscribe({
      next: () => {
        this.dialog.open(InfoDialog, { data: { text : "MFA toggle updated successfully!", type : "information" } });
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