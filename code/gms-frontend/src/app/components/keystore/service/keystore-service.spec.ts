import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { Keystore } from "../model/keystore.model";
import { KeystoreService } from "./keystore-service";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { IdNamePairList } from "../../../common/model/id-name-pair-list.model";
import { IdNamePair } from "../../../common/model/id-name-pair.model";
import { Paging } from "../../../common/model/paging.model";

const TEST_KEYSTORE : Keystore = {
  id: 1,
  description: "Description",
  aliases: []
};

/**
 * @author Peter Szrnka
 */
describe("KeystoreService", () => {
    let service : KeystoreService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [KeystoreService]
      });
      service = TestBed.inject(KeystoreService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should save entity', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "secure/keystore";
        const mockResponse : IEntitySaveResponseDto = {};

        //act
        const blob = new Blob(["testing"], { type: "text/plain" });
        service.save(TEST_KEYSTORE, blob).subscribe((res) => expect(res).toBe(mockResponse));

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('POST');
        req.flush(mockResponse);
        httpMock.verify();
    });

    it('Should delete entity', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/keystore/1";

      // act
      service.delete(1).subscribe((res) => expect(res).toBeCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('DELETE');
      httpMock.verify();
    });

    it('Should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/keystore/list";
      const mockResponse : Keystore[] = [TEST_KEYSTORE];

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
      const expectedUrl = environment.baseUrl + "secure/keystore/1";

      // act
      service.getById(1).subscribe((res) => expect(res).toBe(TEST_KEYSTORE));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(TEST_KEYSTORE);
      httpMock.verify();
    });

    it('Should return count', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/keystore/count";

      // act
      service.count().subscribe((res) => expect(res).toBe(2));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(2);
      httpMock.verify();
    });

    it('Should return all keystore names', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "secure/keystore/list_names";
        const mockList : IdNamePair[] = [ {id:1 , name : "id1"}, {id:2 , name : "id2"} ];
        const mockHttpResponse : IdNamePairList = {
            resultList : [ {id:1 , name : "id1"}, {id:2 , name : "id2"} ]
        };

        // act
        service.getAllKeystoreNames().subscribe(res => expect(res).toBe(mockList));

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('GET');
        req.flush(mockHttpResponse);
        httpMock.verify();
    });

    it('Should return all keystore aliases', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/keystore/list_aliases/1";
      const mockList : IdNamePair[] = [ {id:1 , name : "alias1"}, {id:2 , name : "alias2"} ];
      const mockHttpResponse : IdNamePairList = {
          resultList : [ {id:1 , name : "alias1"}, {id:2 , name : "alias2"} ]
      };

      // act
      service.getAllKeystoreAliases(1).subscribe(res => expect(res).toBe(mockList));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockHttpResponse);
      httpMock.verify();
  });
});