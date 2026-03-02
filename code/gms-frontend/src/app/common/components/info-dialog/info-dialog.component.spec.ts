import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { InfoDialog } from "./info-dialog.component";
import { DialogData } from "./dialog-data.model";
import { vi } from "vitest";
import { TranslatorPipe } from "../pipes/translator/translator.pipe";

/**
 * @author Peter Szrnka
 */
describe('InfoDialog', () => {
    let component : InfoDialog;
    let dialogData: DialogData = {
        text: "config.text",
        type: "information"
    };

    // Fixtures
    let fixture : ComponentFixture<InfoDialog>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports : [InfoDialog,MatDialogModule, TranslatorPipe ],
            schemas : [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
            providers: [
                { provide : MatDialogRef, useValue : {
                    close : vi.fn()
                } },
                { provide : MAT_DIALOG_DATA, useValue : dialogData }
            ]
        });
    });

    it('Should create component with title', () => {
        dialogData.title = "config.title";
        fixture = TestBed.createComponent(InfoDialog);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
    });

    it('Should create component without title', () => {
        dialogData.title = undefined;
        fixture = TestBed.createComponent(InfoDialog);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component).toBeTruthy();
    });
});