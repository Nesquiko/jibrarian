package sk.fiit.jibrarian.model;

public enum ItemType {
    BOOK("book"),
    ARTICLE("article"),
    MAGAZINE("magazine");

    private final String dbValue;

    ItemType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ItemType fromDbValue(String dbValue) {
        for (ItemType itemType : values()) {
            if (itemType.getDbValue().equals(dbValue)) {
                return itemType;
            }
        }
        throw new IllegalArgumentException("No item type with db value " + dbValue);
    }
}
