import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { ConfirmDeleteDialog } from "./confirm-delete-dialog.component";
import { TranslatorModule } from "../pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
describe('ConfirmDeleteDialog', () => {
    let component : ConfirmDeleteDialog;
    let confirmMessage: string | undefined;

    // Fixtures
    let fixture : ComponentFixture<ConfirmDeleteDialog>;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports : [ MatDialogModule, TranslatorModule ],
            declarations : [ConfirmDeleteDialog],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : MatDialogRef, useValue : {
                    close : jest.fn()
                } },
                { provide : MAT_DIALOG_DATA, useValue : {
                    confirmMessage : confirmMessage,
                    result : true
                } }
            ]
        });

        fixture = TestBed.createComponent(ConfirmDeleteDialog);
        component = fixture.componentInstance;
        fixture.detectChanges();
    };

    it('Should create component', () => {
        configureTestBed();

        expect(component).toBeTruthy();
    });
});