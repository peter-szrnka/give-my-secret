import { inject } from "@angular/core";
import { ActivatedRouteSnapshot } from "@angular/router";
import { User } from "../../components/user/model/user.model";
import { SharedDataService } from "../service/shared-data-service";

export const checker = (arr: string[], target: string) => {
    return arr.length === 0 ? true : arr.filter(arrElement => arrElement === target).length === 1;
};

/**
 * @author Peter Szrnka
 * 
 */
export const ROLE_GUARD = async (route: ActivatedRouteSnapshot): Promise<boolean> => {
    const service: SharedDataService = inject(SharedDataService);
    const currentUser: User | undefined = await service.getUserInfo();

    if (!currentUser) {
        return Promise.resolve(false);
    }

    const roles = route?.data?.["roles"] as string[] ?? [];
    return Promise.resolve(checker(roles, currentUser.role));
};