import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslatorModule } from '../../common/components/pipes/translator/translator.module';
import { SharedDataService } from '../../common/service/shared-data-service';
import { NavMenuComponent } from './nav-menu.component';

/**
 * @author Peter Szrnka
 */
describe('NavMenuComponent', () => {
    let component : NavMenuComponent;
    let fixture : ComponentFixture<NavMenuComponent>;
    let sharedDataService : any;
    let mockShowLargeMenuEvent : any;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            imports: [TranslatorModule],
            declarations : [NavMenuComponent],
            providers: [
                { provide : SharedDataService, useValue : sharedDataService }
            ],
            schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
        });

        fixture = TestBed.createComponent(NavMenuComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    beforeEach(() => {
        mockShowLargeMenuEvent = {
            emit : jest.fn()
        };

        sharedDataService = {
            showLargeMenuEvent : mockShowLargeMenuEvent
        };
    });

    it('should query unread messages', () => {
        configTestBed();

        component.admin = false;
        component.enableBottomToggle = false;
        component.showTexts = false;

        // act
        component.handleClick();
        component.toggleTextMenuVisibility();

        // assert
        expect(component.admin).toBeFalsy();
        expect(mockShowLargeMenuEvent.emit).toHaveBeenCalledWith(false);
        expect(component.showTexts).toBeTruthy();
        expect(localStorage.getItem('showTextsInSidevNav')).toEqual('true');
    });
});