package edu.school21.orm;

import edu.school21.annotations.OrmColumn;
import edu.school21.annotations.OrmColumnId;
import edu.school21.annotations.OrmEntity;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import javax.sql.DataSource;
import org.reflections.Reflections;


public class OrmManager {

    private static final Object CLASS_PATH = "edu.school21.models";
    private static final String QUERY_TEMPLATE = "DROP TABLE IF EXISTS %s; CREATE TABLE %s (%s);";
    private static final Map<String, String> TO_SQL_TYPE = new HashMap<>();

    static {
        TO_SQL_TYPE.put("Integer", " INTEGER");
        TO_SQL_TYPE.put("String", " VARCHAR");
        TO_SQL_TYPE.put("Double", " DOUBLE PRECISION");
        TO_SQL_TYPE.put("Long", " BIGINT");
        TO_SQL_TYPE.put("Boolean", " BOOLEAN");
    }

    private final DataSource dataSource;

    public OrmManager(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void init() throws SQLException {
        Reflections reflections = new Reflections(CLASS_PATH);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(OrmEntity.class);
        for (Class<?> clazz : classes) {
            String tableName = clazz.getDeclaredAnnotation(OrmEntity.class).table();
            String columnNames = getColumnsName(clazz);
            String query = String.format(QUERY_TEMPLATE, tableName, tableName, columnNames);
            try (Connection connection = dataSource.getConnection()) {
                connection.createStatement().execute(query);
            }
        }
    }

    private String getColumnsName(final Class<?> clazz) {
        StringJoiner columnNames = new StringJoiner(",");
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(OrmColumnId.class)) {
                columnNames.add("identifier SERIAL PRIMARY KEY");
            } else if (field.isAnnotationPresent(OrmColumn.class)) {
                String columName = field.getAnnotation(OrmColumn.class).name()
                        + TO_SQL_TYPE.get(field.getType().getSimpleName());
                if (columName.endsWith("VARCHAR")) {
                    columName = columName + "("
                            + field.getAnnotation(OrmColumn.class).length() + ")";
                } else if (columName.endsWith("null")) {
                    throw new IllegalArgumentException("type not supported");
                }
                columnNames.add(columName);
            }
        }
        return columnNames.toString();
    }
}
