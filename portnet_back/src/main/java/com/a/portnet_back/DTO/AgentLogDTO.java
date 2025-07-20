package com.a.portnet_back.DTO;

import java.time.LocalDateTime;

public class AgentLogDTO {
    private Long id;
    private String nomComplet;
    private String email;
    private boolean enabled;
    private LocalDateTime lastLogin;

    public Long getId() {
        return id;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setId(Long id) {
    }
    // autres champs utiles

    // constructeurs/getters/setters
}
