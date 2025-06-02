import { Component } from "@angular/core";
import { Router } from "@angular/router";
import { takeUntil } from "rxjs";
import { environment } from "../../../environments/environment";
import { BaseComponent } from "../../common/components/abstractions/component/base.component";
import { DialogService } from "../../common/service/dialog-service";
import { SecureStorageService } from "../../common/service/secure-storage.service";
import { SharedDataService } from "../../common/service/shared-data-service";
import { SplashScreenStateService } from "../../common/service/splash-screen-service";
import { getErrorMessage } from "../../common/utils/error-utils";
import { UserService } from "../user/service/user-service";

export interface PasswordSettings {
  oldCredential: string | undefined,
  newCredential1: string | undefined,
  newCredential2: string | undefined
};

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'settings-summary-component',
    templateUrl: './settings-summary.component.html',
    standalone: false
})
export class SettingsSummaryComponent extends BaseComponent {

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
  showCurrentPassword: boolean = false;
  showNew1Password: boolean = false;
  showNew2Password: boolean = false;

  constructor(
    private readonly router: Router,
    private readonly sharedData: SharedDataService,
    private readonly userService: UserService,
    public dialogService: DialogService,
    private readonly splashScreenService: SplashScreenStateService,
    private readonly storageService: SecureStorageService) {
      super();
    }

  override  ngOnInit(): void {
    this.language = this.storageService.getItemWithoutEncryption('language','en');
    this.sharedData.authModeSubject$.pipe(takeUntil(this.destroy$)).subscribe(authMode => this.authMode = authMode);
    this.userService.isMfaActive().pipe(takeUntil(this.destroy$)).subscribe(response => this.mfaEnabled = response);
  }

  toggleCurrentPasswordDisplay(): void {
    this.showCurrentPassword = !this.showCurrentPassword;
  }

  toggleNew1PasswordDisplay(): void {
    this.showNew1Password = !this.showNew1Password;
  }

  toggleNew2PasswordDisplay(): void {
    this.showNew2Password = !this.showNew2Password;
  }

  save() {
    this.splashScreenService.start();

    this.userService.changeCredentials({
      oldCredential: this.credentialData.oldCredential,
      newCredential: this.credentialData.newCredential1
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.openInfoDialog("settings.password.dialog.title", "settings.password.dialog.text");
        this.splashScreenService.stop();
      },
      error: (err) => this.openWarning(err)
    });
  }

  saveLanguage() {
    this.storageService.setItemWithoutEncryption('language', this.language);
    void this.router.navigate(['/settings']);
  }

  toggleMfa() {
    this.splashScreenService.start();
    this.userService.toggleMfa(this.mfaEnabled).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.openInfoDialog("settings.mfa.dialog.title", "settings.mfa.dialog.text");
        this.splashScreenService.stop();
      },
      error: (err) => this.openWarning(err)
    });
  }

  private openInfoDialog(titleKey: string, textKey: string): void {
    this.dialogService.openNewDialog({ title: titleKey, text: textKey, type: "information" });
  }

  private openWarning(error: any): void {
    this.dialogService.openNewDialog({ text: "settings.error", type: "warning", arg: getErrorMessage(error) });
    this.splashScreenService.stop();
  }
}