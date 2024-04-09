import { Component, OnDestroy, OnInit } from "@angular/core";
import { AngularMaterialModule } from "../../angular-material-module";
import { SystemStatusDto } from "../../common/model/system-status.model";
import { SetupService } from "../setup/service/setup-service";
import { Subscription } from "rxjs";

/**
 * @author Peter Szrnka
 */
@Component({
    standalone: true,
    imports: [ AngularMaterialModule ],
    selector: 'about',
    templateUrl: './about.component.html',
    styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit, OnDestroy {

    systemStatus: SystemStatusDto;
    subscription: Subscription;

    constructor(private setupService: SetupService) {}

    ngOnInit(): void {
        this.subscription = this.setupService.checkReady().subscribe(systemStatus => this.systemStatus = systemStatus);
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}