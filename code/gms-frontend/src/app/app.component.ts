import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SystemReadyData } from './common/model/system-ready.model';
import { User } from './common/model/user.model';
import { SharedDataService } from './common/service/shared-data-service';
import { SplashScreenStateService } from './common/service/splash-screen-service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  currentUser: User | undefined;
  systemReady: boolean;

  constructor(private router: Router, public sharedDataService: SharedDataService, private splashScreenStateService: SplashScreenStateService) {
  }

  ngOnInit(): void {
    this.splashScreenStateService.start();
    this.sharedDataService.userSubject$.subscribe((data : User | undefined) => {
      this.currentUser = data;
    });

    this.sharedDataService.systemReadySubject$.subscribe((readyData : SystemReadyData) => {
      if (readyData.status !== 200) {
        this.router.navigate(['/login']);
        return;
      }

      this.systemReady = readyData.ready;
      if (readyData.ready || this.sharedDataService.authMode === 'ldap') {
        return;
      }
      this.router.navigate(['/setup']);
    });

    this.sharedDataService.check();

    if (this.sharedDataService.getUserInfo() === undefined) {
        this.router.navigate(['/login']);
        return;
    }
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
