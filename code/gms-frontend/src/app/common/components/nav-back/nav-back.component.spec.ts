import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";
import { AngularMaterialModule } from "../../../angular-material-module";
import { NavBackComponent } from "./nav-back.component";

describe('NavBackComponent', () => {
    let component : NavBackComponent;

    // Fixtures
    let fixture : ComponentFixture<NavBackComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, AngularMaterialModule ],
            declarations : [NavBackComponent],
            schemas : [CUSTOM_ELEMENTS_SCHEMA]
        });
    });

    it('Should create component', () => {
        fixture = TestBed.createComponent(NavBackComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        component.backToHome = true;
        component.buttonConfig = [
            { label : "Button1", primary : false, url : "http://localhost:8080/" },
            { label : "Button2", primary : false, url : "http://localhost:8080/" },
            { label : "Button3", primary : false, url : "http://localhost:8080/" }
        ];

        expect(component).toBeTruthy();
    });
});