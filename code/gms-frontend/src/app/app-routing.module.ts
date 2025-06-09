import { NgModule, Type, inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Route, RouterModule, Routes } from '@angular/router';
import { ROLE_GUARD } from './common/interceptor/role-guard';
import { ROLE_ROUTE_MAP } from './common/utils/route-utils';
import { AnnouncementDetailComponent } from './components/announcement/announcement-detail.component';
import { AnnouncementListComponent } from './components/announcement/announcement-list.component';
import { AnnouncementDetailResolver } from './components/announcement/resolver/announcement-detail.resolver';
import { AnnouncementListResolver } from './components/announcement/resolver/announcement-list.resolver';
import { ApiKeyDetailResolver } from './components/apikey/resolver/apikey-detail.resolver';
import { ApiKeyListResolver } from './components/apikey/resolver/apikey-list.resolver';
import { EventListComponent } from './components/event/event-list.component';
import { EventListResolver } from './components/event/resolver/event-list.resolver';
import { ErrorCodeResolver } from './components/help/resolver/error-code.resolver';
import { IprestrictionDetailComponent } from './components/ip_restriction/ip-restriction-detail.component';
import { IpRestrictionListComponent } from './components/ip_restriction/ip-restriction-list.component';
import { IpRestrictionDetailResolver } from './components/ip_restriction/resolver/ip-restriction-detail.resolver';
import { IpRestrictionListResolver } from './components/ip_restriction/resolver/ip-restriction-list.resolver';
import { JobDetailListComponent } from './components/job/job-detail-list.component';
import { JobDetailListResolver } from './components/job/resolver/job-detail-list.resolver';
import { KeystoreDetailResolver } from './components/keystore/resolver/keystore-detail.resolver';
import { KeystoreListResolver } from './components/keystore/resolver/keystore-list.resolver';
import { SecretDetailResolver } from './components/secret/resolver/secret-detail.resolver';
import { SecretListResolver } from './components/secret/resolver/secret-list.resolver';
import { SystemPropertyListResolver } from './components/system_property/resolver/system-property-list.resolver';
import { SystemPropertyListComponent } from './components/system_property/system-property-list.component';
import { UserDetailResolver } from './components/user/resolver/user-detail.resolver';
import { UserListResolver } from './components/user/resolver/user-list.resolver';
import { UserDetailComponent } from './components/user/user-detail.component';
import { UserListComponent } from './components/user/user-list.component';
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

const routeBuilderFn = (routePath: string, resolveKey: string, componentLoader: any, resolver: Type<any>): Route => {
  return {
    path: routePath,
    loadComponent: () => componentLoader,
    data: { 'roles': ROLE_ROUTE_MAP[routePath] },
    resolve: { [resolveKey]: (snapshot: ActivatedRouteSnapshot): ResolveFn<Object> => inject(resolver).resolve(snapshot) },
    canActivate: [ROLE_GUARD],
    runGuardsAndResolvers: 'always'
  };
};

const listRouteBuilderFn = (scope: string, componentLoader: any, resolver: Type<any>): Route => {
  return routeBuilderFn(scope + '/list', 'data', componentLoader, resolver);
};

const detailRouteBuilderFn = (scope: string, componentLoader: any, resolver: Type<any>): Route => {
  return routeBuilderFn(scope + '/:id', 'entity', componentLoader, resolver);
};

const routes: Routes = [
  { path: 'error', loadComponent: () => import('./components/error/error.component').then(c => c.ErrorComponent) },
  { path: 'setup', loadComponent: () => import('./components/setup/setup.component').then(c => c.SetupComponent) },
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(c => c.LoginComponent) },
  { path: 'verify', loadComponent: () => import('./components/verify/verify.component').then(c => c.VerifyComponent) },
  { path: 'password_reset', loadComponent: () => import('./components/password_reset/request-password-reset.component').then(c => c.RequestPasswordResetComponent) },
  { path: 'about', loadComponent: () => import('./components/about/about.component').then(c => c.AboutComponent) },
  { path: 'help', loadComponent: () => import('./components/help/help.compontent').then(c => c.HelpComponent), resolve: { 'data': (snapshot: ActivatedRouteSnapshot) => inject(ErrorCodeResolver).resolve(snapshot) }  },

  // Secured components
  { path: '', loadComponent: () => import('./components/home/home.component').then(c => c.HomeComponent), pathMatch: 'full', data: { 'roles': ROLES_ALL } },
  listRouteBuilderFn('secret', import('./components/secret/secret-list.component').then(c => c.SecretListComponent), SecretListResolver),
  detailRouteBuilderFn('secret', import('./components/secret/secret-detail.component').then(c => c.SecretDetailComponent), SecretDetailResolver),
  listRouteBuilderFn('apikey', import('./components/apikey/apikey-list.component').then(c => c.ApiKeyListComponent), ApiKeyListResolver),
  detailRouteBuilderFn('apikey', import('./components/apikey/apikey-detail.component').then(c => c.ApiKeyDetailComponent), ApiKeyDetailResolver),
  listRouteBuilderFn('keystore', import('./components/keystore/keystore-list.component').then(c => c.KeystoreListComponent), KeystoreListResolver),
  detailRouteBuilderFn('keystore', import('./components/keystore/keystore-detail.component').then(c => c.KeystoreDetailComponent), KeystoreDetailResolver),
  { path: 'settings', loadComponent: () => import('./components/settings/settings-summary.component').then(c => c.SettingsSummaryComponent), data: { 'roles': ROLES_ALL }, canActivate: [ROLE_GUARD] },
  { path: 'api-testing', loadComponent: () => import('./components/api_testing/api-testing.component').then(c => c.ApiTestingComponent), data: { 'roles': ROLES_ALL }, canActivate: [ROLE_GUARD] },

  // Admin functions
  listRouteBuilder('user', UserListComponent, UserListResolver),
  detailRouteBuilder('user', UserDetailComponent, UserDetailResolver),
  listRouteBuilder('event', EventListComponent, EventListResolver),
  listRouteBuilder('announcement', AnnouncementListComponent, AnnouncementListResolver),
  detailRouteBuilder('announcement', AnnouncementDetailComponent, AnnouncementDetailResolver),
  listRouteBuilder('system_property', SystemPropertyListComponent, SystemPropertyListResolver),
  listRouteBuilder('ip_restriction', IpRestrictionListComponent, IpRestrictionListResolver),
  detailRouteBuilder('ip_restriction', IprestrictionDetailComponent, IpRestrictionDetailResolver),
  listRouteBuilder('job', JobDetailListComponent, JobDetailListResolver),

  // Common functions
  { path: 'messages', loadComponent: () => import('./components/messages/message-list.component').then(c => c.MessageListComponent) },
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
