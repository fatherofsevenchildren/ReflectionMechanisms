package edu.school21;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.school21.models.User;
import edu.school21.orm.OrmManager;

public class Program
{
    public static void main( String[] args )
    {
        HikariConfig config = new HikariConfig(
                "/datasource.properties");
        OrmManager ormManager = new OrmManager(new HikariDataSource(config));
        ormManager.run(User.class);
    }
}
