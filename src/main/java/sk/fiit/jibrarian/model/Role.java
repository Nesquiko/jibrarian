package sk.fiit.jibrarian.model;

public enum Role {
    MEMBER("member"),
    LIBRARIAN("librarian"),
    ADMIN("admin");

    private final String dbValue;

    Role(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static Role fromDbValue(String dbValue) {
        for (Role role : values()) {
            if (role.getDbValue().equals(dbValue)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No role with db value " + dbValue);
    }
}
