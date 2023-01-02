import { Announcement } from "./announcement.model";
import { Event } from "./event.model";

export interface HomeData {
   apiKeyCount : number,
   keystoreCount : number,
   userCount : number,
   announcements : Announcement[],
   latestEvents : Event[],
   isAdmin : boolean
}