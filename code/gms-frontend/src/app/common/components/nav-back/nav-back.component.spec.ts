import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { AngularMaterialModule } from "../../../angular-material-module";
import { NavBackComponent } from "./nav-back.component";
import { PipesModule } from "../pipes/pipes.module";

/**
 * @author Peter Szrnka
 */
describe('NavBackComponent', () => {
    let component : NavBackComponent;

    // Fixtures
    let fixture : ComponentFixture<NavBackComponent>;

    beforeEach(() => {
       TestBed.configureTestingModule({
            imports : [RouterTestingModule, AngularMaterialModule, PipesModule ],
            declarations : [NavBackComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA]
        });

        fixture = TestBed.createComponent(NavBackComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('Should create component', () => {
        

        component.backToHome = true;
        component.buttonConfig = [
            { label : "Button1", primary : false, url : "http://localhost:8080/" },
            { label : "Button2", primary : false, url : "http://localhost:8080/", visibilityCondition: true },
            { label : "Button3", primary : false, url : "http://localhost:8080/", visibilityCondition: false }
        ];

        expect(component).toBeTruthy();
    });
});