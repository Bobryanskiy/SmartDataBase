package com.github.bobryanskiy.smartdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

public class FileRenamer {

    static String[] mask_open(String mask, String[] columnNames)
    {
        String[] needed = new String[0];
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(mask);
        while (matcher.find()) {
            String replacement = matcher.group(1);
            if (replacement.equals("filename")) {
                continue;
            } else {
                for (int i = 0; i < columnNames.length; i++) {
                    if (replacement.equals(columnNames[i])) {
                        needed = Arrays.copyOf(needed, needed.length + 1);
                        needed[needed.length - 1] = columnNames[i];
                    }
                }
            }
        }
        return needed;
    }

    static void FileRenamer(String folderPath, String[] columnNames, String[] userQueryColumns, String mask, Connection connection)
    {

        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        for (File file : files) {
            try {
                // Чтение первой строки файла
                List<String> lines = Files.readAllLines(Path.of(file.getPath()));
                String[] firstLineData = lines.get(0).split(",");

                String[] needed = mask_open(mask, columnNames);

                // Создание запроса для поиска
                StringBuilder query = new StringBuilder("SELECT ");
                for (int i = 0; i < needed.length; i++) {
                    query.append(needed[i]);
                    if (i < needed.length - 1) {
                        query.append(", ");
                    }
                }
                query.append(" FROM table_name WHERE ");
                for (int i = 0; i < userQueryColumns.length; i++) {
                    query.append(userQueryColumns[i]).append(" = '").append(firstLineData[i]).append("'");
                    if (i < userQueryColumns.length - 1) {
                        query.append(" AND ");
                    }
                }

                // Выполнение запроса
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query.toString());


                String[] data = new String[columnNames.length];
                while (resultSet.next()) {
                    for (int i = 0; i < columnNames.length; i++) {
                        data[i] = resultSet.getString(columnNames[i]);
                    }
                }


                String newFileName = mask;
                Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                Matcher matcher = pattern.matcher(newFileName);
                while (matcher.find()) {
                    String replacement = matcher.group(1);
                    if (replacement.equals("filename")) {
                        newFileName = newFileName.replace("[" + replacement + "]", file.getName());
                    } else
                    {
                        for (int i = 0; i < columnNames.length; i++) {
                            if (replacement.equals(columnNames[i])) {
                                newFileName = newFileName.replace("[" + replacement + "]", data[i]);
                            }
                        }
                    }
                }
                file.renameTo(new File(folderPath + "\\" + newFileName));
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }

        // Закрытие соединения с базой данных
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {


        String folderPath = "C:\\path\\to\\folder";


        String[] columnNames = {"name", "age", "city"};


        String[] userQueryColumns = {"name", "age"};


        String mask = "[name]_[filename]";


        String connectionUrl = "jdbc:mysql://localhost:3306/database_name";
        String username = "username";
        String password = "password";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(connectionUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        FileRenamer(folderPath, columnNames, userQueryColumns, mask, connection);
    }
}
