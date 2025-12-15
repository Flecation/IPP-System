package projectgroup1.DAO;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

public class databaseConnection {
    private static HikariDataSource dataSource;

//    This is for the every one of the database name
    private static String url = "jdbc:mysql://localhost:3306/";
    private static String username = "root";
    private static String password = "123456";

    static{
        HikariConfig config =new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


}
