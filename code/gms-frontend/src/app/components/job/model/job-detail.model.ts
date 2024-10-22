/**
 * @author Peter Szrnka
 */
export interface JobDetail {
    id: number;
    name: string;
    status: string;
    creationDate: Date;
    startTime: Date;
    endTime?: Date;
    duration?: number;
    message?: string;
}