/**
 * @author Peter Szrnka
 */
export interface SystemReadyData {
    ready : boolean, // TODO Deprecate this field
    systemStatus : string,
    status: number,
    authMode : string,
    automaticLogoutTimeInMinutes?: number
}