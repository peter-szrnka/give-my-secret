import { NgFor, NgIf } from "@angular/common";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { AngularMaterialModule } from "../../../angular-material-module";
import { NavButtonVisibilityPipe } from "../pipes/nav-button-visibility.pipe";
import { NavBackComponent } from "./nav-back.component";

/**
 * @author Peter Szrnka
 */
describe('NavBackComponent', () => {
    let component : NavBackComponent;

    // Fixtures
    let fixture : ComponentFixture<NavBackComponent>;

    beforeEach(() => {
       TestBed.configureTestingModule({
            imports : [ NavBackComponent, AngularMaterialModule,
                NavButtonVisibilityPipe,
                NgIf, NgFor,
                RouterLink
            ],
            providers : [
                { provide: ActivatedRoute, useValue: { snapshot: { url: [ { path: 'test' } ] } } }
            ],
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