import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { User } from '../user/model/user.model';
import { SharedDataService } from '../../common/service/shared-data-service';
import { MessageService } from '../messages/service/message-service';
import { environment } from '../../../environments/environment';
import { filter } from 'rxjs';

/**
 * @author Peter Szrnka
 */
@Component({
    selector : 'header',
    templateUrl : './header.component.html',
    styleUrls : ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
    currentUser : User | undefined;
    unreadMessageCount  = 0;

    isProd : boolean = environment.production;
    showLargeMenu : boolean = false;

    constructor(
        public router: Router, 
        public sharedDataService: SharedDataService, 
        private messageService : MessageService) {
    }

    ngOnInit(): void {
        this.sharedDataService.messageCountUpdateEvent.subscribe(() => this.getAllUnread());
        this.sharedDataService.userSubject$.subscribe(user => this.currentUser = user);
        this.router.events.pipe(filter(event => (event instanceof NavigationEnd))).subscribe((event) => {
            if (this.currentUser === undefined || (event as NavigationEnd).url !== "/") {
                return;
            }

            this.getAllUnread();
        });
    }
    
    logout() : void {
        this.currentUser = undefined;
        this.sharedDataService.logout();
    }

    toggleMenu() : void {
        this.showLargeMenu = !this.showLargeMenu;
    }

    private getAllUnread() : void {
        this.messageService.getAllUnread().subscribe(value => this.unreadMessageCount = value);
    }
}
