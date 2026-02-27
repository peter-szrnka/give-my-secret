import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../../environments/environment";
import { ApiTestingService } from "./api-testing-service";
import { CredentialApiResponse } from "../../secret/model/credential-api-response.model";

/**
 * @author Peter Szrnka
 */
describe("ApiTestingService", () => {
    let service : ApiTestingService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers : [ApiTestingService]
      });
      service = TestBed.inject(ApiTestingService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('Should return secret value', () => {
        // arrange
        const expectedUrl = environment.baseUrl + "api/secret/secret-id";
        const mockResponse : CredentialApiResponse = { value: "test-value" };

        //act
        service.getSecretValue("secret-id", "api-key").subscribe((res) => {
            expect(res).toBe(mockResponse);
        });

        // assert
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('GET');
        req.flush(mockResponse);
        httpMock.verify();
    });

    it('Should return complex value', () => {
      // arrange
      const expectedUrl = environment.baseUrl + "api/secret/secret-id";
      const mockResponse : { [key:string] : string } = { user : "john.doe", credential : "test-value" };

      //act
      service.getSecretValue("secret-id", "api-key").subscribe((res) => {
          expect(res).toBe(mockResponse);
      });

      // assert
      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
      httpMock.verify();
  });
});