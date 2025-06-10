import { Location } from '@angular/common';
import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit } from '@angular/core';
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterEvent, RouterModule } from '@angular/router';
import { takeUntil } from 'rxjs';
import { AngularMaterialModule } from './angular-material-module';
import { BaseComponent } from './common/components/abstractions/component/base.component';
import { SharedDataService } from './common/service/shared-data-service';
import { SplashScreenStateService } from './common/service/splash-screen-service';
import { roleCheck } from './common/utils/permission-utils';
import { HeaderModule } from './components/header/header-module';
import { NavMenuModule } from './components/menu/nav-menu.module';
import { User } from './components/user/model/user.model';

const LOGIN_CALLBACK_URL = '/login';

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    schemas: [CUSTOM_ELEMENTS_SCHEMA], 
    imports: [
        AngularMaterialModule,
        NavMenuModule,
        HeaderModule,
        RouterModule
    ]
})
export class AppComponent extends BaseComponent implements OnInit {

  currentUser: User | undefined;
  showTexts = JSON.parse(localStorage.getItem('showTextsInSidevNav') ?? 'true');
  isAdmin: boolean;

  constructor(
    private readonly location: Location,
    private readonly router: Router, 
    public sharedDataService: SharedDataService, 
    private readonly splashScreenStateService: SplashScreenStateService,
    ) {
      super();
  }

  ngOnInit(): void {
    this.splashScreenStateService.start();
    this.sharedDataService.navigationChangeEvent.pipe(takeUntil(this.destroy$)).subscribe(newUrl => this.navigateTo(newUrl));
    this.router.events.pipe(takeUntil(this.destroy$))
      .subscribe((routerEvent) => this.checkRouterEvent(routerEvent as RouterEvent));

      this.sharedDataService.systemReadySubject$.pipe(takeUntil(this.destroy$)).subscribe(readyData => {

        if (readyData.status === 0 && readyData.ready === false) {
          this.router.navigate(['/error']);
          return;
        }

        if (readyData.status !== 200) {
          return;
        }

        if (!readyData.ready && ['ldap'].indexOf(readyData.authMode) < 0) {
          void this.router.navigate(['/setup'], { queryParams: { systemStatus: readyData.systemStatus } });
          return;
        }

        this.processUserSubject();
      });

    this.sharedDataService.check();
  }

  private processUserSubject() : void {
    this.sharedDataService.userSubject$.asObservable().pipe(takeUntil(this.destroy$)).subscribe(user => {
      this.currentUser = user;
      this.isAdmin = !this.currentUser ? false : roleCheck(this.currentUser, 'ROLE_ADMIN');

      if (!this.currentUser && (!this.router.url.startsWith(LOGIN_CALLBACK_URL))) {
        this.navigateToLogin();
        return;
      }

      if (this.currentUser && this.router.url.startsWith(LOGIN_CALLBACK_URL) && this.router.url.indexOf("previousUrl") === -1) {
        this.router.navigate(['']);
      }
    });
  }

  toggleTextMenuVisibility() : void {
    this.showTexts = !this.showTexts;
    localStorage.setItem('showTextsInSidevNav', this.showTexts)
  }

  private navigateTo(newUrl : string): void {
    void this.router.navigate([newUrl]);
  }

  private navigateToLogin(): void {
    const locationPath = this.location.path();

    if (locationPath === '' || locationPath.indexOf('setup?systemStatus=') > -1) {
      void this.router.navigate([LOGIN_CALLBACK_URL]);
    } else {
      void this.router.navigate([LOGIN_CALLBACK_URL], { queryParams: { previousUrl: locationPath } });
    }
  }

  private checkRouterEvent(routerEvent: RouterEvent): void {
    if (routerEvent instanceof NavigationStart) {
      this.splashScreenStateService.start();
    } else if (this.isNavigationEndInstance(routerEvent)) {
      this.sharedDataService.resetAutomaticLogoutTimer();
       this.splashScreenStateService.stop();
    }
  }

  private isNavigationEndInstance(routerEvent: RouterEvent): boolean {
    return routerEvent instanceof NavigationEnd || routerEvent instanceof NavigationCancel || routerEvent instanceof NavigationError;
  }
}
