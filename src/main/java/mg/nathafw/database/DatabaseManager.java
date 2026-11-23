package mg.nathafw.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Ouvre des connexions JDBC avec la configuration fournie au démarrage. */
public final class DatabaseManager {
    public static final String REQUEST_ATTRIBUTE = DatabaseManager.class.getName();
    private final DatabaseConfig config;

    public DatabaseManager(DatabaseConfig config) throws ClassNotFoundException {
        this.config = config;
        if (config.getDriverClassName() != null && !config.getDriverClassName().isBlank()) {
            Class.forName(config.getDriverClassName());
        }
    }

    /** Retourne une connexion que l'appelant doit fermer (try-with-resources). */
    public Connection getConnection() throws SQLException {
        Properties credentials = new Properties();
        if (config.getUsername() != null) credentials.setProperty("user", config.getUsername());
        if (config.getPassword() != null) credentials.setProperty("password", config.getPassword());
        return DriverManager.getConnection(config.getUrl(), credentials);
    }
}
