package mail.mail.connection;

import mail.core.utils.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by lordjoda <lordjoda@gmail.com> on 12.10.2017.
 */
public class ConnectionMaria {
    private static boolean initSuccessful = false;

    static {
        try {

            Class.forName("org.mariadb.jdbc.Driver").newInstance();
            initSuccessful = true;
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException e) {
            Log.error(e.getLocalizedMessage());
            initSuccessful = false;
        }
    }

    public static Connection getConnection(String serverAddress, int serverPort, String userName, String password, String databaseName) {
        if (!initSuccessful)
            return null;

        try {
            System.out.println("jdbc:mariadb://" + serverAddress + ":" + serverPort + (databaseName.equals("") ? "" : "/" + databaseName));
            return DriverManager.getConnection("jdbc:mariadb://" + serverAddress + ":" + serverPort , userName, password);
        } catch (SQLException e) {
            Log.error("Connection failed");
            Log.error(e.getLocalizedMessage());
            e.printStackTrace();

            return null;
        }

    }
}
