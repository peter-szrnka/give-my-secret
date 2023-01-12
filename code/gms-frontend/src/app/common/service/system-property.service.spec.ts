import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { SystemPropertyService } from "./system-property.service";
import { SystemProperty } from "../model/system-property.model";
import { environment } from "../../../environments/environment";
import { IEntitySaveResponseDto } from "../model/entity-save-response.model";
import { Paging } from "../model/paging.model";

const TEST_SYSTEM_PROPERTY : SystemProperty = {
    key : '',
    value : '',
    factoryValue : true
};

describe("SytemPropertyService", () => {
    let service : SystemPropertyService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [SystemPropertyService]
      });
      service = TestBed.inject(SystemPropertyService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should save entity', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "secure/system_property";
        const mockResponse : IEntitySaveResponseDto = {};

        //act
        service.save(TEST_SYSTEM_PROPERTY).subscribe((res) => expect(res).toBe(mockResponse));

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(TEST_SYSTEM_PROPERTY);
        req.flush(mockResponse);
        httpMock.verify();
    });

    it('Should delete entity', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/system_property/REFRESH_JWT_ALGORITHM";

      // act
      service.delete('REFRESH_JWT_ALGORITHM').subscribe((res) => expect(res).toBeCalled());

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('DELETE');
      httpMock.verify();
    });

    it('Should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "secure/system_property/list";
      const mockResponse : SystemProperty[] = [TEST_SYSTEM_PROPERTY];

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
});