import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { Subject } from "rxjs";
import { SplashScreenStateService } from "./splash-screen-service";

/**
 * @author Peter Szrnka
 */
describe('SplashScreenStateService', () => {

    let service : SplashScreenStateService;
    let mockSplashScreenSubject : Subject<boolean>;
    
    beforeEach(() => {
        TestBed.configureTestingModule({
          imports: [HttpClientTestingModule],
          providers : [SplashScreenStateService]
        });
        service = TestBed.inject(SplashScreenStateService);
        mockSplashScreenSubject = new Subject<boolean>();
      });
  
    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should start splash screen display', () => {
      // assert & act
      mockSplashScreenSubject.subscribe(value => expect(value).toBe(true));
      service.start();
  });

    it('should stop splash screen display', () => {
        // assert & act
        mockSplashScreenSubject.subscribe(value => expect(value).toBe(false));
        service.stop();
    });
});