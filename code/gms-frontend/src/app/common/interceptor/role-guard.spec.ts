import { of } from "rxjs";
import { RoleGuard } from "./role-guard";

/**
 * @author Peter Szrnka
 */
describe('RoleGuard', () => {
    let roleGuard : RoleGuard;
    let router : any;
    let sharedData : any;

    const userData = {
        userId : "test",
        userName : "test-user",
        exp : 1,
        roles : ["ROLE_USER"]
    };

    beforeEach(() => {
        router = {
            navigate : jest.fn().mockReturnValue(of(true))
        };
    });

    it('should return true', () => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(userData)
        };

        roleGuard = new RoleGuard(router, sharedData);
 
        // act
        const response = roleGuard.canActivate({ data : { roles: ["ROLE_USER", "ROLE_VIEWER"] } } as any, {} as any);

        // assert
        expect(response).toBeTruthy();
        expect(router.navigate).toHaveBeenCalledTimes(0);

    });

    it('should return false', () => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(userData)
        };
        roleGuard = new RoleGuard(router, sharedData);

        // act
        const response = roleGuard.canActivate({ data : { roles: ["ROLE_ADMIN"] } } as any, {} as any);

        // assert
        expect(response).toBeFalsy();
        expect(router.navigate).toHaveBeenCalledTimes(1);
        expect(router.navigate).toHaveBeenCalledWith([""]);
    });

    it('should deny empty user', () => {
        // arrange
        sharedData = {
            getUserInfo : jest.fn().mockReturnValue(undefined)
        };

        roleGuard = new RoleGuard(router, sharedData);
 
        // act
        const response = roleGuard.canActivate({ data : { roles: ["ROLE_USER", "ROLE_VIEWER"] } } as any, {} as any);

        // assert
        expect(response).toBeFalsy();

    });
});