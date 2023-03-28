import { AnnouncementList } from "../../announcement/model/annoucement-list.model";
import { EventList } from "../../event/model/event-list.model";

/**
 * @author Peter Szrnka
 */
export interface HomeData {
   apiKeyCount : number,
   keystoreCount : number,
   userCount : number,
   announcements : AnnouncementList,
   latestEvents : EventList,
   isAdmin : boolean
}