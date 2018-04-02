package mail.mail;

import mail.core.config.Config;
import mail.core.utils.Log;
import mail.mail.connection.ConnectionException;
import mail.mail.connection.ConnectionMaria;
import mail.mail.connection.ConnectionMySQL;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by lordjoda <lordjoda@gmail.com> on 11.10.2017.
 */
public class ConnectionHandler {

    private static boolean initSucessfull;
    private static ConnectionHandler instance = null;

    private static ArrayList<SQLSavedData> listener = new ArrayList<>();

    static {
        try {
            instance = new ConnectionHandler();
            initSucessfull = true;
        } catch (ConnectionException e) {
            initSucessfull = false;
            Log.warning("No connection could be established");
        }
    }

    @Nullable
    private Connection connection;

    private ConnectionHandler() throws ConnectionException {
        initConnection();
    }

    private void initConnection() throws ConnectionException {
        Config.MailMode sqlMode = Config.SQLMode;
        String serverAddress = Config.serverAddress;
        int serverPort = Config.serverPort;
        String userName = Config.userName;
        String password = Config.password;
        String databaseName = Config.databaseName;

        switch (sqlMode) {
            case MYSql:
                connection = ConnectionMySQL.getConnection(serverAddress, serverPort, userName, password, databaseName);
                break;
            case MARIA:
                connection = ConnectionMaria.getConnection(serverAddress, serverPort, userName, password, databaseName);
                break;
            case None:
            default:
                Log.warning("No connectionHolder for " + sqlMode);
                connection = null;
                throw new ConnectionException();
        }

        if (connection == null || !checkDatabase(connection, databaseName)) {
            throw new ConnectionException();
        }


    }

    private static boolean checkDatabase(Connection connection, String databaseName) {
//Setup database and tables if required.
        Log.info("Check and install database");
        try {
            try (Statement databaseStatement = connection.createStatement()) {
                databaseStatement.execute("CREATE DATABASE IF NOT EXISTS `" + databaseName + "`;");

                try (Statement tradeStationStatement = connection.createStatement()) {
                    tradeStationStatement.execute("CREATE TABLE IF NOT EXISTS `" + databaseName + "`.`TradeStation`(" +
                            "`ID` VARCHAR(200) NOT NULL," +
                            "`Address` BLOB NOT NULL," +
                            "`Name` VARCHAR(50) NULL," +
                            "`UUID` VARCHAR(50) NULL," +
                            "`Virtual` BIT NULL," +
                            "`Invalid` BIT NULL," +
                            "`Inventory` BLOB NULL," +
                            " PRIMARY KEY (`ID`)" +
                            ");");


                    try (Statement postOfficeStatement = connection.createStatement()) {
                        postOfficeStatement.execute("CREATE TABLE IF NOT EXISTS `" + databaseName + "`.`PostOffice`(" +
                                "`ID` INT NOT NULL," +
                                "`Value` INT NULL," +
                                " PRIMARY KEY (`ID`)" +
                                ");"
                        );
                    }

                    try (Statement poboxStatement = connection.createStatement()) {
                        poboxStatement.execute("CREATE TABLE IF NOT EXISTS `" + databaseName + "`.`POBox`(" +
                                "`ID` VARCHAR(200) NOT NULL," +
                                "`Address` BLOB NULL," +
                                "`Inventory` BLOB NULL," +
                                "PRIMARY KEY (`ID`)" +
                                ");");
                    }

                    try (Statement statement = connection.createStatement()) {
                        statement.execute("USE `" + databaseName + "`;");
                    }
                }
            }
        } catch (SQLException e) {
            Log.error("Initialization failed");
            Log.error(e.getLocalizedMessage());
            return false;
        }
        Log.info("DB Init completed");
        return true;

    }

    public static boolean isInitSucessfull() {
        return initSucessfull;
    }


    public boolean isConnectionActive() {
        try {
            return (connection != null) && connection.isValid(0);
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }

    public static boolean verifyConnection() {
        if (!instance.isConnectionActive()) {
            try {
                instance.initConnection();
                listener.forEach(SQLSavedData::reloadStatements);

            } catch (ConnectionException e) {
                Log.error("Reconnecting failed!", e);
            }
            return false;
        }
        return true;
    }

    public static void addListener(SQLSavedData data) {
        if (!listener.contains(data)) {
            listener.add(data);
        }
    }

    public static void removeListener(SQLSavedData data) {
        listener.remove(data);
    }

    public static PreparedStatement getPreparedStatement(String statement) throws SQLException {
        Log.debug("Preparing statement: " + statement);

        return instance.connection.prepareStatement(statement);
    }
}
