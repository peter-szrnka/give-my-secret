import { User } from "../../components/user/model/user.model";

/**
 * @author Peter Szrnka
 */
export function checkRights(user?: User, requireAdminRights?: boolean): boolean {
    if (user === undefined || user.roles === undefined) {
        return true;
    }

    if (requireAdminRights === undefined) {
        return false;
    }

    if (requireAdminRights) {
        return user.roles.filter((role) => role === 'ROLE_ADMIN').length === 0;
    }

    return user.roles.filter((role) => role === 'ROLE_ADMIN').length > 0;
}

export function isSpecificUser(roles: string[], requiredRole: string): boolean {
    return roles.filter(role => role === requiredRole).length > 0;
}

export function roleCheck(currentUser: User, roleName: string): boolean {
    return currentUser.roles?.filter(role => role === roleName).length > 0;
}