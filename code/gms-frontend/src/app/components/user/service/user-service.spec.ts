import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { Paging } from "../../../common/model/paging.model";
import { User } from "../model/user.model";
import { UserService } from "./user-service";

const TEST_USER : User = {
  id: 1,
  role: 'ROLE_USER'
};

/**
 * @author Peter Szrnka
 */
describe("SecretService", () => {
    let service : UserService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [UserService]
      });
      service = TestBed.inject(UserService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should save entity', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "secure/user";
        const mockResponse : IEntitySaveResponseDto = {};

        //act
        service.save(TEST_USER).subscribe((res) => expect(res).toBe(mockResponse));

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(TEST_USER);
        req.flush(mockResponse);
        httpMock.verify();
    });

    it('should delete entity', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/user/1";

      // act
      service.delete(1).subscribe((res) => expect(res).toBeCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('DELETE');
      httpMock.verify();
    });

    it('should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/user/list?direction=asc&property=id&page=0&size=10";
      const mockResponse : User[] = [TEST_USER];

      // act
      const request : Paging = {
        direction : "asc",
        page: 0,
        property: "id",
        size: 10
      };
      service.list(request).subscribe((res) => expect(res).toBe(mockResponse));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(request);
      httpMock.verify();
    });

    it('should return by id', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/user/1";

      // act
      service.getById(1).subscribe((res) => expect(res).toBe(TEST_USER));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(TEST_USER);
      httpMock.verify();
    });

    it('should change credentials', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/user/change_credential";

      // act
      const request : any = {
        "oldCredential" : "asdf1234!",
        "newCredential" : "Good2345!"
      };
      service.changeCredentials(request).subscribe(() => {
        // Nothing to do here
      });

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toBe(request);
      httpMock.verify();
    });

    it('should enable entity', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/user/1?enabled=true";

      // act
      service.toggle(1, true).subscribe((res) => expect(res).toHaveBeenCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

    it.each([ true, false ])('should toggle MFA', (input: boolean) => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/user/toggle_mfa?enabled=" + input;

      // act
      service.toggleMfa(input).subscribe((res) => expect(res).toHaveBeenCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

    it('should toggle MFA', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/user/mfa_active";

      // act
      service.isMfaActive().subscribe((res) => expect(res).toHaveBeenCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      httpMock.verify();
    });

    it('should sync LDAP users', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/user/sync_ldap_users";

      // act
      service.manualLdapUserSync().subscribe((res) => expect(res).toHaveBeenCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      httpMock.verify();
    });
});