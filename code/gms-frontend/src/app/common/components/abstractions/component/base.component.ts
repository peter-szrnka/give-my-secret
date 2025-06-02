import { Directive, OnDestroy, OnInit } from "@angular/core";
import { Subject } from "rxjs";


/**
 * @author Peter Szrnka
 */
@Directive()
export abstract class BaseComponent implements OnInit, OnDestroy {


    destroy$ : Subject<boolean> = new Subject();

    ngOnInit(): void {
    }
    
    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

}