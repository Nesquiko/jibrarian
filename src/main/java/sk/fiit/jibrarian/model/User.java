package sk.fiit.jibrarian.model;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && email.equals(user.email) && passHash.equals(user.passHash) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, passHash, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", passHash='" + passHash + '\'' +
                ", role=" + role +
                '}';
    }
}
