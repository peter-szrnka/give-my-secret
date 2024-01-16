import { TestBed } from "@angular/core/testing";
import { SharedDataService } from "../service/shared-data-service";
import { ROLE_GUARD } from "./role-guard";
import { HttpClientTestingModule } from "@angular/common/http/testing";

/**
 * @author Peter Szrnka
 */
describe('RoleGuard', () => {
    let sharedData : any;

    const userData = {
        userId : "test",
        userName : "test-user",
        exp : 1,
        roles : ["ROLE_USER"]
    };

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
              {
                provide: SharedDataService,
                useValue: sharedData,
              }
            ],
          });
    };

    it.each([
        undefined,
        { data : undefined },
        { data : { roles: undefined } }
    ])('should return false when no data provided', async (routeData: any) => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(userData)
        };

        configureTestBed();
 
        // act
        const response = await TestBed.runInInjectionContext(async () => ROLE_GUARD(routeData));

        // assert
        expect(response).toBeFalsy();
    });

    it('should return true', async () => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(userData)
        };

        configureTestBed();
 
        // act
        const response = await TestBed.runInInjectionContext(async () => ROLE_GUARD({ data : { roles: ["ROLE_USER", "ROLE_VIEWER"] } } as any));

        // assert
        expect(response).toBeTruthy();
    });

    it('should return false', async() => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(userData)
        };
        configureTestBed();

        // act
        const response = await TestBed.runInInjectionContext(async () => ROLE_GUARD({ data : { roles: ["ROLE_ADMIN"] } } as any));

        // assert
        expect(response).toBeFalsy();
    });

    it('should deny empty user', async () => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(undefined)
        };
        configureTestBed();
 
        // act
        const response = await TestBed.runInInjectionContext(async () => ROLE_GUARD({ data : { roles: ["ROLE_USER", "ROLE_VIEWER"] } } as any));

        // assert
        expect(response).toBeFalsy();
    });
});