import { Component, Input } from "@angular/core";
import { SharedDataService } from "../../common/service/shared-data-service";

export interface NavMenuItem {
    text: string;
    icon: string;
    link: string;
    admin: boolean;
}

export const NAV_MENU_ITEMS: NavMenuItem[] = [
    { text: "Users", icon: "face", link: "user/list", admin: true },
    { text: "System properties", icon: "settings", link: "system_property/list", admin: true },
    { text: "IP restrictions", icon: "http", link: "ip_restriction/list", admin: true },
    { text: "Jobs", icon: "watch_later", link: "job/list", admin: true },
    { text: "Announcements", icon: "announcement", link: "announcement/list", admin: true },
    { text: "Events", icon: "event", link: "event/list", admin: true },
    { text: "Secrets", icon: "key", link: "/secret/list", admin: false },
    { text: "API keys", icon: "security", link: "/apikey/list", admin: false },
    { text: "Keystores", icon: "widgets", link: "/keystore/list", admin: false },
    { text: "API Testing", icon: "speaker_phone", link: "api-testing", admin: false }
];

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

    navMenuItems: NavMenuItem[] = NAV_MENU_ITEMS;

    constructor(private readonly sharedDataService : SharedDataService) {
    }

    handleClick() : void {
        this.sharedDataService.showLargeMenuEvent.emit(false);
    }

    toggleTextMenuVisibility() : void {
        this.showTexts = !this.showTexts;
        localStorage.setItem('showTextsInSidevNav', '' + this.showTexts);
      }
}