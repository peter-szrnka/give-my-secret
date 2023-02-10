import { Observable } from "rxjs";
import { ServiceBase } from "./service-base";
import { environment } from "../../../../../environments/environment";
import { BaseList } from "../../../model/base-list";
import { IEntitySaveResponseDto } from "../../../model/entity-save-response.model";
import { getHeaders } from "../../../utils/header-utils";

/**
 * @author Peter Szrnka
 */
export abstract class SaveServiceBase<T, L extends BaseList<T>> extends ServiceBase<T, L> {

    save(item : T) : Observable<IEntitySaveResponseDto> {
        return this.http.post<IEntitySaveResponseDto>(environment.baseUrl + 'secure/'  + this.scope, item, { withCredentials: true, headers : getHeaders() });
    }
}