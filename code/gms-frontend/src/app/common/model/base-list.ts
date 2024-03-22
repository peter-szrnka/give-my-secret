/**
 * @author Peter Szrnka
 */
export interface BaseList<T> {
    resultList : T[];
    totalElements : number;
    error? : string;
}