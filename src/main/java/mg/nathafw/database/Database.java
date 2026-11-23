package mg.nathafw.database;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;

/** Point d'accès aux connexions JDBC pendant une requête NathaFw. */
public final class Database {
    private Database() { }

    public static Connection getConnection(HttpServletRequest request) throws SQLException {
        Object manager = request.getAttribute(DatabaseManager.REQUEST_ATTRIBUTE);
        if (!(manager instanceof DatabaseManager)) {
            throw new IllegalStateException("Base non configurée. Définissez DB_URL dans web.xml.");
        }
        DatabaseManager databaseManager = (DatabaseManager) manager;
        return databaseManager.getConnection();
    }
}
