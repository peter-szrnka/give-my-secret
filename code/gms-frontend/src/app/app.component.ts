import { Component, OnDestroy, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterEvent } from '@angular/router';
import { User } from './components/user/model/user.model';
import { SharedDataService } from './common/service/shared-data-service';
import { SplashScreenStateService } from './common/service/splash-screen-service';
import { Subject, combineLatest, takeUntil } from 'rxjs';
import { roleCheck } from './common/utils/permission-utils';

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

    combineLatest([
      this.sharedDataService.systemReadySubject$,
      this.sharedDataService.userSubject$
    ]).subscribe(([readyData, user]) => {
      this.currentUser = user;

      if (!readyData.ready && ['ldap'].indexOf(readyData.authMode) < 0) {
        void this.router.navigate(['/setup']);
        return;
      }

      if (readyData.status !== 200 || (!this.currentUser && !this.router.url.startsWith(LOGIN_CALLBACK_URL))) {
        void this.router.navigate([LOGIN_CALLBACK_URL], { queryParams: { previousUrl: this.location.path() } });
        return;
      }

      this.systemReady = readyData.ready;
    });

    this.sharedDataService.check();
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
  }

  checkRouterEvent(routerEvent: RouterEvent): void {
    if (routerEvent instanceof NavigationStart) {
      this.splashScreenStateService.start();
    } else if (this.isNavigationEndInstance(routerEvent)) {
       this.splashScreenStateService.stop();
    }
  }

  toggleTextMenuVisibility() : void {
    this.showTexts = !this.showTexts;
    localStorage.setItem('showTextsInSidevNav', this.showTexts)
  }

  isNormalUser(): boolean {
    return this.systemReady && this.isUserDefined() && roleCheck(this.currentUser!!, 'ROLE_USER');
  }

  isAdmin(): boolean {
    return this.systemReady && this.isUserDefined() && roleCheck(this.currentUser!!, 'ROLE_ADMIN');
  }

  private isUserDefined() {
    return this.currentUser !== undefined && this.currentUser !== null;
  }

  private isNavigationEndInstance(routerEvent: RouterEvent): boolean {
    return routerEvent instanceof NavigationEnd || routerEvent instanceof NavigationCancel || routerEvent instanceof NavigationError;
  }
}
