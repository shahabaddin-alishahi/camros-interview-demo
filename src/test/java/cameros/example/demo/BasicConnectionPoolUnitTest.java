package cameros.example.demo;

import cameros.example.demo.config.BasicConnectionPool;
import cameros.example.demo.config.ConnectionPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BasicConnectionPoolUnitTest {

    private static ConnectionPool connectionPool;

    @BeforeAll
    public static void setUpBasicConnectionPoolInstance() throws SQLException {
        connectionPool = BasicConnectionPool.create("jdbc:h2:mem:test", "user", "password");
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenCalledgetConnection_thenCorrect() throws Exception {
        assertTrue(connectionPool.getConnection().isValid(1));
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenCalledreleaseConnection_thenCorrect() throws Exception {
        Connection connection = connectionPool.getConnection();
        Assertions.assertTrue(connectionPool.releaseConnection(connection));
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenCalledgetUrl_thenCorrect() {
        Assertions.assertTrue(connectionPool.getUrl().equalsIgnoreCase("jdbc:h2:mem:test"));
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenCalledgetUser_thenCorrect() {
        Assertions.assertTrue(connectionPool.getUser().equalsIgnoreCase("user"));
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenCalledgetPassword_thenCorrect() {
        Assertions.assertTrue(connectionPool.getPassword().equalsIgnoreCase("password"));
    }

    @Test()
    public void givenBasicConnectionPoolInstance_whenAskedForMoreThanMax_thenError() throws Exception {
        ConnectionPool cp = BasicConnectionPool.create("jdbc:h2:mem:test", "user", "password");
        final int MAX_POOL_SIZE = 20;
        assertThrowsExactly(RuntimeException.class, () -> {
            for (int i = 0; i < MAX_POOL_SIZE + 1; i++) {
                cp.getConnection();
            }
        });
    }

    @Test
    public void givenBasicConnectionPoolInstance_whenSutdown_thenEmpty() throws Exception {
        ConnectionPool cp = BasicConnectionPool.create("jdbc:h2:mem:test", "user", "password");
        Assertions.assertFalse(cp.getSize() ==10);

        ((BasicConnectionPool) cp).shutdown();
        Assertions.assertTrue(cp.getSize() == 0);
    }
}
