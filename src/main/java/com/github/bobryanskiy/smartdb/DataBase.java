package com.github.bobryanskiy.smartdb;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DataBase {
    static Connection connection;
    static Statement statement;
    static String dbName;
    private static final int AMOUNT_TO_GET_INFO_FROM_TABLE = 10;

    public static void connect(String user, String password) throws SQLException {
//        user = password = "bobr";
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", user, password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new SQLException("Ошибка! Неверный логин или пароль");
        }
        dbName = connection.getCatalog();
        if (dbName == null) throw new SQLException("Датабаз не создано");
    }

    public static ArrayList<String> getTables() throws SQLException {
        ArrayList<String> tables = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("show tables");
        while (resultSet.next()) {
            tables.add(resultSet.getString(1));
        }
        if (tables.isEmpty()) throw new SQLException("Таблиц не создано");
        return tables;
    }

    public static LinkedHashMap<String, String> getColumns(String tableName) throws SQLException {
        LinkedHashMap<String, String> columns = new LinkedHashMap<>();
        ArrayList<String> keys = new ArrayList<>(), values = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("show columns from " + tableName);
        while (resultSet.next()) {
            keys.add(resultSet.getString(1));
        }
        resultSet = statement.executeQuery("SELECT DATA_TYPE FROM information_schema.columns WHERE table_name = "
                + "'" + tableName + "'" + "order by table_name, ordinal_position");
        while (resultSet.next()) {
            values.add(resultSet.getString(1));
        }
        for (int i = 0; i < keys.size(); ++i) {
            columns.put(keys.get(i), values.get(i));
        }
        return columns;
    }

    public static String[][] getSomeData(String tableName, int size) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select * from " + tableName + " limit 10");
        String[][] data = new String[AMOUNT_TO_GET_INFO_FROM_TABLE][size];
        int i = 0;
        while (resultSet.next()) {
            for (int j = 0; j < size; ++j)
                data[i][j] = resultSet.getString(j + 1);
            ++i;
        }
        return data;
    }

    public static ArrayList<String> getDataFromTable(String query, int len) throws SQLException {
        ResultSet resultSet = statement.executeQuery(query + " limit 1");
        ArrayList<String> out = new ArrayList<>();
        if (resultSet.next()) {
            for (int i = 1; i <= len; ++i) {
                out.add(resultSet.getString(i));
            }
        }
        return out;
    }

    public static ArrayList<String> getAvailableFromColumn(String tableName, String columnName) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select distinct " + columnName + " from " + tableName);
        ArrayList<String> types = new ArrayList<>();
        while (resultSet.next()) {
            types.add(resultSet.getString(1));
        }
        return types;
    }

    public static void close() throws SQLException {
        connection.close();
    }
}
