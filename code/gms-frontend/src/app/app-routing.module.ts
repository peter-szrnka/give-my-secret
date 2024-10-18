import { NgModule, Type, inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Route, RouterModule, Routes } from '@angular/router';
import { ROLE_GUARD } from './common/interceptor/role-guard';
import { ROLE_ROUTE_MAP } from './common/utils/route-utils';
import { AboutComponent } from './components/about/about.component';
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
import { HelpComponent } from './components/help/help.compontent';
import { ErrorCodeResolver } from './components/help/resolver/error-code.resolver';
import { HomeComponent } from './components/home/home.component';
import { IprestrictionDetailComponent } from './components/ip_restriction/ip-restriction-detail.component';
import { IpRestrictionListComponent } from './components/ip_restriction/ip-restriction-list.component';
import { IpRestrictionDetailResolver } from './components/ip_restriction/resolver/ip-restriction-detail.resolver';
import { IpRestrictionListResolver } from './components/ip_restriction/resolver/ip-restriction-list.resolver';
import { KeystoreDetailComponent } from './components/keystore/keystore-detail.component';
import { KeystoreListComponent } from './components/keystore/keystore-list.component';
import { KeystoreDetailResolver } from './components/keystore/resolver/keystore-detail.resolver';
import { KeystoreListResolver } from './components/keystore/resolver/keystore-list.resolver';
import { LoginComponent } from './components/login/login.component';
import { MessageListComponent } from './components/messages/message-list.component';
import { RequestPasswordResetComponent } from './components/password_reset/request-password-reset.component';
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
import { ErrorComponent } from './components/error/error.component';

const ROLES_ALL = ['ROLE_USER', 'ROLE_VIEWER', 'ROLE_ADMIN'];

const routeBuilder = (routePath: string, resolveKey: string, component: Type<any>, resolver: Type<any>): Route => {
  return {
    path: routePath,
    component: component,
    data: { 'roles': ROLE_ROUTE_MAP[routePath] },
    resolve: { [resolveKey]: (snapshot: ActivatedRouteSnapshot): ResolveFn<Object> => inject(resolver).resolve(snapshot) },
    canActivate: [ROLE_GUARD],
    runGuardsAndResolvers: 'always'
  };
};

const listRouteBuilder = (scope: string, component: Type<any>, resolver: Type<any>): Route => {
  return routeBuilder(scope + '/list', 'data', component, resolver);
};

const detailRouteBuilder = (scope: string, component: Type<any>, resolver: Type<any>): Route => {
  return routeBuilder(scope + '/:id', 'entity', component, resolver);
};

const routes: Routes = [
  { path: 'error', component: ErrorComponent },
  { path: 'setup', component: SetupComponent },
  { path: 'login', component: LoginComponent },
  { path: 'verify', component: VerifyComponent },
  { path: 'password_reset', component: RequestPasswordResetComponent },
  { path: 'about', component: AboutComponent },
  { path: 'help', component: HelpComponent, resolve: { 'data': (snapshot: ActivatedRouteSnapshot) => inject(ErrorCodeResolver).resolve(snapshot) }  },

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
  listRouteBuilder('user', UserListComponent, UserListResolver),
  detailRouteBuilder('user', UserDetailComponent, UserDetailResolver),
  listRouteBuilder('event', EventListComponent, EventListResolver),
  listRouteBuilder('announcement', AnnouncementListComponent, AnnouncementListResolver),
  detailRouteBuilder('announcement', AnnouncementDetailComponent, AnnouncementDetailResolver),
  listRouteBuilder('system_property', SystemPropertyListComponent, SystemPropertyListResolver),
  listRouteBuilder('ip_restriction', IpRestrictionListComponent, IpRestrictionListResolver),
  detailRouteBuilder('ip_restriction', IprestrictionDetailComponent, IpRestrictionDetailResolver),

  // Common functions
  { path: 'messages', component: MessageListComponent },
  // All other unknown routes
  {path: '**', redirectTo: ''},
];

/**
 * @author Peter Szrnka
 */
@NgModule({
  imports: [RouterModule.forRoot(routes,{ enableTracing: false })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
