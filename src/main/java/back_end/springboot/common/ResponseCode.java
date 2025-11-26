package back_end.springboot.common;

public enum ResponseCode {
    DE, // Database Error
    BR, // Bad Request
    SC, // Success
    NEI, // Not Exsist Id
    NEP, // Not Exsist Password
    DU, // Duplicate
    DUE, // Duplicate Email
    ABS,
    NV, // Not Verify
    IVT, // Invaild Token
    NCT, // Not Compatible Type
    NEW_SIGN // New Oauth SignIn
}
