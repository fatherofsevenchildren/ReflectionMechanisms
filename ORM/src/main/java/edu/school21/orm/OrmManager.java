package edu.school21.orm;

import edu.school21.annotations.OrmEntity;

import javax.sql.DataSource;

public class OrmManager {

    DataSource dataSource;

    public OrmManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void run(Class<?> clazz) {
        System.out.println(clazz.getAnnotation(OrmEntity.class).table());
    }

}
