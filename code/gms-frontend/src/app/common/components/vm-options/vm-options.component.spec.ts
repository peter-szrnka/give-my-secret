import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { of } from "rxjs";
import { VmOption } from "../../model/common.model";
import { InformationService } from "../../service/info-service";
import { VmOptionsComponent } from "./vm-options.component";

/**
 * @author Peter Szrnka
 */
describe('VmOptionsComponent', () => {
    let component: VmOptionsComponent;
    let informationService: any;
    // Fixtures
    let fixture: ComponentFixture<VmOptionsComponent>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [VmOptionsComponent, NoopAnimationsModule],
            declarations: [],
            schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide: InformationService, useValue: informationService }
            ]
        });

        fixture = TestBed.createComponent(VmOptionsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        informationService = {
            getVmOptions: jest.fn().mockReturnValue(of([
                { key: 'k1', value: 'value1' } as VmOption,
                { key: 'k2', value: 'value2' } as VmOption,
            ])),
        };
    });

    it('Should return data', () => {
        // arrange
        configureTestBed();

        component.applyFilter({ target: { value: 'a' }});

        // act &assert
        expect(component).toBeTruthy();
        expect(informationService.getVmOptions).toHaveBeenCalled();
    });
});