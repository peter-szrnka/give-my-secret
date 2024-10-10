import { Component, OnInit } from "@angular/core";
import { environment } from "../../../environments/environment";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { UserService } from "../user/service/user-service";

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
  authMode = '';
  mfaEnabled = false;
  showQrCode = false;

  constructor(
    private readonly sharedData : SharedDataService,
    private readonly userService: UserService,
    public dialogService: DialogService,
    private readonly splashScreenService : SplashScreenStateService) { }

  ngOnInit(): void {
    this.sharedData.authModeSubject$.subscribe(authMode => this.authMode = authMode);
    this.userService.isMfaActive().subscribe(response => this.mfaEnabled = response);
  }

  save() {
    this.splashScreenService.start();

    this.userService.changeCredentials({
      oldCredential: this.credentialData.oldCredential,
      newCredential: this.credentialData.newCredential1
    }).subscribe({
      next: () => {
        this.dialogService.openInfoDialog("Password updated", "Password has been updated successfully!");
        this.splashScreenService.stop();
      },
      error: (err) => {
        this.openWarning(err);
      }
    });
  }

  toggleMfa() {
    this.splashScreenService.start();
    this.userService.toggleMfa(this.mfaEnabled).subscribe({
      next: () => {
        this.dialogService.openInfoDialog("MFA toggle updated", "MFA toggle updated successfully!");
        this.splashScreenService.stop();
      },
      error: (err) => {
        this.openWarning(err);
      }
    });
  }

  private openWarning(error: any): void {
    this.dialogService.openWarningDialog("Unexpected error occurred: " + getErrorMessage(error));
    this.splashScreenService.stop();
  }
}