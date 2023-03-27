import { AnnouncementList } from "../../announcement/model/annoucement-list.model";
import { Event } from "../../event/model/event.model";

/**
 * @author Peter Szrnka
 */
export interface HomeData {
   apiKeyCount : number,
   keystoreCount : number,
   userCount : number,
   announcements : AnnouncementList,
   latestEvents : Event[],
   isAdmin : boolean
}