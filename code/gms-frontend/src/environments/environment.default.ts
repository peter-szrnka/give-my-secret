/**
 * @author Peter Szrnka
 */
export interface Environment {
  production : boolean,
  baseUrl : string,
  enableMock: boolean
}

export const defaultEnvironment : Environment = {
  production: false,
  baseUrl: "http://localhost:8080/",
  enableMock: false
};