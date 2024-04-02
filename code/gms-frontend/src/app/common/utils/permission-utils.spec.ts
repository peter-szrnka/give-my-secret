import { User } from "../../components/user/model/user.model";
import { checkRights, isSpecificUser } from "./permission-utils";

/**
 * @author Peter Szrnka
 */
describe("Permission utils", () => {

    it('Is specific user without available roles', () => {
        expect(isSpecificUser([], 'ROLE_ADMIN')).toBeFalsy();
    });

    it('Is specific user without available roles', () => {
        expect(isSpecificUser(['ROLE_ADMIN'], 'ROLE_ADMIN')).toBeTruthy();
    });

    it('User is undefined', () => {
        expect(checkRights(undefined)).toBeTruthy();
    });

    it('User rights', () => {
        const user : User = { role : "ROLE_USER" };
        expect(checkRights(user, true)).toBeTruthy();
    });

    it('Admin rights', () => {
        const user : User = { role : "ROLE_ADMIN" };
        expect(checkRights(user, false)).toBeTruthy();
    });
});