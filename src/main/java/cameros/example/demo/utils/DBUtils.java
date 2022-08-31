package cameros.example.demo.utils;

import cameros.example.demo.config.BasicConnectionPool;
import cameros.example.demo.config.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.System.out;

public class DBUtils {

    private static ConnectionPool connectionPool;

    static {
        Date startupTime = new Date();
        try {
            connectionPool = BasicConnectionPool.create("jdbc:h2:~/file:test", "user", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Long runningTime = new Date().getTime() - startupTime.getTime();
            out.println("\n (DBUtils) =>  Total Running time for creating all connection in connection pool (" + runningTime + ") => \n");
        }
    }

    public void writingDBPreparedStatement(String name) throws SQLException {
        Connection connection = connectionPool.getConnection();
        String query = "INSERT INTO persons( name) VALUES( ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, name);
        preparedStatement.executeUpdate();
        connectionPool.releaseConnection(connection);
        out.println(name);
    }

    public void preparedDB(String callerName) {
        Date startupTime = new Date();
        try {
            String query = "create table if not exists PERSONS (ID INT , NAME VARCHAR(100))";
            Connection connection = connectionPool.getConnection();
            connection.createStatement().executeUpdate(query);
            connectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Long runningTime = new Date().getTime() - startupTime.getTime();
            out.println("\n " + callerName + "(DBUtils) => Total Running time for preparing database and create one table (" + runningTime + ") => \n");
        }
    }

    public void oneTransactionDBPreparedStatement(List<String> list) throws SQLException {
        Date startupTime = new Date();
        Connection connection = connectionPool.getConnection();
        connection.setAutoCommit(false);
        list.forEach(s -> {
            String query = "INSERT INTO persons( name) VALUES( ?)";
            PreparedStatement preparedStatement = null;
            try {
                out.println(s);
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, s);
                preparedStatement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        connection.commit();
        connectionPool.releaseConnection(connection);
        Long runningTime = new Date().getTime() - startupTime.getTime();
        out.println("\n (DBUtils) =>  Running time for all in one transaction (" + runningTime + ") => \n");

    }
}
