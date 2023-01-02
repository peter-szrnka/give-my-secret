

export const getErrorMessage = (err : any) : string => {
    return (err.error) ? err.error.message : err.message;
};