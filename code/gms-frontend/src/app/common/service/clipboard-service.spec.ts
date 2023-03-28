import { TestBed } from "@angular/core/testing";
import { ClipboardService } from "./clipboard-service";
import { Clipboard } from '@angular/cdk/clipboard';
import { MatSnackBar } from "@angular/material/snack-bar";

describe('ClipboardService', () => {
    let service : ClipboardService;
    let clipboard : any;
    let snackbar : any;

    const configureTestBed = () => {
        TestBed.configureTestingModule({
            imports: [],
            providers : [
              ClipboardService,
              { provide : Clipboard, useValue : clipboard },
              { provide : MatSnackBar, useValue : snackbar }
            ]
          });
          service = TestBed.inject(ClipboardService);
    };
  
    it.each([true, false]) ('Should copy value', (input) => {
        const pendingCopy : any = {
            copy : jest.fn().mockImplementation(() => input),
            destroy : jest.fn()
        };
        snackbar = {
            open : jest.fn()
        };
        clipboard = {
            beginCopy : jest.fn().mockReturnValue(pendingCopy)
        };
        configureTestBed();

        // act
        service.copyValue('copied-value', 'snackbar message');

        expect(service).toBeTruthy();
        expect(clipboard.beginCopy).toHaveBeenCalled();
        expect(pendingCopy.copy).toHaveBeenCalled();
        expect(pendingCopy.destroy).toHaveBeenCalledTimes(input ? 1 : 0);
        expect(snackbar.open).toHaveBeenCalledTimes(input ? 1 : 0);
    });
});