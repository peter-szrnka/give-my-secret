import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialog } from "@angular/material/dialog";
import { ReplaySubject } from "rxjs";
import { SharedDataService } from "../../service/shared-data-service";
import { AutomaticLogoutComponent } from "./automatic-logout.component";
import { InfoDialog } from "../info-dialog/info-dialog.component";

/**
 * @author Peter Szrnka
 */
describe('AutomaticLogoutComponent', () => {
    let component: AutomaticLogoutComponent;
    let fixture: ComponentFixture<AutomaticLogoutComponent>;
    let sharedData: any;
    let mockSubject: ReplaySubject<void> = new ReplaySubject<void>();
    let dialog: Partial<MatDialog>;
  
    beforeEach(async () => {
        sharedData = {
            resetTimerSubject$: mockSubject,
            logout: jest.fn()
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
      component.automaticLogoutTimeInMs = 60000; // Example time for testing
      expect(component).toBeTruthy();

      component.ngOnInit();
      mockSubject.next();
      expect(component.logoutComing).toBeFalsy();

      fixture.detectChanges();
      jest.advanceTimersByTime(30000);

      jest.advanceTimersByTime(30000);
  
      expect(component.logoutComing).toBeTruthy();
      expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { title: 'Automatic Logout', text: 'You have been logged out due to inactivity.', type: 'information' } });
      expect(sharedData.logout).toHaveBeenCalled();
    });
  });