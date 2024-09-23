import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialog } from "@angular/material/dialog";
import { ReplaySubject } from "rxjs";
import { SharedDataService } from "../../service/shared-data-service";
import { AutomaticLogoutComponent, WARNING_THRESHOLD } from "./automatic-logout.component";
import { InfoDialog } from "../info-dialog/info-dialog.component";

/**
 * @author Peter Szrnka
 */
describe('AutomaticLogoutComponent', () => {
    let component: AutomaticLogoutComponent;
    let fixture: ComponentFixture<AutomaticLogoutComponent>;
    let sharedData: any;
    let mockSubject: ReplaySubject<number | undefined> = new ReplaySubject<number | undefined>();
    let dialog: Partial<MatDialog>;
  
    beforeEach(async () => {
        sharedData = {
            resetTimerSubject$: mockSubject,
            logout: jest.fn(),
            setStartTime: jest.fn(),
            resetAutomaticLogoutTimer: jest.fn()
        };
  
      dialog = {
        open: jest.fn()
      };
  
      await TestBed.configureTestingModule({
        declarations: [ AutomaticLogoutComponent ],
        providers: [
          { provide: SharedDataService, useValue: sharedData },
          { provide: MatDialog, useValue: dialog }
        ]
      })
      .compileComponents();
      jest.useFakeTimers();

      fixture = TestBed.createComponent(AutomaticLogoutComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });
  
    afterEach(() => {
      // Clean up any subscriptions or timers
      if (component.timeLeftSubscription) {
        component.timeLeftSubscription.unsubscribe();
      }
    });
  
    it('should set logoutComing to true on warning before logout', async () => {
      component.automaticLogoutTimeInMinutes = 2;
      expect(component).toBeTruthy();

      component.ngOnInit();
      mockSubject.next(undefined);
      expect(component.logoutComing).toBeFalsy();

      fixture.detectChanges();
      jest.advanceTimersByTime(WARNING_THRESHOLD);

      jest.advanceTimersByTime(WARNING_THRESHOLD);
  
      expect(component.logoutComing).toBeTruthy();
      expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { title: 'Automatic Logout', text: 'You have been logged out due to inactivity.', type: 'information' } });
      expect(sharedData.logout).toHaveBeenCalled();
    });

    it('should logout when time expired and app was in the background', async () => {
      component.automaticLogoutTimeInMinutes = 2;
      expect(component).toBeTruthy();

      component.ngOnInit();
      mockSubject.next(undefined);
      expect(component.logoutComing).toBeFalsy();

      fixture.detectChanges();
      jest.advanceTimersByTime(WARNING_THRESHOLD);

      mockSubject.next(120001);

      jest.advanceTimersByTime(WARNING_THRESHOLD);

      expect(component.logoutComing).toBeTruthy();
      expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { title: 'Automatic Logout', text: 'You have been logged out due to inactivity.', type: 'information' } });
      expect(sharedData.logout).toHaveBeenCalled();
    });

    it('should resetLogoutTimer', async () => {
      component.resetLogoutTimer();
      
      expect(sharedData.resetAutomaticLogoutTimer).toHaveBeenCalledWith(false);
    });
  });