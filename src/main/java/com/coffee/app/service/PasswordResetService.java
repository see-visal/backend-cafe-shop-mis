package com.coffee.app.service;

public interface PasswordResetService {
    /** Request an OTP for the given email. Sends it via Telegram (if user has telegramChatId) or the shop chat. */
    void requestReset(String email);

    /** Verify the OTP and update the user's password. */
    void resetPassword(String otp, String newPassword);
}
