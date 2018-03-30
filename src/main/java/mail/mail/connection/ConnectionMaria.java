package mail.mail.connection;

import mail.core.utils.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Johannes Lohrer <lohrer@dbs.ifi.lmu.de> on 12.10.2017.
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
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/DB?user=root&password=myPassword");

//            MysqlDataSource datasource = new MysqlDataSource();
//            datasource.setServerName("127.0.0.1");
//            datasource.setPort(serverPort);
//            datasource.setUser(userName);
//            datasource.setPassword(password);
//            datasource.setDatabaseName(databaseName);
            System.out.println("jdbc:mariadb://" + serverAddress + ":" + serverPort + (databaseName.equals("") ? "" : "/" + databaseName));
            return DriverManager.getConnection("jdbc:mariadb://" + serverAddress + ":" + serverPort , userName, password);
//            Connection connectionHolder = datasource.getConnection();
//            if(connectionHolder == null){
//                Log.info("connectionHolder null");
//                throw new RuntimeException("bla");
//            }
//            Log.info("connectionHolder successful");
//            return connectionHolder;
        } catch (SQLException e) {
            Log.error("Connection failed");
            Log.error(e.getLocalizedMessage());
            e.printStackTrace();

            return null;
        }

    }
}
