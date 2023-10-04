import { HttpTestingController, HttpClientTestingModule } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { environment } from "../../../environments/environment";
import { InformationService } from "./info-service";
import { User } from "../../components/user/model/user.model";

/**
 * @author Peter Szrnka
 */
describe('InformationService', () => {
    let service : InformationService;
    let httpMock : HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
          imports: [HttpClientTestingModule],
          providers : [InformationService]
        });
        service = TestBed.inject(InformationService);
        httpMock = TestBed.inject(HttpTestingController);
      });

    it('Should provide null userinfo', async () => {
        // arrange
        const expectedUrl = environment.baseUrl + "info/me";
        const mockResponse: User  = {
            id: 1,
            username: 'test',
            roles: ["ROLE_USER"]
        };

        //act
        const response: User | null = await service.getUserInfo();

        // assert
        expect(service).toBeTruthy();
        expect(response).toEqual(null);
        const req = httpMock.expectOne(expectedUrl);
        expect(req.request.method).toBe('GET');
        req.flush(mockResponse);
        httpMock.verify();
    });
});