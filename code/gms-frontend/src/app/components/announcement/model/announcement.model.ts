import { PageConfig } from "../../../common/model/common.model";

/**
 * @author Peter Szrnka
 */
export interface Announcement {
    id? : number;
    title : string;
    description : string;
    announcementDate? : Date;
}

export const EMPTY_ANNOUNCEMENT : Announcement = {
    title: "",
    description: ""
}

export const PAGE_CONFIG_ANNOUNCEMENT : PageConfig = {
    scope: "announcement",
    label: "Announcement"
};