import { User } from "../model/user.model";
import { checkRights } from "./permission-utils";

describe("Permission utils", () => {

    it('User is undefined', () => {
        expect(checkRights(undefined, undefined)).toBeTruthy();
    });

    it('User role is undefined', () => {
        const user : User = {
            roles: []
        };
        expect(checkRights(user, undefined)).toBeFalsy();
    });

    it('Admin rights are undefined', () => {
        const user : User = { roles : ["ROLE_USER"] };
        expect(checkRights(user, undefined)).toBeFalsy();
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