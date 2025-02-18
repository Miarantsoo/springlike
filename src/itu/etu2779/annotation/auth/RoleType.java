package itu.etu2779.annotation.auth;

public enum RoleType {
    Default(0),
    Client(1),
    Admin(2);

    public final int level;

    RoleType(int level) {
        this.level = level;
    }
}
