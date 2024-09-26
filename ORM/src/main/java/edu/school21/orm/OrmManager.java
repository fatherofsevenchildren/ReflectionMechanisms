package edu.school21.orm;

import edu.school21.annotations.OrmColumn;
import edu.school21.annotations.OrmColumnId;
import edu.school21.annotations.OrmEntity;

import org.reflections.Reflections;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;


public class OrmManager {

    private static final Object CLASS_PATH = "edu.school21.models";
    private static final String QUERY_TEMPLATE = "DROP TABLE IF EXISTS %s; CREATE TABLE %s (%s);";
    private static final String OrmColumn_QUERY_TEMPLATE = "? ?";
    private static final Map<String,String> TO_SQL_TYPE = new HashMap<>();

    static {
        TO_SQL_TYPE.put("Integer", " INTEGER");
        TO_SQL_TYPE.put("String", " VARCHAR");
        TO_SQL_TYPE.put("Double", " DOUBLE PRECISION");
        TO_SQL_TYPE.put("Long", " BIGINT");
        TO_SQL_TYPE.put("Boolean", " BOOLEAN");
    }

    private DataSource dataSource;

    public OrmManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void init() throws SQLException {
        Reflections reflections = new Reflections("edu.school21.models");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(OrmEntity.class);
        for(Class<?> clazz : classes) {
            String tableName = clazz.getDeclaredAnnotation(OrmEntity.class).table();
            Field[] fields = clazz.getDeclaredFields();
            StringJoiner columnsName = new StringJoiner(",");
            for (Field field : fields) {
                if (field.isAnnotationPresent(OrmColumnId.class)) {
                    columnsName.add("identifier SERIAL PRIMARY KEY");
                } else if (field.isAnnotationPresent(OrmColumn.class)) {
                    String columName = field.getAnnotation(OrmColumn.class).name()
                            + TO_SQL_TYPE.get(field.getType().getSimpleName());
                    if (columName.endsWith("VARCHAR")){
                        columName = columName + "(" + field.getAnnotation(OrmColumn.class).length() + ")";
                    }
                    columnsName.add(columName);
                }
            }
            String query = String.format(QUERY_TEMPLATE, tableName, tableName, columnsName);
            try (Connection connection = dataSource.getConnection()) {
                connection.createStatement().execute(query);
            }
        }
    }

}
