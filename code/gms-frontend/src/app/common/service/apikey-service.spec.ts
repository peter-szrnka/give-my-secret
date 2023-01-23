import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../environments/environment";
import { ApiKey } from "../model/apikey.model";
import { IdNamePair } from "../model/id-name-pair.model";
import { IdNamePairList } from "../model/id-name-pair-list.model";
import { IEntitySaveResponseDto } from "../model/entity-save-response.model";
import { Paging } from "../model/paging.model";
import { ApiKeyService } from "./apikey-service";

const TEST_API_KEY : ApiKey = {
  id : 1,
  userId : 1,
  name : "apiKey1",
  description : "",
  status : "ACTIVE"
};

/**
 * @author Peter Szrnka
 */
describe("ApiKeyService", () => {
    let service : ApiKeyService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [ApiKeyService]
      });
      service = TestBed.inject(ApiKeyService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should save entity', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "secure/apikey";
        const mockResponse : IEntitySaveResponseDto = {};

        //act
        service.save(TEST_API_KEY).subscribe((res) => expect(res).toBe(mockResponse));

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(TEST_API_KEY);
        req.flush(mockResponse);
        httpMock.verify();
    });

    it('Should delete entity', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/apikey/1";

      // act
      service.delete(1).subscribe((res) => expect(res).toBeCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('DELETE');
      httpMock.verify();
    });

    it('Should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/apikey/list";
      const mockResponse : ApiKey[] = [TEST_API_KEY];

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

    it('Should return by id', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/apikey/1";

      // act
      service.getById(1).subscribe((res) => expect(res).toBe(TEST_API_KEY));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(TEST_API_KEY);
      httpMock.verify();
    });

    it('Should return count', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/apikey/count";

      // act
      service.count().subscribe((res) => expect(res).toBe(2));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(2);
      httpMock.verify();
    });

    it('Should return all api key names', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/apikey/list_names";
      const mockList : IdNamePair[] = [ {id:1 , name : "id1"}, {id:2 , name : "id2"} ];
      const mockHttpResponse : IdNamePairList = {
          resultList : [ {id:1 , name : "id1"}, {id:2 , name : "id2"} ]
      };

      // act
      service.getAllApiKeyNames().subscribe(res => expect(res).toBe(mockList));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockHttpResponse);
      httpMock.verify();
  });
});