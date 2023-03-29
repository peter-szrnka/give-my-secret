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
   admin? : boolean
}