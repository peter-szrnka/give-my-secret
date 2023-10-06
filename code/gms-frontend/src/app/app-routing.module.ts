import { NgModule, Type, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Route, RouterModule, Routes } from '@angular/router';
import { ROLE_GUARD } from './common/interceptor/role-guard';
import { AnnouncementDetailComponent } from './components/announcement/announcement-detail.component';
import { AnnouncementListComponent } from './components/announcement/announcement-list.component';
import { AnnouncementDetailResolver } from './components/announcement/resolver/announcement-detail.resolver';
import { AnnouncementListResolver } from './components/announcement/resolver/announcement-list.resolver';
import { ApiTestingComponent } from './components/api_testing/api-testing.component';
import { ApiKeyDetailComponent } from './components/apikey/apikey-detail.component';
import { ApiKeyListComponent } from './components/apikey/apikey-list.component';
import { ApiKeyDetailResolver } from './components/apikey/resolver/apikey-detail.resolver';
import { ApiKeyListResolver } from './components/apikey/resolver/apikey-list.resolver';
import { EventListComponent } from './components/event/event-list.component';
import { EventListResolver } from './components/event/resolver/event-list.resolver';
import { HomeComponent } from './components/home/home.component';
import { KeystoreDetailComponent } from './components/keystore/keystore-detail.component';
import { KeystoreListComponent } from './components/keystore/keystore-list.component';
import { KeystoreDetailResolver } from './components/keystore/resolver/keystore-detail.resolver';
import { KeystoreListResolver } from './components/keystore/resolver/keystore-list.resolver';
import { LoginComponent } from './components/login/login.component';
import { MessageListComponent } from './components/messages/message-list.component';
import { SecretDetailResolver } from './components/secret/resolver/secret-detail.resolver';
import { SecretListResolver } from './components/secret/resolver/secret-list.resolver';
import { SecretDetailComponent } from './components/secret/secret-detail.component';
import { SecretListComponent } from './components/secret/secret-list.component';
import { SettingsSummaryComponent } from './components/settings/settings-summary.component';
import { SetupComponent } from './components/setup/setup.component';
import { SystemPropertyListResolver } from './components/system_property/resolver/system-property-list.resolver';
import { SystemPropertyListComponent } from './components/system_property/system-property-list.component';
import { UserDetailResolver } from './components/user/resolver/user-detail.resolver';
import { UserListResolver } from './components/user/resolver/user-list.resolver';
import { UserDetailComponent } from './components/user/user-detail.component';
import { UserListComponent } from './components/user/user-list.component';
import { VerifyComponent } from './components/verify/verify.component';

const ROLES_ALL = ['ROLE_USER', 'ROLE_VIEWER', 'ROLE_ADMIN'];
const ROLES_USER_AND_VIEWER = ['ROLE_USER', 'ROLE_VIEWER'];
const ROLES_ADMIN = ['ROLE_ADMIN'];

const routeBuilder = (routePath: string, resolveKey: string, component: Type<any>, resolver: Type<any>, roles = ROLES_USER_AND_VIEWER): Route => {
  return {
    path: routePath,
    component: component,
    data: { 'roles': roles },
    resolve: { [resolveKey]: (snapshot: ActivatedRouteSnapshot) => inject(resolver).resolve(snapshot) },
    canActivate: [ROLE_GUARD],
    runGuardsAndResolvers: 'always'
  };
};

const listRouteBuilder = (scope: string, component: Type<any>, resolver: Type<any>, roles = ROLES_USER_AND_VIEWER): Route => {
  return routeBuilder(scope + '/list', 'data', component, resolver, roles);
};

const detailRouteBuilder = (scope: string, component: Type<any>, resolver: Type<any>, roles = ROLES_USER_AND_VIEWER): Route => {
  return routeBuilder(scope + '/:id', 'entity', component, resolver, roles);
};

const routes: Routes = [
  { path: 'setup', component: SetupComponent },
  { path: 'login', component: LoginComponent },
  { path: 'verify', component: VerifyComponent },

  // Secured components
  { path: '', component: HomeComponent, pathMatch: 'full', data: { 'roles': ROLES_ALL } },
  listRouteBuilder('secret', SecretListComponent, SecretListResolver),
  detailRouteBuilder('secret', SecretDetailComponent, SecretDetailResolver),
  listRouteBuilder('apikey', ApiKeyListComponent, ApiKeyListResolver),
  detailRouteBuilder('apikey', ApiKeyDetailComponent, ApiKeyDetailResolver),
  listRouteBuilder('keystore', KeystoreListComponent, KeystoreListResolver),
  detailRouteBuilder('keystore', KeystoreDetailComponent, KeystoreDetailResolver),
  { path: 'settings', component: SettingsSummaryComponent, data: { 'roles': ROLES_ALL }, canActivate: [ROLE_GUARD] },
  { path: 'api-testing', component: ApiTestingComponent, data: { 'roles': ROLES_ALL }, canActivate: [ROLE_GUARD] },

  // Admin functions
  listRouteBuilder('user', UserListComponent, UserListResolver, ROLES_ADMIN),
  detailRouteBuilder('user', UserDetailComponent, UserDetailResolver, ROLES_ADMIN),
  listRouteBuilder('event', EventListComponent, EventListResolver, ROLES_ADMIN),
  listRouteBuilder('announcement', AnnouncementListComponent, AnnouncementListResolver, ROLES_ADMIN),
  detailRouteBuilder('announcement', AnnouncementDetailComponent, AnnouncementDetailResolver, ROLES_ADMIN),
  listRouteBuilder('system_property', SystemPropertyListComponent, SystemPropertyListResolver, ROLES_ADMIN),

  // Common functions
  { path: 'messages', component: MessageListComponent },
];

/**
 * @author Peter Szrnka
 */
@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
