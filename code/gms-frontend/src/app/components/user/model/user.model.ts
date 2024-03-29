/**
 * @author Peter Szrnka
 */
export interface User {
    id? : number,
    username? : string,
    roles : string[]
}

export const EMPTY_USER : User = {
    roles : []
};