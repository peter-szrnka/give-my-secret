import { Route, RouterModule, Routes } from '@angular/router';
import { ApiKeyDetailComponent } from './components/apikey/apikey-detail.component';
import { ApiKeyListComponent } from './components/apikey/apikey-list.component';
import { ROLE_GUARD } from './common/interceptor/role-guard';
import { EventListComponent } from './components/event/event-list.component';
import { HomeComponent } from './components/home/home.component';
import { KeystoreDetailComponent } from './components/keystore/keystore-detail.component';
import { KeystoreListComponent } from './components/keystore/keystore-list.component';
import { LoginComponent } from './components/login/login.component';
import { SecretDetailComponent } from './components/secret/secret-detail.component';
import { SecretListComponent } from './components/secret/secret-list.component';
import { SetupComponent } from './components/setup/setup.component';
import { UserDetailComponent } from './components/user/user-detail.component';
import { UserListComponent } from './components/user/user-list.component';
import { AnnouncementListComponent } from './components/announcement/announcement-list.component';
import { AnnouncementDetailComponent } from './components/announcement/announcement-detail.component';
import { SettingsSummaryComponent } from './components/settings/settings-summary.component';
import { MessageListComponent } from './components/messages/message-list.component';
import { ApiTestingComponent } from './components/api_testing/api-testing.component';
import { AnnouncementListResolver } from './components/announcement/resolver/announcement-list.resolver';
import { ApiKeyDetailResolver } from './components/apikey/resolver/apikey-detail.resolver';
import { ApiKeyListResolver } from './components/apikey/resolver/apikey-list.resolver';
import { AnnouncementDetailResolver } from './components/announcement/resolver/announcement-detail.resolver';
import { NgModule, Type } from '@angular/core';
import { HomeResolver } from './components/home/resolver/home.resolver';
import { SystemPropertyListComponent } from './components/system_property/system-property-list.component';
import { SystemPropertyListResolver } from './components/system_property/resolver/system-property-list.resolver';
import { EventListResolver } from './components/event/resolver/event-list.resolver';
import { KeystoreDetailResolver } from './components/keystore/resolver/keystore-detail.resolver';
import { KeystoreListResolver } from './components/keystore/resolver/keystore-list.resolver';
import { SecretDetailResolver } from './components/secret/resolver/secret-detail.resolver';
import { SecretListResolver } from './components/secret/resolver/secret-list.resolver';
import { UserDetailResolver } from './components/user/resolver/user-detail.resolver';
import { UserListResolver } from './components/user/resolver/user-list.resolver';

const ROLES_ALL = ['ROLE_USER', 'ROLE_VIEWER', 'ROLE_ADMIN'];
const ROLES_USER_AND_VIEWER = ['ROLE_USER', 'ROLE_VIEWER'];
const ROLES_ADMIN = ['ROLE_ADMIN'];

const listRouteBuilder = (scope : string, component : Type<any>, resolver : Type<any>, roles = ROLES_USER_AND_VIEWER) : Route => {
  return { path: scope + '/list', component : component, data : { 'roles' : roles }, canActivate: [ ROLE_GUARD ], resolve: { 'data' : resolver }, runGuardsAndResolvers: 'always' };
};

const detailRouteBuilder = (scope : string, component : Type<any>, resolver : Type<any>, roles = ROLES_USER_AND_VIEWER) : Route => {
  return { path: scope + '/:id', component : component, data : { 'roles' : roles }, canActivate: [ ROLE_GUARD ], resolve: { 'entity' : resolver } };
};

const routes: Routes = [
  { path: 'setup', component: SetupComponent },
  { path: 'login', component: LoginComponent },

  // Secured components
  { path: '', component: HomeComponent, pathMatch: 'full', data : { 'roles' : ROLES_ALL }, canActivate: [ ROLE_GUARD ], resolve : { 'data' : HomeResolver } },
  listRouteBuilder('secret', SecretListComponent, SecretListResolver),
  detailRouteBuilder('secret', SecretDetailComponent, SecretDetailResolver),
  listRouteBuilder('apikey', ApiKeyListComponent, ApiKeyListResolver),
  detailRouteBuilder('apikey', ApiKeyDetailComponent, ApiKeyDetailResolver),
  listRouteBuilder('keystore', KeystoreListComponent, KeystoreListResolver),
  detailRouteBuilder('keystore', KeystoreDetailComponent, KeystoreDetailResolver),
  { path: 'settings', component: SettingsSummaryComponent, data : {'roles' : ROLES_ALL }, canActivate: [ ROLE_GUARD ] },
  { path: 'api-testing', component: ApiTestingComponent, data: {'roles' : ROLES_ALL }, canActivate: [ ROLE_GUARD ] },

  // Admin functions
  listRouteBuilder('user', UserListComponent, UserListResolver, ROLES_ADMIN),
  detailRouteBuilder('user', UserDetailComponent, UserDetailResolver, ROLES_ADMIN),
  listRouteBuilder('event', EventListComponent, EventListResolver, ROLES_ADMIN),
  listRouteBuilder('announcement', AnnouncementListComponent, AnnouncementListResolver, ROLES_ADMIN),
  detailRouteBuilder('announcement', AnnouncementDetailComponent, AnnouncementDetailResolver, ROLES_ADMIN),
  listRouteBuilder('system_property', SystemPropertyListComponent, SystemPropertyListResolver, ROLES_ADMIN),

  // Common functions
  { path: 'messages', component: MessageListComponent, runGuardsAndResolvers: 'always' },
];

/**
 * @author Peter Szrnka
 */
@NgModule({
  imports: [RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
