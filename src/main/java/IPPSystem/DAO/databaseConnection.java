package IPPSystem.DAO;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

public class databaseConnection {
    private static HikariDataSource dataSource;

//    This is for the every one of the database name


    static{
        HikariConfig config =new HikariConfig();
        config.setJdbcUrl(dotenv.mysql_url);
        config.setUsername(dotenv.mysql_username);
        config.setPassword(dotenv.mysql_password);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }





}
