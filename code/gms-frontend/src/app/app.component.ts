import { Location } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterEvent } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { SharedDataService } from './common/service/shared-data-service';
import { SplashScreenStateService } from './common/service/splash-screen-service';
import { roleCheck } from './common/utils/permission-utils';
import { User } from './components/user/model/user.model';

const LOGIN_CALLBACK_URL = '/login';

/**
 * @author Peter Szrnka
 */
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {

  unsubscribe = new Subject<void>();
  currentUser: User | undefined;
  systemReady: boolean;
  showTexts = JSON.parse(localStorage.getItem('showTextsInSidevNav') ?? 'true');
  isAdmin: boolean;

  constructor(
    private location: Location,
    private router: Router, 
    public sharedDataService: SharedDataService, 
    private splashScreenStateService: SplashScreenStateService) {
  }

  ngOnInit(): void {
    this.splashScreenStateService.start();
    this.router.events.pipe(takeUntil(this.unsubscribe))
      .subscribe((routerEvent) => this.checkRouterEvent(routerEvent as RouterEvent));

      this.sharedDataService.systemReadySubject$.subscribe(readyData => {
        if (readyData.status !== 200) {
          this.navigateToLogin();
          return;
        }

        if (!readyData.ready && ['ldap'].indexOf(readyData.authMode) < 0) {
          void this.router.navigate(['/setup']);
          return;
        }
  
        this.systemReady = readyData.ready;
      });

      this.sharedDataService.userSubject$.subscribe(user => {
        if ((!user || !user.roles) && (!this.router.url.startsWith(LOGIN_CALLBACK_URL))) {
          this.navigateToLogin();
          return;
        }

        this.currentUser = user;
        this.isAdmin = this.currentUser!! && roleCheck(this.currentUser, 'ROLE_ADMIN');
      });

    this.sharedDataService.check();
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
  }

  toggleTextMenuVisibility() : void {
    this.showTexts = !this.showTexts;
    localStorage.setItem('showTextsInSidevNav', this.showTexts)
  }

  private navigateToLogin(): void {
    // FIXME previousUrl temporary disabled!
    void this.router.navigate([LOGIN_CALLBACK_URL]/*, { queryParams: { previousUrl: this.location.path() } }*/);
  }

  private checkRouterEvent(routerEvent: RouterEvent): void {
    if (routerEvent instanceof NavigationStart) {
      this.splashScreenStateService.start();
    } else if (this.isNavigationEndInstance(routerEvent)) {
       this.splashScreenStateService.stop();
    }
  }

  private isNavigationEndInstance(routerEvent: RouterEvent): boolean {
    return routerEvent instanceof NavigationEnd || routerEvent instanceof NavigationCancel || routerEvent instanceof NavigationError;
  }
}
