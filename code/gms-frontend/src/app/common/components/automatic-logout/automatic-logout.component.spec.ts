import { DatePipe, NgClass } from "@angular/common";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatTooltipModule } from "@angular/material/tooltip";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { of, ReplaySubject } from "rxjs";
import { DialogService } from "../../service/dialog-service";
import { SharedDataService } from "../../service/shared-data-service";
import { AutomaticLogoutComponent, WARNING_THRESHOLD } from "./automatic-logout.component";
import { vi } from "vitest";

/**
 * @author Peter Szrnka
 */
describe.skip('AutomaticLogoutComponent', () => {
  let component: AutomaticLogoutComponent;
  let fixture: ComponentFixture<AutomaticLogoutComponent>;
  let sharedData: any;
  let mockSubject: ReplaySubject<number | undefined> = new ReplaySubject<number | undefined>();
  let dialogService: any;

  beforeEach(() => {
    sharedData = {
      resetTimerSubject$: mockSubject,
      logout: vi.fn(),
      setStartTime: vi.fn()
    };

    dialogService = {
      openNewDialog : vi.fn().mockReturnValue({ afterClosed : vi.fn().mockReturnValue(of(true)) } as any)
    };

     TestBed.configureTestingModule({
      imports: [AutomaticLogoutComponent, MatTooltipModule, NoopAnimationsModule, DatePipe, NgClass],
      providers: [
        { provide: SharedDataService, useValue: sharedData },
        { provide: DialogService, useValue: dialogService }
      ],
      schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
    });
    vi.useFakeTimers();

    fixture = TestBed.createComponent(AutomaticLogoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should set logoutComing to true on warning before logout', () => {
    vi.useFakeTimers();

    component.automaticLogoutTimeInMinutes = 2;
    fixture.detectChanges();

    mockSubject.next(undefined);
    fixture.detectChanges();

    expect(component.logoutComing).toBeFalsy();

    vi.advanceTimersByTime(WARNING_THRESHOLD);
    vi.advanceTimersByTime(WARNING_THRESHOLD);

    fixture.detectChanges();

    expect(component.logoutComing).toBeTruthy();
    expect(dialogService.openNewDialog).toHaveBeenCalledWith({
      title: "automaticLogout.title",
      text: "automaticLogout.logout",
      type: "information"
    });

    component.ngOnDestroy();
    vi.clearAllTimers();
  });

  it('should logout when time expired and app was in the background', () => {
    vi.useFakeTimers();

    component.automaticLogoutTimeInMinutes = 2;
    expect(component).toBeTruthy();

    fixture.detectChanges();
    mockSubject.next(undefined);
    expect(component.logoutComing).toBeFalsy();

    fixture.detectChanges();
    vi.advanceTimersByTime(WARNING_THRESHOLD);

    mockSubject.next(120001);

    vi.advanceTimersByTime(WARNING_THRESHOLD);
    component.ngOnDestroy();

    expect(component.logoutComing).toBeTruthy();
    expect(dialogService.openNewDialog).toHaveBeenCalledTimes(0);

    vi.clearAllTimers();
  });
});