import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from './components/user/model/user.model';
import { SharedDataService } from './common/service/shared-data-service';
import { SplashScreenStateService } from './common/service/splash-screen-service';
import { combineLatest } from 'rxjs';

const LOGIN_CALLBACK_URL = '/login';

/**
 * @author Peter Szrnka
 */
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  currentUser: User | undefined;
  systemReady: boolean;
  showTexts = JSON.parse(localStorage.getItem('showTextsInSidevNav') || 'true');

  constructor(
    private router: Router, 
    public sharedDataService: SharedDataService, 
    private splashScreenStateService: SplashScreenStateService) {
  }

  ngOnInit(): void {
    this.splashScreenStateService.start();
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
        void this.router.navigate([LOGIN_CALLBACK_URL]);
        return;
      }

      this.systemReady = readyData.ready;
    });

    this.sharedDataService.check();
  }

  toggleTextMenuVisibility() : void {
    this.showTexts = !this.showTexts;
    localStorage.setItem('showTextsInSidevNav', this.showTexts)
  }

  isNormalUser(): boolean {
    return this.systemReady && this.currentUser !== undefined && this.roleCheck(this.currentUser, 'ROLE_USER');
  }

  isAdmin(): boolean {
    return this.systemReady && this.currentUser !== undefined && this.roleCheck(this.currentUser, 'ROLE_ADMIN');
  }

  roleCheck(currentUser: User, roleName: string): boolean {
    return currentUser !== undefined && currentUser.roles !== undefined && currentUser.roles.filter(role => role === roleName).length > 0;
  }
}
