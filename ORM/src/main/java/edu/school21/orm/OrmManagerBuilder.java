package edu.school21.orm;

import javax.sql.DataSource;

public class OrmManagerBuilder {
    private DataSource dataSource;

    public OrmManagerBuilder setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public OrmManager createOrmManager() {
        return new OrmManager(dataSource);
    }
}