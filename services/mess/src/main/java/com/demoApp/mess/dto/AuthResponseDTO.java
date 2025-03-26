package com.demoApp.mess.dto;

import java.util.Objects;

public class AuthResponseDTO {
    private boolean success;
    private String message;
    private String token;

    // ✅ No-argument constructor
    public AuthResponseDTO() {
    }

    // ✅ Parameterized constructor
    public AuthResponseDTO(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    // ✅ Getter and Setter for success
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // ✅ Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // ✅ Getter and Setter for token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // ✅ Override toString() for better debugging
    @Override
    public String toString() {
        return "AuthResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    // ✅ Override equals() for object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AuthResponseDTO that = (AuthResponseDTO) obj;
        return success == that.success &&
                Objects.equals(message, that.message) &&
                Objects.equals(token, that.token);
    }

    // ✅ Override hashCode() to generate a unique hash code
    @Override
    public int hashCode() {
        return Objects.hash(success, message, token);
    }
}
