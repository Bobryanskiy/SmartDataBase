package com.github.bobryanskiy.smartdb;

import java.sql.*;
import java.util.ArrayList;

public class DataBase {
    static Connection connection;
    static Statement statement;
    static String dbName;

    public static void connect(String user, String password) throws SQLException {
        user = password = "bobr";
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

    public static Object[] getColumns(String tableName) throws SQLException {
        ArrayList<String> columns = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("show columns from " + tableName);
        while (resultSet.next()) {
            columns.add(resultSet.getString(1));
        }
        resultSet = statement.executeQuery("SELECT DATA_TYPE FROM information_schema.columns WHERE table_name = "
                + "'" + tableName + "'" + "order by table_name, ordinal_position");
        String[] types = new String[columns.size()];
        int i = 0;
        while (resultSet.next()) {
            types[i++] = resultSet.getString(1);
        }
        return new Object[]{columns, types};
    }

    public static String[][] getSomeData(String tableName, int size) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select * from " + tableName + " limit 10");
        String[][] data = new String[10][size];
        int i = 0;
        while (resultSet.next()) {
            for (int j = 0; j < size; ++j)
                data[i][j] = resultSet.getString(j + 1);
            ++i;
        }
        return data;
    }

    public static void close() throws SQLException {
        connection.close();
    }
}
