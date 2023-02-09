import { HttpClientModule } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { GmsComponentsModule } from "../components/gms-components-module";
import { AnnouncementDetailResolver } from "./announcement-detail.resolver";
import { AnnouncementListResolver } from "./announcement-list.resolver";
import { HomeResolver } from "./home.resolver";
import { KeystoreDetailResolver } from "./keystore-detail.resolver";
import { KeystoreListResolver } from "./keystore-list.resolver";
import { SecretDetailResolver } from "./secret-detail.resolver";
import { SecretListResolver } from "./secret-list.resolver";
import { UserDetailResolver } from "./user-detail.resolver";
import { UserListResolver } from "./user-list.resolver";
import { SystemPropertyListResolver } from "./system-property-list.resolver";

/**
 * @author Peter Szrnka
 */
@NgModule({
    imports: [
        AngularMaterialModule,
        HttpClientModule,
        AppRoutingModule,
        GmsComponentsModule
    ],
    providers: [ 
      HomeResolver,
      AnnouncementListResolver,
      AnnouncementDetailResolver,
      KeystoreListResolver,
      KeystoreDetailResolver,
      SecretListResolver,
      SecretDetailResolver,
      UserListResolver,
      UserDetailResolver,
      SystemPropertyListResolver
    ]
  })
export class ResolverModule {}