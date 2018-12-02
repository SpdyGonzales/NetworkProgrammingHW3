package server.integration;

import server.model.File;
import server.model.UserAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileTransferDAO {
    private static final String TABLE_NAME1 = "USERSANDPASS";
    private static final String TABLE_NAME2 = "FILES";
    private static final String USER_COLUMN_NAME = "USER";
    private static final String PASS_COLUMN_NAME = "PASS";
    private static final String FILE_COLUMN_NAME = "FILENAME";
    private static final String OWNER_COLUMN_NAME = "OWNER";
    private static final String SIZE_COLUMN_NAME = "FILESIZE";
    private static final String READ_COLUMN_NAME = "READRIGHT";
    private static final String WRITE_COLUMN_NAME = "WRITERIGHT";
    private PreparedStatement createAccountStmt;
    private PreparedStatement findAccountStmt;
    private PreparedStatement insertFileStmt;
    private PreparedStatement fetchFileStmt;
    private PreparedStatement fetchAllFilesStmt;
    private PreparedStatement deleteFileStmt;

    public FileTransferDAO(String dbms, String datasource) throws FileTransferException {
        try{
            Connection connection = createDatasource(dbms, datasource);
            prepareStatements(connection);
        }catch (ClassNotFoundException | SQLException exception) {
            throw new FileTransferException("Connection failed.", exception);
        }
    }

    private Connection createDatasource(String dbms, String datasource) throws
            ClassNotFoundException, SQLException, FileTransferException {
        Connection connection = connectToFileDB(dbms, datasource);
        /*
        if (!userTableExists(connection)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE " + TABLE_NAME1
                    + " (" + USER_COLUMN_NAME + " VARCHAR(32) PRIMARY KEY,"
                    + PASS_COLUMN_NAME + " VARCHAR(32))");
        }
        if (!fileTableExists(connection)){
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE " + TABLE_NAME2
                    + " (" + FILE_COLUMN_NAME + " VARCHAR(32) PRIMARY KEY,"
                    + OWNER_COLUMN_NAME + " VARCHAR(32),"
                    + SIZE_COLUMN_NAME + " INT,"
                    + READ_COLUMN_NAME + " BIT,"
                    + WRITE_COLUMN_NAME + " BIT)");
        }
        */
        return connection;
    }
    private boolean userTableExists(Connection connection) throws SQLException {
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(tableNameColumn).equals(TABLE_NAME1)) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean fileTableExists(Connection connection) throws SQLException {
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(tableNameColumn).equals(TABLE_NAME2)) {
                    return true;
                }
            }
            return false;
        }
    }

    private Connection connectToFileDB(String dbms, String datasource)
            throws ClassNotFoundException, SQLException, FileTransferException {
        if (dbms.equalsIgnoreCase("derby")) {
            Class.forName("org.apache.derby.jdbc.ClientXADataSource");
            return DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/" + datasource + ";create=true");
        } else if (dbms.equalsIgnoreCase("mysql")) {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + datasource, "jdbc", "jdbc");
        } else {
            throw new FileTransferException("Couldn't create datasource");
        }
    }

    public UserAccount checkAccount(String userName) {
        ResultSet result = null;
        UserAccount userAccount = null;
        try {
            findAccountStmt.setString(1, userName);
            result = findAccountStmt.executeQuery();
            if (result.next()) {
                userAccount = new UserAccount(userName, result.getString(PASS_COLUMN_NAME));
            }
        } catch (SQLException sqle) {
            userAccount = null;
        } finally {
            try {
                result.close();
            } catch (Exception e) { }
        }
        return userAccount;
    }

    public void createAccount(UserAccount account) throws FileTransferException {
        String failureMsg = "Could not create the account: " + account;
        try {
            createAccountStmt.setString(1, account.getUsername());
            createAccountStmt.setString(2, account.getPassword());
            int rows = createAccountStmt.executeUpdate();
            if (rows != 1) {
                throw new FileTransferException(failureMsg);
            }

        } catch (SQLException sqle) {
            throw new FileTransferException(failureMsg, sqle);
        }

    }

    public List<File> findAllFiles() throws FileTransferException {
        String failureMsg = "Could not list accounts.";
        List<File> files = new ArrayList<>();
        try (ResultSet result = fetchAllFilesStmt.executeQuery()) {
            while (result.next()) {
                files.add(new File(result.getString(FILE_COLUMN_NAME)));
            }
        } catch (SQLException sqle) {
            throw new FileTransferException(failureMsg, sqle);
        }
        return files;
    }

    public void addFile(String fileName, String owner, int size, boolean readPerm, boolean writePerm) throws FileTransferException{
        String failureMsg = "Could not add file: " + fileName;
        try {
            insertFileStmt.setString(1, fileName);
            insertFileStmt.setString(2, owner);
            insertFileStmt.setInt(3, size);
            insertFileStmt.setBoolean(4,readPerm);
            insertFileStmt.setBoolean(5,writePerm);
            int rows = insertFileStmt.executeUpdate();
            if (rows != 1) {
                throw new FileTransferException(failureMsg);
            }

        } catch (SQLException sqle) {
            throw new FileTransferException(failureMsg, sqle);
        }

    }
    public void removeFile(String file) throws FileTransferException {
        try {
            deleteFileStmt.setString(1, file);
            deleteFileStmt.executeUpdate();
        } catch (SQLException sqle) {
            throw new FileTransferException("Could not delete the file: " + file, sqle);
        }
    }

    public File fetchFile(String fileName) throws FileTransferException {
        String failureMsg = "Could not search for specified account.";
        ResultSet result = null;
        File foundFile = null;
        try{
            fetchFileStmt.setString(1, fileName);
            result = fetchFileStmt.executeQuery();
            if (result.next()) {
                foundFile = new File(result.getString(FILE_COLUMN_NAME), result.getString(OWNER_COLUMN_NAME), result.getInt(SIZE_COLUMN_NAME), result.getBoolean(READ_COLUMN_NAME), result.getBoolean(WRITE_COLUMN_NAME));
            }
        }catch (SQLException sqle) {
            throw new FileTransferException(failureMsg, sqle);
        }finally {
            try {
                result.close();
            } catch (Exception e) {
                throw new FileTransferException(failureMsg, e);
            }
        }
        return foundFile;
    }


    private void prepareStatements(Connection connection) throws SQLException {

        createAccountStmt = connection.prepareStatement("INSERT INTO "
                + TABLE_NAME1 + " VALUES (?, ?)");
        findAccountStmt = connection.prepareStatement("SELECT * from "
                + TABLE_NAME1 + " WHERE USER = ?");
        insertFileStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME2 + " VALUES (?,?,?,?,?)");
        fetchAllFilesStmt = connection.prepareStatement("SELECT * from " + TABLE_NAME2);
        fetchFileStmt = connection.prepareStatement("SELECT * from " + TABLE_NAME2 + " WHERE FILENAME = ?");
        deleteFileStmt = connection.prepareStatement("DELETE FROM " + TABLE_NAME2 + " WHERE FILENAME = ?");
    }
}
