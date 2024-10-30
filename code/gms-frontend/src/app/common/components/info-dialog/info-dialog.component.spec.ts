import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { RouterTestingModule } from "@angular/router/testing";
import { InfoDialog } from "./info-dialog.component";
import { TranslatorModule } from "../pipes/translator/translator.module";

/**
 * @author Peter Szrnka
 */
describe('InfoDialog', () => {
    let component : InfoDialog;

    // Fixtures
    let fixture : ComponentFixture<InfoDialog>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports : [RouterTestingModule, MatDialogModule, TranslatorModule ],
            declarations : [InfoDialog],
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
        fixture = TestBed.createComponent(InfoDialog);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
        component.closeDialog();

        expect(component.dialogRef.close).toHaveBeenCalled();
    });
});