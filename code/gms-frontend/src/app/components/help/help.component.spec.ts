import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { AngularMaterialModule } from "../../angular-material-module";
import { HelpComponent } from "./help.compontent";
import { ActivatedRoute } from "@angular/router";
import { ErrorCode } from "./model/error-code.model";
import { of } from "rxjs";

/**
 * @author Peter Szrnka
 */
describe('HelpComponent', () => {

    let component: HelpComponent;
    let activatedRoute : any = {};
    // Fixtures
    let fixture: ComponentFixture<HelpComponent>;

    beforeEach(async () => {
        activatedRoute = class {
            data : ErrorCode[] = of([])
        };

        TestBed.configureTestingModule({
            imports: [AngularMaterialModule],
            declarations : [HelpComponent],
            schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : ActivatedRoute, useClass : activatedRoute }
            ]
        }).compileComponents();
        fixture = TestBed.createComponent(HelpComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should load component', () => {
        expect(component).toBeTruthy();
    });
});