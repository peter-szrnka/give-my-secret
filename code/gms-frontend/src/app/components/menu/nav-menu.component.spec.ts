import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SharedDataService } from '../../common/service/shared-data-service';
import { LoginComponent } from '../login/login.component';
import { NavMenuComponent } from './nav-menu.component';

describe('NavMenuComponent', () => {
    let component : NavMenuComponent;
    let fixture : ComponentFixture<NavMenuComponent>;
    let sharedDataService : any;
    let mockShowLargeMenuEvent : any;

    const configTestBed = () => {
        TestBed.configureTestingModule({
            declarations : [LoginComponent],
            providers: [
                { provide : SharedDataService, useValue : sharedDataService }
            ]
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
        expect(component.isAdmin()).toBeFalsy();
        expect(mockShowLargeMenuEvent.emit).toHaveBeenCalledWith(false);
        expect(component.showTexts).toBeTruthy();
        expect(localStorage.getItem('showTextsInSidevNav')).toEqual('true');
    });
});