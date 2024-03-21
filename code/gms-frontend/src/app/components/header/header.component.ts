import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Subscription, filter } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SharedDataService } from '../../common/service/shared-data-service';
import { checkRights } from '../../common/utils/permission-utils';
import { MessageService } from '../messages/service/message-service';
import { User } from '../user/model/user.model';

/**
 * @author Peter Szrnka
 */
@Component({
    selector : 'header',
    templateUrl : './header.component.html',
    styleUrls : ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {
    currentUser : User | undefined;
    unreadMessageCount  = 0;

    isProd : boolean = environment.production;
    showLargeMenu : boolean = false;
    unreadSubscription: Subscription;
    userSubscription: Subscription;

    constructor(
        public router: Router, 
        public sharedDataService: SharedDataService, 
        private messageService : MessageService) {
    }

    ngOnDestroy(): void {
        this.unreadSubscription.unsubscribe();
        this.userSubscription.unsubscribe();
    }

    ngOnInit(): void {
        this.sharedDataService.showLargeMenuEvent.subscribe((result : boolean) => this.showLargeMenu = result);
        this.sharedDataService.messageCountUpdateEvent.subscribe(() => this.getAllUnread());
        this.userSubscription = this.sharedDataService.userSubject$.subscribe(user => this.currentUser = user);
        this.router.events.pipe(filter(event => (event instanceof NavigationEnd))).subscribe((event) => {
            if (this.currentUser === undefined || (event as NavigationEnd).url !== "/") {
                return;
            }

            this.getAllUnread();
        });

        this.getAllUnread();
    }
    
    logout() : void {
        this.currentUser = undefined;
        this.sharedDataService.logout();
    }

    toggleMenu() : void {
        this.showLargeMenu = !this.showLargeMenu;
    }

    isAdmin() : boolean {
        return checkRights(this.currentUser, false);
    }

    private getAllUnread() : void {
        this.unreadSubscription = this.messageService.getAllUnread().subscribe(value => this.unreadMessageCount = value);
    }
}
