import { Component, OnInit } from "@angular/core";
import { environment } from "../../../environments/environment";
import { DialogService } from "../../common/service/dialog-service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { UserService } from "../user/service/user-service";
import { TranslatorService } from "../../common/service/translator-service";

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
  templateUrl: './settings-summary.component.html'
})
export class SettingsSummaryComponent implements OnInit {

  imageBaseUrl: string = environment.baseUrl;
  panelOpenState = false;
  credentialData: PasswordSettings = {
    oldCredential: undefined,
    newCredential1: undefined,
    newCredential2: undefined
  };
  language = 'en';
  authMode = '';
  mfaEnabled = false;
  showQrCode = false;

  constructor(
    private readonly sharedData: SharedDataService,
    private readonly userService: UserService,
    public dialogService: DialogService,
    private readonly splashScreenService: SplashScreenStateService,
    private readonly translatorService: TranslatorService) { }

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
        this.openInfoDialog("settings.password.dialog.title", "settings.password.dialog.text");
        this.splashScreenService.stop();
      },
      error: (err) => this.openWarning(err)
    });
  }

  saveLanguage() {
    console.info(this.language);
  }

  toggleMfa() {
    this.splashScreenService.start();
    this.userService.toggleMfa(this.mfaEnabled).subscribe({
      next: () => {
        this.openInfoDialog("settings.mfa.dialog.title", "settings.mfa.dialog.text");
        this.splashScreenService.stop();
      },
      error: (err) => this.openWarning(err)
    });
  }

  private openInfoDialog(titleKey: string, textKey: string): void {
    this.dialogService.openInfoDialog(
      this.translate(titleKey),
      this.translate(textKey)
    );
  }

  private translate(key: string): string {
    return this.translatorService.translate(key);
  }

  private openWarning(error: any): void {
    this.dialogService.openWarningDialog(this.translatorService.translate("settings.error", getErrorMessage(error)));
    this.splashScreenService.stop();
  }
}