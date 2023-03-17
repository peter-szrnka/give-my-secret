import { Observable } from "rxjs";

/**
 * @author Peter Szrnka
 */
export interface ToggleService {

    toggle(id : number, enabled : boolean): Observable<string>;
}