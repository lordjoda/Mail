package mail.mail;

import mail.api.core.INbtWritable;
import mail.core.utils.Log;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.terraingen.OreGenEvent;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Johannes Lohrer <lohrer@dbs.ifi.lmu.de> on 03.10.2017.
 */
public abstract class SQLSavedData {

    Connection connection;
    protected final String tableName;
    protected final String key;
    protected final boolean load;

    public SQLSavedData(String tableName, String key, Connection connection) throws SQLException {
        this(tableName, key, connection, true);
    }

    public SQLSavedData(String tableName, String key, Connection connection, boolean load) throws SQLException {

        this.tableName = tableName;
        this.key = key;
        this.connection = connection;
        try {
            setupStatements();
        } catch (SQLException e) {
            Log.warning("Setup failed. Reconnect?", e);
            setupStatements();
        }

        this.load = load;

    }

    public abstract void load() throws SQLException;

    protected abstract void setupStatements() throws SQLException;

    public abstract void save() throws SQLException;

    protected void writeToStatement(INbtWritable writable, PreparedStatement statement, int position) throws IOException, SQLException {
        NBTTagCompound nbt = new NBTTagCompound();
        writable.writeToNBT(nbt);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CompressedStreamTools.writeCompressed(nbt, outputStream);

        statement.setBinaryStream(position, new ByteArrayInputStream(outputStream.toByteArray()));

    }

    public static NBTTagCompound readFormStatement(ResultSet rs, String columnName) throws IOException, SQLException {
        InputStream binaryStream;

        binaryStream = rs.getBinaryStream(columnName);

        return CompressedStreamTools.readCompressed(binaryStream);
    }


}
