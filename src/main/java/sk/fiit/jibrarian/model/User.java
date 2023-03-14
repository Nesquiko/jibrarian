package sk.fiit.jibrarian.model;

import java.util.UUID;

public class User {
    private UUID id;
    private String email;
    private String passHash;
    private Role role;

    public User() {
    }

    public User(UUID id, String email, String passHash, Role role) {
        this.id = id;
        this.email = email;
        this.passHash = passHash;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
