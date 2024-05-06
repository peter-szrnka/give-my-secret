import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ActivatedRoute } from "@angular/router";
import { of } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { HelpComponent } from "./help.compontent";
import { ErrorCode } from "./model/error-code.model";

/**
 * @author Peter Szrnka
 */
describe('HelpComponent', () => {

    let component: HelpComponent;
    let activatedRoute : any = {};
    // Fixtures
    let fixture: ComponentFixture<HelpComponent>;

    const configureTestBed = () => {
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
    };

    beforeEach(async () => {
        activatedRoute = class {
            data : any = of({
                data: [
                    { code: "GMS-001", description: "test" } as ErrorCode
                ]
            })
        };
    });

    it('should load component', () => {
        configureTestBed();
        expect(component).toBeTruthy();
        expect(component.datasource).toBeDefined();
    });
});