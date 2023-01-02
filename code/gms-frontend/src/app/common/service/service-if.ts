import { Observable } from "rxjs";
import { IEntitySaveResponseDto } from "../model/entity-save-response.model";
import { Paging } from "../model/paging.model";

export interface Service<T> {

    save?(item : T) : Observable<IEntitySaveResponseDto>;

    delete(id : number): Observable<string>;

    list(paging : Paging) : Observable<T[]>;

    getById?(id : number) : Observable<T>;

    count?() : Observable<number>;
}