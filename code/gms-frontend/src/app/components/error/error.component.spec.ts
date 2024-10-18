// Implement a unit test for error.component.ts

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { InformationService } from '../../common/service/info-service';
import { SharedDataService } from '../../common/service/shared-data-service';
import { ErrorComponent } from './error.component';

describe('ErrorComponent', () => {
  let component: ErrorComponent;
  let fixture: ComponentFixture<ErrorComponent>;
  let router: any;
  let sharedDataService: any;
  let informationService: any;

  const configureTestBed = () => {
    TestBed.configureTestingModule({
        imports: [ErrorComponent],
        providers: [
          { provide: Router, useValue: router },
          { provide: SharedDataService, useValue: sharedDataService },
          { provide: InformationService, useValue: informationService }
        ]
      });
      jest.useFakeTimers();
  
      fixture = TestBed.createComponent(ErrorComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
  };

  beforeEach(() => {
    router = {
        navigate: jest.fn().mockImplementation(() => {})
    };
    sharedDataService = {
        check: jest.fn().mockImplementation(() => {})
    };
    informationService = {
        healthCheck: jest.fn().mockResolvedValue("OK")
    };
  });

  it('should create', () => {
    configureTestBed();
    expect(component).toBeTruthy();
  });

  it('should handle successful healthcheck', () => {
    configureTestBed();

    jest.advanceTimersByTime(15000);
    expect(component).toBeTruthy();
    expect(informationService.healthCheck).toHaveBeenCalled();
  });

  it('should handle failed healthcheck', () => {
    informationService.healthCheck = jest.fn().mockRejectedValue("Error");
    configureTestBed();

    jest.advanceTimersByTime(15000);
    expect(component).toBeTruthy();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});