/**
 * @author Peter Szrnka
 */
export interface User {
    id? : number,
    username? : string,
    role : string
}

export const EMPTY_USER : User = {
    role: 'EMPTY'
};