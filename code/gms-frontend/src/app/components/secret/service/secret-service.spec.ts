import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { Paging } from "../../../common/model/paging.model";
import { Secret } from "../model/secret.model";
import { SecretService } from "./secret-service";

const TEST_SECRET : Secret = {
  id : 1,
  value : "myValue",
  keystoreId: 1,
  returnDecrypted: false,
  rotationPeriod : "HOURLY",
  status : "ACTIVE",
  apiKeyRestrictions : [],
  type : 'CREDENTIAL'
};

/**
 * @author Peter Szrnka
 */
describe("SecretService", () => {
    let service : SecretService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [SecretService]
      });
      service = TestBed.inject(SecretService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should not save entity', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "secure/secret";
        const mockResponse : IEntitySaveResponseDto = {
          entityId : 1,
          success : false
        };

        //act
        service.save(TEST_SECRET).subscribe((res) => {
          expect(res).toBe(mockResponse);
          expect(res.success).toBeFalsy();
          expect(res.entityId).toEqual(1);
        });

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(TEST_SECRET);
        req.flush(mockResponse);
        httpMock.verify();
    });

    it('should delete entity', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/secret/1";

      // act
      service.delete(1).subscribe((res) => expect(res).toBeCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('DELETE');
      httpMock.verify();
    });

    it('should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/secret/list";
      const mockResponse : Secret[] = [TEST_SECRET];

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
      const expectedUrl = environment.baseUrl + "secure/secret/1";

      // act
      service.getById(1).subscribe((res) => expect(res).toBe(TEST_SECRET));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(TEST_SECRET);
      httpMock.verify();
    });

    it('should return count', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/secret/count";

      // act
      service.count().subscribe((res) => expect(res).toBe(2));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(2);
      httpMock.verify();
    });

    it('should return with the value', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/secret/value/1";
      const mockResponse = "__decoded_value__";

      // act
      service.getValue(1).subscribe((res) => expect(res).toBe(mockResponse));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
      httpMock.verify();
    });

    it('should rotate value', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/secret/rotate/1";
      const mockResponse = "OK";

      // act
      service.rotate(1).subscribe((res) => expect(res).toBe(mockResponse));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      req.flush(mockResponse);
      httpMock.verify();
    });
});