package edu.school21;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.school21.models.User;
import edu.school21.orm.OrmManager;

import java.sql.SQLException;

public class Program
{
    public static void main( String[] args ) throws SQLException {
        HikariConfig config = new HikariConfig(
                "/datasource.properties");
        OrmManager ormManager = new OrmManager(new HikariDataSource(config));
            ormManager.init();
    }
}
