import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../environments/environment";
import { AuthenticationPhase, Login, LoginResponse, VerifyLogin } from "../model/login.model";
import { AuthService } from "./auth-service";

const TEST_LOGIN : Login = {};
const VERIFY_LOGIN: VerifyLogin = {};

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
        const mockResponse: LoginResponse  = {
          currentUser: {
            role: "ROLE_USER"
          },
          phase: AuthenticationPhase.COMPLETED
        };

        //act
        service.login(TEST_LOGIN).subscribe(res => expect(res).toBe(mockResponse));

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(TEST_LOGIN);
        req.flush(mockResponse);
        httpMock.verify();
    });

    it('Should verify login', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "verify";
      const mockResponse: LoginResponse  = {
        currentUser: {
          role: "ROLE_USER"
        },
        phase: AuthenticationPhase.COMPLETED
      };

      //act
      service.verifyLogin(VERIFY_LOGIN).subscribe(res => expect(res).toBe(mockResponse));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(VERIFY_LOGIN);
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