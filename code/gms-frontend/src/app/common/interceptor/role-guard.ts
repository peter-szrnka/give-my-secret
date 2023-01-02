import { Injectable } from "@angular/core";
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { User } from "../model/user.model";
import { SharedDataService } from "../service/shared-data-service";

const checker = (arr : string[], target : string[]) => target.every(v => arr.includes(v));

@Injectable({
    providedIn: "root"
})
export class RoleGuard implements CanActivate {

    constructor(public router: Router, private sharedData : SharedDataService) {
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
        const roles = route.data["roles"] as string[];
        const currentUser : User | undefined = this.sharedData.getUserInfo();

        if (currentUser === undefined) {
            return false;
        }

        const checkResult = checker(roles, currentUser.roles);

        if (checkResult === false) {
            this.router.navigate(['']);
            return false;
        }

        return true;
    }
}