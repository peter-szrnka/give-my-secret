import { User } from "../model/user.model";

export function checkRights(user? : User, requireAdminRights? : boolean) : boolean {
    if (user === undefined || user.roles === undefined) {
        return true;
    }

    if (requireAdminRights === undefined) {
        return false;
    }

    if (requireAdminRights) {
        return user.roles.filter((role)=> role === 'ROLE_ADMIN').length === 0;
    }

    return user.roles.filter((role)=> role === 'ROLE_ADMIN').length > 0;
}

export function isSpecificUser(roles : string[], requiredRole : string) : boolean {
    return roles !== undefined && roles.filter(role => role === requiredRole).length > 0;
}