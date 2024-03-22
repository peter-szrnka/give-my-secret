import { BaseDetail } from "../../../common/model/base-detail.model";

/**
 * @author Peter Szrnka
 */
export interface Message extends BaseDetail {
    id? : number,
    message : string,
    creationDate? : Date,
    opened : boolean;
    actionPath?: string;
}