import { TestBed } from "@angular/core/testing";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { of } from "rxjs";
import { ConfirmDeleteDialog } from "../components/confirm-delete/confirm-delete-dialog.component";
import { DialogService } from "./dialog-service";
import { InfoDialog } from "../components/info-dialog/info-dialog.component";

/**
 * @author Peter Szrnka
 */
describe('DialogService', () => {
    let dialog: any;
    let service: DialogService;

    beforeEach(() => {
        dialog = {
            open: jest.fn().mockReturnValue({ afterClosed: () => of(true) })
        };

        TestBed.configureTestingModule({
            imports: [MatDialogModule],
            providers: [
                DialogService,
                { provide: MatDialog, useValue: dialog }
            ]
        });
        service = TestBed.inject(DialogService);
    });

    it('Should open new info dialog', () => {
        // act
        const result = service.openNewDialog({
            text: "dialog.delete.user",
            type: "warning"
        });

        // assert
        expect(result).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: {
            text: "dialog.delete.user",
            type: "warning"
        }});
    });
 
    it('Should open confirm delete dialog', () => {
        // act
        const result = service.openConfirmDeleteDialog({ result: true, key: "dialog.delete.user" });

        // assert
        expect(result).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(ConfirmDeleteDialog, { data: {
            result: true,
            key: "dialog.delete.user"
        } });
    });
});