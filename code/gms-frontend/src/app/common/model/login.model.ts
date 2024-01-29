import { User } from "../../components/user/model/user.model";

/**
 * @author Peter Szrnka
 */
export interface Login {
    username?: string;
    credential?: string; 
}

export interface VerifyLogin {
    username?: string;
    verificationCode?: string; 
}

export enum AuthenticationPhase {
    BLOCKED = "BLOCKED",
    FAILED = "FAILED",
    MFA_REQUIRED = "MFA_REQUIRED",
    COMPLETED = "COMPLETED",
}

export interface LoginResponse {
    currentUser: User;
    phase: AuthenticationPhase;
}
