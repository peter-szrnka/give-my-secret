import { Component } from "@angular/core";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'nav-menu',
    templateUrl: './nav-menu.html',
    styleUrls: ['./nav-menu.css']
})
export class NavMenuComponent {

    showTexts: boolean = true;
    admin: boolean = false;

    isAdmin(): boolean {
        return this.admin;
    }
}