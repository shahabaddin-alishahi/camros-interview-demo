package cameros.example.demo.config;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BasicConnectionPool implements ConnectionPool {

    private final String url;
    private final String user;
    private final String password;
    private final List<Connection> connectionPool;
    private final List<Connection> usedConnections = new ArrayList<>();
    private static final int INITIAL_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 20;
    private static final int MAX_TIMEOUT = 1;

    public synchronized static BasicConnectionPool create(String url, String user, String password) throws SQLException {
        List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(url, user, password));
        }
        return new BasicConnectionPool(url, user, password, pool);
    }

    private BasicConnectionPool(String url, String user, String password, List<Connection> connectionPool) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.connectionPool = connectionPool;
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                connectionPool.add(createConnection(url, user, password));
            } else {
                try {
                    wait();
                } catch (InterruptedException ie) {

                }
                return (getConnection());
            }
        }

        Connection connection = connectionPool.remove(connectionPool.size() - 1);

        if (connection.isClosed()){
            notifyAll();
            return getConnection();
        }

        if(!connection.isValid(MAX_TIMEOUT)){
            connection = createConnection(url, user, password);
        }

        usedConnections.add(connection);
        return connection;
    }

    @Override
    public synchronized boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        notifyAll();
        return usedConnections.remove(connection);

    }

    private synchronized static Connection createConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }

    @Override
    public List<Connection> getConnectionPool() {
        return connectionPool;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void shutdown() throws SQLException {
        usedConnections.forEach(this::releaseConnection);
        for (Connection c : connectionPool) {
            c.close();
        }
        connectionPool.clear();
    }
}
