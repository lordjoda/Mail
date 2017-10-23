package mail.mail;

import mail.core.config.Config;
import mail.core.utils.Log;
import mail.mail.connection.ConnectionMySQL;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Johannes Lohrer <lohrer@dbs.ifi.lmu.de> on 11.10.2017.
 */
public class ConnectionHandler {

    public static Connection initConnection() {
        Config.MailMode sqlMode = Config.SQLMode;
        String serverAddress = Config.serverAddress;
        int serverPort = Config.serverPort;
        String userName = Config.userName;
        String password = Config.password;
        String databaseName = Config.databaseName;
        Connection connection = null;
        switch (sqlMode) {
            case MYSql:
                connection = ConnectionMySQL.getConnection(serverAddress, serverPort, userName, password, databaseName);
                break;
            case None:

                return null;
            default:
                Log.warning("No connection for ");
                return null;
        }

        if (connection != null&&checkDatabase(connection, databaseName))
            return connection;
        return null;
    }

    private static boolean checkDatabase(Connection connection, String databaseName) {
//Setup database and tables if required.
        Log.info("Check and install database");
        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE DATABASE IF NOT EXISTS `" + databaseName + "`;");
            statement.close();

            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS `" + databaseName + "`.`TradeStation`(" +
                    "`ID` VARCHAR(200) NOT NULL,"+
                    "`Address` BLOB NOT NULL,"+
                    "`Name` VARCHAR(50) NULL," +
                    "`UUID` VARCHAR(50) NULL," +
                    "`Virtual` BIT NULL," +
                    "`Invalid` BIT NULL," +
                    "`Inventory` BLOB NULL," +
                    " PRIMARY KEY (`ID`)" +
                    ");");
            statement.close();

            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS `" + databaseName + "`.`PostOffice`(" +
                    "`ID` INT NOT NULL," +
                    "`Value` INT NULL," +
                    " PRIMARY KEY (`ID`)" +
                    ");"
            );
            statement.close();

            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS `" + databaseName + "`.`POBox`(" +
                    "`ID` VARCHAR(200) NOT NULL," +
                    "`Address` BLOB NULL," +
                    "`Inventory` BLOB NULL," +
                    "PRIMARY KEY (`ID`)" +
                    ");");
            statement.close();

            statement = connection.createStatement();
            statement.execute("USE `"+databaseName+"`;");
            statement.close();
        } catch (SQLException e) {
            Log.error("Initialization failed");
            Log.error(e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
        Log.info("DB Init completed");
        return true;

    }
}
