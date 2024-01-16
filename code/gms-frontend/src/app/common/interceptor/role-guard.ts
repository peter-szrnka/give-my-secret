import { inject } from "@angular/core";
import { ActivatedRouteSnapshot } from "@angular/router";
import { User } from "../../components/user/model/user.model";
import { SharedDataService } from "../service/shared-data-service";

export const checker = (arr: string[], target: string[]) => {
    let result = false;
    arr.forEach(arrElement => {
        target.forEach(targetElement => {
            if (arrElement === targetElement) {
                result = true;
            }
        });
    });

    return result;
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
    return Promise.resolve(checker(roles, currentUser.roles));
};