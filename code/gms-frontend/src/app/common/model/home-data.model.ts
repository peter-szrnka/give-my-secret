import { Announcement } from "./announcement.model";
import { Event } from "../../components/event/model/event.model";

/**
 * @author Peter Szrnka
 */
export interface HomeData {
   apiKeyCount : number,
   keystoreCount : number,
   userCount : number,
   announcements : Announcement[],
   latestEvents : Event[],
   isAdmin : boolean
}