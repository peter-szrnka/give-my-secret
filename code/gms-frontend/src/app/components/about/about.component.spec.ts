import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { of } from "rxjs";
import { AngularMaterialModule } from "../../angular-material-module";
import { SystemStatusDto } from "../../common/model/system-status.model";
import { SetupService } from "../setup/service/setup-service";
import { AboutComponent } from "./about.component";

/**
 * @author Peter Szrnka
 */
describe('AboutComponent', () => {

    let component: AboutComponent;
    let setupService: any;
    // Fixtures
    let fixture: ComponentFixture<AboutComponent>;
    const mockStatus: SystemStatusDto = { authMode:'db', status:'OK', version: '1.0.0', built: '2024-04-09T12:34:56.000Z', containerId: '1234567', containerHostType: 'DOCKER' };

    beforeEach(async () => {
        setupService = {
            checkReady: jest.fn().mockReturnValue(of(mockStatus))
        };

        TestBed.configureTestingModule({
            imports: [AngularMaterialModule, AboutComponent],
            schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide: SetupService, useValue: setupService }
            ]
        }).compileComponents();
        fixture = TestBed.createComponent(AboutComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should load component', () => {
        expect(component).toBeTruthy();
        expect(component.systemStatus).toEqual(mockStatus);
        expect(setupService.checkReady).toHaveBeenCalled();
    });
});