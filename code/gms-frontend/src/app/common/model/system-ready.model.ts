/**
 * @author Peter Szrnka
 */
export interface SystemReadyData {
    ready : boolean,
    status: number,
    authMode : string,
    automaticLogoutTimeInMinutes?: number
}