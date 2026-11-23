package com.Am.demo.util;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import mg.nathafw.database.DatabaseManager;

public class Genutil {
    public static <T> int create(DatabaseManager databaseManager, String tableName,
            T entity, String generatedKey) throws SQLException, ReflectiveOperationException {
        List<Property> properties = getReadableProperties(entity);
        if (properties.isEmpty()) {
            throw new IllegalArgumentException("La classe ne contient aucune propriété persistable");
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        for (Property property : properties) {
            if (property.columnName.equalsIgnoreCase(generatedKey)) {
                continue;
            }
            if (columns.length() > 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(quoteIdentifier(property.columnName));
            placeholders.append("?");
        }

        String sql = "INSERT INTO " + quoteIdentifier(tableName)
                + " (" + columns + ") VALUES (" + placeholders + ") RETURNING "
                + quoteIdentifier(generatedKey);
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            int parameterIndex = 1;
            for (Property property : properties) {
                if (!property.columnName.equalsIgnoreCase(generatedKey)) {
                    statement.setObject(parameterIndex++, property.getValue(entity));
                }
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("La base n'a pas retourné la clé générée");
                }
                return resultSet.getInt(1);
            }
        }
    }

    public static <T> int create(DatabaseManager databaseManager, String tableName,
            T entity) throws SQLException, ReflectiveOperationException {
        List<Property> properties = getReadableProperties(entity);
        if (properties.isEmpty()) {
            throw new IllegalArgumentException("La classe ne contient aucune propriété persistable");
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        for (Property property : properties) {
            if (columns.length() > 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(quoteIdentifier(property.columnName));
            placeholders.append("?");
        }

        String sql = "INSERT INTO " + quoteIdentifier(tableName)
                + " (" + columns + ") VALUES (" + placeholders + ")";
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, properties, entity);
            return statement.executeUpdate();
        }
    }

