import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { User } from "../model/user.model";
import { UserService } from "./user-service";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { Paging } from "../../../common/model/paging.model";

const TEST_USER : User = {
  id: 1,
  roles: []
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
      const expectedUrl = environment.baseUrl + "secure/user/list";
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
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(request);
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

    it('should return count', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/user/count";

      // act
      service.count().subscribe((res) => expect(res).toBe(2));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(2);
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
});