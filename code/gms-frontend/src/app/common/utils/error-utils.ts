/**
 * @author Peter Szrnka
 */
export const getErrorMessage = (err : any) : string => {
    return (err.error) ? err.error.message : err.message;
};

export const getErrorCode = (err : any) : string | undefined => {
    return (err.error) ? err.error.errorCode : undefined;
};