import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AnnouncementList } from "../model/annoucement-list.model";
import { Announcement } from "../model/announcement.model";
import { SaveServiceBase } from "./save-service-base";

/**
 * @author Peter Szrnka
 */
@Injectable({providedIn : "root"})
export class AnnouncementService extends SaveServiceBase<Announcement, AnnouncementList> {

    constructor(http : HttpClient) {
        super(http, "announcement");
    }
}