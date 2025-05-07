/**
 * @author Peter Szrnka
 */
export interface SystemReadyData {
    ready : boolean,
    systemStatus : string,
    status: number,
    authMode : string,
    automaticLogoutTimeInMinutes?: number
}