import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../../environments/environment";
import { IEntitySaveResponseDto } from "../../../common/model/entity-save-response.model";
import { Paging } from "../../../common/model/paging.model";
import { IpRestriction } from "../model/ip-restriction.model";
import { IpRestrictionService } from "./ip-restriction.service";

const TEST_IP_RESTRICTION : IpRestriction = {
  id: 1,
  status: "ACTIVE",
  ipPattern: "",
  allow: false
};

/**
 * @author Peter Szrnka
 */
describe("IpRestrictionService", () => {
    let service : IpRestrictionService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [IpRestrictionService]
      });
      service = TestBed.inject(IpRestrictionService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should save entity', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "secure/ip_restriction";
        const mockResponse : IEntitySaveResponseDto = {};

        //act
        service.save(TEST_IP_RESTRICTION).subscribe((res) => expect(res).toBe(mockResponse));

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(TEST_IP_RESTRICTION);
        req.flush(mockResponse);
        httpMock.verify();
    });

    it('Should delete entity', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/ip_restriction/1";

      // act
      service.delete(1).subscribe((res) => expect(res).toBeCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('DELETE');
      httpMock.verify();
    });

    it('Should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/ip_restriction/list?direction=asc&property=id&page=0&size=10";
      const mockResponse : IpRestriction[] = [TEST_IP_RESTRICTION];

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

    it('Should return by id', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/ip_restriction/1";

      // act
      service.getById(1).subscribe((res) => expect(res).toBe(TEST_IP_RESTRICTION));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(TEST_IP_RESTRICTION);
      httpMock.verify();
    });

  it('should enable entity', () => {
    // arrange
    const expectedUrl = environment.baseUrl + "secure/ip_restriction/1?enabled=true";

    // act
    service.toggle(1, true).subscribe((res) => expect(res).toHaveBeenCalled());

    // assert
    const req = httpMock.expectOne(expectedUrl);
    expect(req.request.method).toBe('POST');
    httpMock.verify();
  });
});