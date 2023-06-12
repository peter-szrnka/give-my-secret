import { Directive, OnInit } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { BaseList } from "../../../model/base-list";
import { PageConfig } from "../../../model/common.model";
import { SharedDataService } from "../../../service/shared-data-service";
import { getErrorMessage } from "../../../utils/error-utils";
import { InfoDialog } from "../../info-dialog/info-dialog.component";
import { SaveServiceBase } from "../service/save-service-base";

/**
 * @author Peter Szrnka
 */
@Directive()
export abstract class BaseSaveableDetailComponent<T, S extends SaveServiceBase<T, BaseList<T>>> implements OnInit {

    data : T;
    loading  = true;

    constructor(
        protected router : Router,
        protected sharedData : SharedDataService, 
        protected service : S,
        public dialog: MatDialog,
        protected activatedRoute: ActivatedRoute) {}

    abstract getPageConfig() : PageConfig;

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    protected dataLoadingCallback(data : T) : void {
        // Empty implementation
    }

    ngOnInit(): void {
        this.fetchData();
    }

    save() {
        this.loading = true;
        this.service.save(this.data)
        .subscribe({
            next: () => {
                this.openInformationDialog(this.getPageConfig().label + " has been saved!", true, 'information');
            },
            error: (err) => {
                this.openInformationDialog("Error: " + getErrorMessage(err), false, 'warning');
            },
            complete :() => {
                this.loading = false;
            }
        });
    }

    private fetchData() {
        this.loading = true;
        this.activatedRoute.data.subscribe((response : any) => {
            this.data = response['entity'];
            this.loading = false;
            this.dataLoadingCallback(this.data);
        });
    }

    public openInformationDialog(message : string, navigateToList : boolean, type : string) {
        const dialogRef : MatDialogRef<InfoDialog, any> = this.dialog.open(InfoDialog, {
          data: { text: message, type : type },
        });
    
        dialogRef.afterClosed().subscribe(() => {
          if (navigateToList === false) {
            return;
          }

          void this.router.navigate(['/' + this.getPageConfig().scope + '/list']);
        });
    }
}