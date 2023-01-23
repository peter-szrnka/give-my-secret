import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../environments/environment";
import { Login } from "../model/login.model";
import { AuthService } from "./auth-service";

const TEST_LOGIN : Login = {};

/**
 * @author Peter Szrnka
 */
describe('AuthService', () => {
    let service : AuthService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
          imports: [HttpClientTestingModule],
          providers : [AuthService]
        });
        service = TestBed.inject(AuthService);
        httpMock = TestBed.inject(HttpTestingController);
      });
  
    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('Should log in', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "authenticate";
        const mockResponse  = "OK";

        //act
        service.login(TEST_LOGIN).subscribe(res => expect(res).toBe(mockResponse));

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(TEST_LOGIN);
        req.flush(mockResponse);
        httpMock.verify();
    });

    it('should log out', () => {
      const expectedUrl = environment.baseUrl + "logoutUser";

      // act
      service.logout().subscribe(res => {
        expect(res).toBeDefined();
      });

      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      req.flush({});
      httpMock.verify();
    });

    it('should refresh token', () => {
      const expectedUrl = environment.baseUrl + "refresh";

      // act
      service.refreshToken().subscribe(res => {
        expect(res).toBeDefined();
      });

      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      req.flush({});
      httpMock.verify();
    });
});