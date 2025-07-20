package com.a.portnet_back.DTO;

public class AuthResponse {
    private String token;
    private String role;
    private Object user;
    private String message;

    // Constructeur par défaut
    public AuthResponse() {}

    // Constructeur avec tous les paramètres
    public AuthResponse(String token, String role, Object user, String message) {
        this.token = token;
        this.role = role;
        this.user = user;
        this.message = message;
    }

    // Constructeur sans message
    public AuthResponse(String token, String role, Object user) {
        this.token = token;
        this.role = role;
        this.user = user;
        this.message = "Connexion réussie";
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public Object getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setToken(String token) {
        this.token = token;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", role='" + role + '\'' +
                ", user=" + user +
                ", message='" + message + '\'' +
                '}';
    }
}