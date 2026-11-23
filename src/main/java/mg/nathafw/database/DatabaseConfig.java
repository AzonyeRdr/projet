package mg.nathafw.database;

/** Configuration JDBC d'une base de données. */
public final class DatabaseConfig {
    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;

    public DatabaseConfig(String url, String username, String password, String driverClassName) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("L'URL JDBC est obligatoire.");
        }
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
    }

    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDriverClassName() { return driverClassName; }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DatabaseConfig)) return false;
        DatabaseConfig config = (DatabaseConfig) other;
        return url.equals(config.url) && equal(username, config.username)
                && equal(password, config.password)
                && equal(driverClassName, config.driverClassName);
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + (username == null ? 0 : username.hashCode());
        result = 31 * result + (password == null ? 0 : password.hashCode());
        result = 31 * result + (driverClassName == null ? 0 : driverClassName.hashCode());
        return result;
    }

    private boolean equal(Object left, Object right) {
        return left == null ? right == null : left.equals(right);
    }
}
