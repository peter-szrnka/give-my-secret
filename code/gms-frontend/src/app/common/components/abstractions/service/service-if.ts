import { Observable } from "rxjs";
import { IEntitySaveResponseDto } from "../../../model/entity-save-response.model";
import { Paging } from "../../../model/paging.model";
import { BaseList } from "../../../model/base-list";

/**
 * @author Peter Szrnka
 */
export interface Service<T> {

    save?(item : T) : Observable<IEntitySaveResponseDto>;

    delete(id : number): Observable<string>;

    list(paging : Paging) : Observable<BaseList<T>>;

    getById?(id : number) : Observable<T>;

    toggle?(id : number, enabled : boolean): Observable<string>;
}