    public static <T> int update(DatabaseManager databaseManager, String tableName,
            String primaryKey, T entity) throws SQLException, ReflectiveOperationException {
        List<Property> properties = getReadableProperties(entity);
        Property key = findProperty(properties, primaryKey);
        if (key == null) {
            throw new IllegalArgumentException("Clé primaire introuvable : " + primaryKey);
        }

        StringBuilder assignments = new StringBuilder();
        for (Property property : properties) {
            if (property.columnName.equalsIgnoreCase(primaryKey)) {
                continue;
            }
            if (assignments.length() > 0) {
                assignments.append(", ");
            }
            assignments.append(quoteIdentifier(property.columnName)).append(" = ?");
        }
        if (assignments.length() == 0) {
            throw new IllegalArgumentException("Aucune colonne à mettre à jour");
        }

        String sql = "UPDATE " + quoteIdentifier(tableName)
                + " SET " + assignments
                + " WHERE " + quoteIdentifier(primaryKey) + " = ?";
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            int parameterIndex = 1;
            for (Property property : properties) {
                if (!property.columnName.equalsIgnoreCase(primaryKey)) {
                    statement.setObject(parameterIndex++, property.getValue(entity));
                }
            }
            statement.setObject(parameterIndex, key.getValue(entity));
            return statement.executeUpdate();
        }
    }

    public static int delete(DatabaseManager databaseManager, String tableName,
            String primaryKey, Object value) throws SQLException {
        String sql = "DELETE FROM " + quoteIdentifier(tableName)
                + " WHERE " + quoteIdentifier(primaryKey) + " = ?";
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, value);
            return statement.executeUpdate();
        }
    }

    public static <T> List<T> findAll(DatabaseManager databaseManager, String tableName,
            Class<T> entityClass) throws SQLException, ReflectiveOperationException {
        String sql = "SELECT * FROM " + quoteIdentifier(tableName);
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            return mapResultSet(resultSet, entityClass);
        }
    }

    public static <T> T findById(DatabaseManager databaseManager, String tableName,
            String primaryKey, Object value, Class<T> entityClass)
            throws SQLException, ReflectiveOperationException {
        String sql = "SELECT * FROM " + quoteIdentifier(tableName)
                + " WHERE " + quoteIdentifier(primaryKey) + " = ?";
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapRow(resultSet, entityClass);
            }
        }
    }

    public static <T> T findByColumn(DatabaseManager databaseManager, String tableName,
            String columnName, Object value, Class<T> entityClass)
            throws SQLException, ReflectiveOperationException {
        String sql = "SELECT * FROM " + quoteIdentifier(tableName)
                + " WHERE " + quoteIdentifier(columnName) + " = ?";
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet, entityClass) : null;
            }
        }
    }

    private static void setParameters(PreparedStatement statement,
            List<Property> properties, Object entity)
            throws SQLException, ReflectiveOperationException {
        for (int i = 0; i < properties.size(); i++) {
            Object value = properties.get(i).getValue(entity);
            statement.setObject(i + 1, value);
        }
    }

    private static <T> List<T> mapResultSet(ResultSet resultSet, Class<T> entityClass)
            throws SQLException, ReflectiveOperationException {
        List<T> entities = new ArrayList<>();
        while (resultSet.next()) {
            entities.add(mapRow(resultSet, entityClass));
        }
        return entities;
    }

    private static <T> T mapRow(ResultSet resultSet, Class<T> entityClass)
            throws SQLException, ReflectiveOperationException {
        T entity = entityClass.getDeclaredConstructor().newInstance();
        ResultSetMetaData metadata = resultSet.getMetaData();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String columnName = metadata.getColumnLabel(i);
            Method setter = findSetter(entityClass, columnName);
            if (setter == null) {
                continue;
            }

            Object value = resultSet.getObject(columnName);
            Class<?> parameterType = setter.getParameterTypes()[0];
            value = convertValue(value, parameterType);
            setter.invoke(entity, value);
        }
        return entity;
    }

    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType.isInstance(value)) {
            return value;
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Number number) {
                return number.intValue() != 0;
            }
        }
        if (targetType == java.sql.Date.class && value instanceof Timestamp timestamp) {
            return new java.sql.Date(timestamp.getTime());
        }
        if (targetType == java.sql.Date.class && value instanceof java.util.Date date) {
            return new java.sql.Date(date.getTime());
        }
        if (targetType == Integer.class || targetType == int.class) {
            return ((Number) value).intValue();
        }
        if (targetType == Long.class || targetType == long.class) {
            return ((Number) value).longValue();
        }
        if (targetType == Double.class || targetType == double.class) {
            return ((Number) value).doubleValue();
        }
        if (targetType == String.class) {
            return value.toString();
        }
        return value;
    }

    private static List<Property> getReadableProperties(Object entity)
            throws ReflectiveOperationException {
        List<Property> properties = new ArrayList<>();
        for (Method method : entity.getClass().getMethods()) {
            if (!method.getName().startsWith("get")
                    || method.getParameterCount() != 0
                    || method.getReturnType() == void.class
                    || method.getDeclaringClass() == Object.class
                    || method.getReturnType().getName().startsWith("java.lang.reflect")
                    || !isSimpleType(method.getReturnType())) {
                continue;
            }
            String columnName = decapitalize(method.getName().substring(3));
            properties.add(new Property(columnName, method));
        }
        return properties;
    }

    private static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || type == Boolean.class
                || type == Integer.class
                || type == Long.class
                || type == Double.class
                || type == java.sql.Date.class
                || type == java.util.Date.class
                || type == Timestamp.class;
    }

    private static Property findProperty(List<Property> properties, String columnName) {
        for (Property property : properties) {
            if (property.columnName.equalsIgnoreCase(columnName)) {
                return property;
            }
        }
        return null;
    }

    private static Method findSetter(Class<?> entityClass, String columnName) {
        String normalizedColumn = normalizeForComparison(columnName);
        for (Class<?> current = entityClass; current != null; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getName().startsWith("set")
                        && method.getParameterCount() == 1
                        && normalizeForComparison(method.getName().substring(3)).equals(normalizedColumn)) {
                    return method;
                }
            }
        }
        return null;
    }

    private static String normalizeColumnName(String name) {
        StringBuilder normalized = new StringBuilder();
        for (String part : name.split("_")) {
            normalized.append(capitalize(part));
        }
        return normalized.toString();
    }

    private static String normalizeForComparison(String name) {
        return name.replace("_", "").toLowerCase(java.util.Locale.ROOT);
    }

    private static String quoteIdentifier(String identifier) {
        if (!identifier.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Identifiant SQL invalide : " + identifier);
        }
        // Les noms camelCase de Java correspondent aux noms non cités en
        // PostgreSQL, qui sont automatiquement repliés en minuscules.
        return "\"" + identifier.toLowerCase(java.util.Locale.ROOT) + "\"";
    }

    private static String decapitalize(String value) {
        if (value.isEmpty()) {
            return value;
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    private static String capitalize(String value) {
        if (value.isEmpty()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private static final class Property {
        private final String columnName;
        private final Method getter;

        private Property(String columnName, Method getter) {
            this.columnName = columnName;
            this.getter = getter;
        }

        private Object getValue(Object entity)
                throws ReflectiveOperationException {
            return getter.invoke(entity);
        }
    }
}
