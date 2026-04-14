package quvoncuz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClickErrorCode {
    SUCCESS(0, "Success"),
    SIGN_CHECK_FAILED(-1, "SIGN CHECK FAILED!"),
    INCORRECT_AMOUNT(-2, "Incorrect parameter amount"),
    ACTION_NOT_FOUND(-3, "Action not found"),
    ALREADY_PAID(-4, "Already paid"),
    USER_NOT_EXIST(-5, "User does not exist"),
    TRANSACTION_NOT_EXIST(-6, "Transaction does not exist"),
    FAILED_TO_UPDATE_USER(-7, "Failed to update user"),
    ERROR_IN_REQUEST(-8, "Error in request from click"),
    TRANSACTION_CANCELLED(-9, "Transaction cancelled");

    private final int code;
    private final String note;
}
