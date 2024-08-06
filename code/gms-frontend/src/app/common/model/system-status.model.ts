/**
 * @author Peter Szrnka
 */
export interface SystemStatus {
    status : string,
    authMode : string;
    version: string;
    built: string;
    containerHostType: string;
    containerId?: string;
}