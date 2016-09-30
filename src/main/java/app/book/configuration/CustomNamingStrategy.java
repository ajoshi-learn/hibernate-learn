package app.book.configuration;

import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * Created by ajoshi on 30-Sep-16.
 */
public class CustomNamingStrategy extends ImprovedNamingStrategy {
    @Override
    public String classToTableName(String className) {
        return StringHelper.unqualify(className);
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return propertyName;
    }

    @Override
    public String tableName(String tableName) {
        return "AJ_" + tableName;
    }

    @Override
    public String columnName(String columnName) {
        return columnName;
    }
}
