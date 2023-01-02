import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { RouterTestingModule } from "@angular/router/testing";
import { ConfirmDeleteDialog } from "./confirm-delete-dialog.component";

describe('ConfirmDeleteDialog', () => {
    let component : ConfirmDeleteDialog;

    // Fixtures
    let fixture : ComponentFixture<ConfirmDeleteDialog>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, MatDialogModule ],
            declarations : [ConfirmDeleteDialog],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : MatDialogRef, useValue : {
                    close : jest.fn()
                } },
                { provide : MAT_DIALOG_DATA, useValue : true }
            ]
        });
    });

    it('Should create component and close', () => {
        fixture = TestBed.createComponent(ConfirmDeleteDialog);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        component.onNoClick();

        expect(component.dialogRef.close).toHaveBeenCalled();
    });
});