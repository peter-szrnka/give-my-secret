import { Component, Input } from "@angular/core";
import { SharedDataService } from "../../common/service/shared-data-service";

/**
 * @author Peter Szrnka
 */
@Component({
    selector: 'nav-menu',
    templateUrl: './nav-menu.html',
    styleUrls: ['./nav-menu.css']
})
export class NavMenuComponent {

    @Input() showTexts: boolean = true;
    @Input() enableBottomToggle : boolean = false;
    @Input() admin: boolean = false;

    constructor(private sharedDataService : SharedDataService) {
    }

    isAdmin(): boolean {
        return this.admin;
    }

    handleClick() : void {
        this.sharedDataService.showLargeMenuEvent.emit(false);
    }

    toggleTextMenuVisibility() : void {
        this.showTexts = !this.showTexts;
        localStorage.setItem('showTextsInSidevNav', '' + this.showTexts);
      }
}