import { Directive, OnDestroy } from "@angular/core";
import { Subject } from "rxjs";

/**
 * @author Peter Szrnka
 */
@Directive()
export abstract class BaseComponent implements OnDestroy {

    destroy$ : Subject<boolean> = new Subject();
    
    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }
}