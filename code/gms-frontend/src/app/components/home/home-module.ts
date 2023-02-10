import { HttpClientModule } from "@angular/common/http";
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AngularMaterialModule } from "../../angular-material-module";
import { AppRoutingModule } from "../../app-routing.module";
import { PipesModule } from "../../common/components/pipes/pipes.module";
import { HomeComponent } from "./home.component";
import { HomeResolver } from "./resolver/home.resolver";

/**
 * @author Peter Szrnka
 */
@NgModule({
    declarations: [ 
      HomeComponent
    ],
    imports: [
        AngularMaterialModule,
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        AppRoutingModule,
        PipesModule
    ],
    providers: [ HomeResolver ]
  })
  export class HomeModule { }