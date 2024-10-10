import { TestBed } from "@angular/core/testing";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { DialogService } from "./dialog-service";
import { of } from "rxjs";
import { InfoDialog } from "../components/info-dialog/info-dialog.component";
import { ConfirmDeleteDialog } from "../components/confirm-delete/confirm-delete-dialog.component";

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

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('Should open custom dialog', () => {
        // arrange
        const text = "Text";

        // act
        const result = service.openCustomDialog(text, 'information');

        // assert
        expect(result).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { text: text, type: 'information' } });
    });

    it('Should open custom dialog with errorcode', () => {
        // arrange
        const text = "Text";

        // act
        const result = service.openCustomDialogWithErrorCode(text, 'information', 'GMS-018');

        // assert
        expect(result).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { text: text, type: 'information', errorCode: 'GMS-018' } });
    });

    it('Should open info dialog', () => {
        // arrange
        const title = "Title";
        const text = "Text";

        // act
        const result = service.openInfoDialog(title, text);

        // assert
        expect(result).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { title: title, text: text, type: 'information' } });
    });

    it('Should open info dialog without title', () => {
        // arrange
        const text = "Text";

        // act
        const result = service.openInfoDialogWithoutTitle(text);

        // assert
        expect(result).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { text: text, type: 'information' } });
    });

    it('Should open warning dialog', () => {
        // arrange
        const text = "Text";

        // act
        const result = service.openWarningDialog(text);

        // assert
        expect(result).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(InfoDialog, { data: { text: text, type: 'warning' } });
    });

    it('Should open confirm delete dialog', () => {
        // arrange
        const text = "?";

        // act
        const result = service.openConfirmDeleteDialog(text);

        // assert
        expect(result).toBeTruthy();
        expect(dialog.open).toHaveBeenCalledWith(ConfirmDeleteDialog, { data: {
            result: true,
            confirmMessage: text
        } });
    });
});