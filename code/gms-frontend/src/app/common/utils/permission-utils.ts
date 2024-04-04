import { User } from "../../components/user/model/user.model";

/**
 * @author Peter Szrnka
 */
export function checkRights(user?: User, requireAdminRights?: boolean): boolean {
    if (!user) {
        return true;
    }

    if (requireAdminRights === undefined) {
        return false;
    }

    if (requireAdminRights) {
        return user.role !== 'ROLE_ADMIN';
    }

    return user.role === 'ROLE_ADMIN';
}

export function isSpecificUser(roles: string[], requiredRole: string): boolean {
    return roles.filter(role => role === requiredRole).length > 0;
}

export function roleCheck(currentUser: User, roleName: string): boolean {
    return currentUser.role === roleName;
}