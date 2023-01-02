import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { User } from '../../common/model/user.model';
import { SharedDataService } from '../../common/service/shared-data-service';
import { MessageService } from '../../common/service/message-service';
import { environment } from '../../../environments/environment';
import { filter } from 'rxjs';

@Component({
    selector : 'header',
    templateUrl : './header.component.html',
    styleUrls : ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
    currentUser : User | undefined;
    unreadMessageCount  = 0;

    isProd : boolean = environment.production;

    constructor(
        public router: Router, 
        public sharedDataService: SharedDataService, 
        private messageService : MessageService) {
    }

    ngOnInit(): void {
        this.sharedDataService.messageCountUpdateEvent.subscribe(() => this.getAllUnread());
        this.sharedDataService.userSubject$.subscribe(user => this.currentUser = user);
        this.router.events.pipe(filter(event => (event instanceof NavigationEnd))).subscribe(() => this.getAllUnread());
    }
    
    logout() : void {
        this.currentUser = undefined;
        this.sharedDataService.logout();
    }

    private getAllUnread() : void {
        this.messageService.getAllUnread().subscribe(value => this.unreadMessageCount = value);
    }
}
