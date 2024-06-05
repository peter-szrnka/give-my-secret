import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../../environments/environment";
import { ErrorCodeList } from "../model/error-code-list.model";
import { ErrorCode } from "../model/error-code.model";
import { ErrorCodeService } from "./error-code.service";

const TEST_CODE : ErrorCode = {
  code: 'GMS-001',
  description: 'Test'
};

/**
 * @author Peter Szrnka
 */
describe("ErrorCodeService", () => {
    let service : ErrorCodeService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [ErrorCodeService]
      });
      service = TestBed.inject(ErrorCodeService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('Should list results', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "error_codes";
      const mockResponse : ErrorCodeList = { errorCodeList: [TEST_CODE] };

      // act
      service.list().subscribe((res) => expect(res).toBe(mockResponse));

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush({});
      httpMock.verify();
    });
});