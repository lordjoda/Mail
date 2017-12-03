package mail.mail.connection;

import mail.core.utils.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Johannes Lohrer <lohrer@dbs.ifi.lmu.de> on 12.10.2017.
 */
public class ConnectionMySQL {
    private static boolean initSuccessful = false;

    static {
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
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
//            MysqlDataSource datasource = new MysqlDataSource();
//            datasource.setServerName("127.0.0.1");
//            datasource.setPort(serverPort);
//            datasource.setUser(userName);
//            datasource.setPassword(password);
//            datasource.setDatabaseName(databaseName);
            System.out.println("jdbc:mysql://" + serverAddress + ":" + serverPort + (databaseName.equals("") ? "" : "/" + databaseName) +
                    "?zeroDateTimeBehavior=convertToNull&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8");
            return DriverManager.getConnection("jdbc:mysql://" + serverAddress + ":" + serverPort  +
                    "?zeroDateTimeBehavior=convertToNull&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8", userName, password);
//            Connection connection = datasource.getConnection();
//            if(connection == null){
//                Log.info("connection null");
//                throw new RuntimeException("bla");
//            }
//            Log.info("connection successful");
//            return connection;
        } catch (SQLException e) {
            Log.error("Connection failed");
            Log.error(e.getLocalizedMessage());
            e.printStackTrace();

            return null;
        }

    }
}
