import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, Router } from "@angular/router";
import { User } from "../../components/user/model/user.model";
import { SharedDataService } from "../service/shared-data-service";

const checker = (arr: string[], target: string[]) => target.every(v => arr.includes(v));

/**
 * @author Peter Szrnka
 */
export const ROLE_GUARD = async (route: ActivatedRouteSnapshot): Promise<boolean> => {
    const roles = route.data["roles"] as string[];
    const service: SharedDataService = inject(SharedDataService);
    const currentUser: User | undefined = await service.getUserInfo();

    if (!currentUser) {
        return false;
    }

    const checkResult = checker(roles, currentUser.roles);

    if (checkResult === false) {
        void inject(Router).navigate(['']);
        return false;
    }

    return true;
};