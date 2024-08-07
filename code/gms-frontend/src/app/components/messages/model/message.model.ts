import { BaseDetail } from "../../../common/model/base-detail.model";

/**
 * @author Peter Szrnka
 */
export interface Message extends BaseDetail {
    message : string,
    creationDate? : Date,
    opened : boolean;
    actionPath?: string;
    selected?: boolean;
}