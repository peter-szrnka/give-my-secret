import { TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { of } from "rxjs";
import { SharedDataService } from "../service/shared-data-service";
import { ROLE_GUARD } from "./role-guard";
import { HttpClientTestingModule } from "@angular/common/http/testing";

/**
 * @author Peter Szrnka
 */
describe('RoleGuard', () => {
    let router : any;
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
              },
              {
                provide: Router,
                useValue: router,
              }
            ],
          });
    };

    beforeEach(() => {
        router = {
            navigate : jest.fn().mockReturnValue(of(true))
        };
    });

    it('should return true', async () => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(userData)
        };

        configureTestBed();
 
        // act
        const response = await TestBed.runInInjectionContext(async () => ROLE_GUARD({ data : { roles: ["ROLE_USER", "ROLE_VIEWER"] } } as any, router));

        // assert
        expect(response).toBeTruthy();
        expect(router.navigate).toHaveBeenCalledTimes(0);

    });

    it('should return false', async() => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(userData)
        };
        configureTestBed();

        // act
        const response = await TestBed.runInInjectionContext(async () => ROLE_GUARD({ data : { roles: ["ROLE_ADMIN"] } } as any, router));

        // assert
        expect(response).toBeFalsy();
        expect(router.navigate).toHaveBeenCalledTimes(1);
        expect(router.navigate).toHaveBeenCalledWith([""]);
    });

    it('should deny empty user', async () => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(undefined)
        };
        configureTestBed();
 
        // act
        const response = await TestBed.runInInjectionContext(async () => ROLE_GUARD({ data : { roles: ["ROLE_USER", "ROLE_VIEWER"] } } as any, router));

        // assert
        expect(response).toBeFalsy();
    });
});