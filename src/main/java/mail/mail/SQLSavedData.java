package mail.mail;

import mail.api.core.INbtWritable;
import mail.core.utils.Log;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lordjoda <lordjoda@gmail.com> on 03.10.2017.
 */
public abstract class SQLSavedData {

    protected final String tableName;
    protected final String key;
    protected final boolean load;

    public SQLSavedData(String tableName, String key) throws SQLException {
        this(tableName, key, true);
    }

    public SQLSavedData(String tableName, String key, boolean load) throws SQLException {

        this.tableName = tableName;
        this.key = key;
        try {
            setupStatements();
        } catch (SQLException e) {
            Log.warning("Setup failed. Reconnect?", e);
            setupStatements();
        }

        this.load = load;
        ConnectionHandler.addListener(this);
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

    /**
     * this shall be called if the connection was closed and has to be reloaded
     */
    public void reloadStatements() {

    }


}
