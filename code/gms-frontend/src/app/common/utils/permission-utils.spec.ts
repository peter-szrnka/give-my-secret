import { User } from "../model/user.model";
import { checkRights } from "./permission-utils";

/**
 * @author Peter Szrnka
 */
describe("Permission utils", () => {

    it('User is undefined', () => {
        expect(checkRights(undefined)).toBeTruthy();
    });

    it('User role is undefined', () => {
        const user : User = {
            roles: []
        };
        expect(checkRights(user)).toBeFalsy();
    });

    it('Admin rights are undefined', () => {
        const user : User = { roles : ["ROLE_USER"] };
        expect(checkRights(user)).toBeFalsy();
    });

    it('User rights', () => {
        const user : User = { roles : ["ROLE_USER"] };
        expect(checkRights(user, true)).toBeTruthy();
    });

    it('Admin rights', () => {
        const user : User = { roles : ["ROLE_ADMIN"] };
        expect(checkRights(user, false)).toBeTruthy();
    });
});