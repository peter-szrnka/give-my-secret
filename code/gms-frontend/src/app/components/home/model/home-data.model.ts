import { AnnouncementList } from "../../announcement/model/annoucement-list.model";
import { EventList } from "../../event/model/event-list.model";

/**
 * @author Peter Szrnka
 */
export interface HomeData {
   announcementCount : number,
   apiKeyCount : number,
   keystoreCount : number,
   secretCount : number,
   userCount : number,
   announcements : AnnouncementList,
   events : EventList,
   role? : string
}

export const EMPTY_HOME_DATA: HomeData = {
   events: {
    resultList: [],
    totalElements: 0
   },
   announcements: {
    resultList: [],
    totalElements: 0
   },
   userCount: 0,
   role: undefined,
   apiKeyCount: 0,
   keystoreCount: 0,
   announcementCount: 0,
   secretCount: 0
};