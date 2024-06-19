package com.github.bobryanskiy.smartdb;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FileRenamer {

    private static ArrayList<String> getFilenameFromMask(String mask, Set<String> columnNames) {
        ArrayList<String> needed = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(mask);
        while (matcher.find()) {
            String replacement = matcher.group(1);
            if (columnNames.contains(replacement)) {
                needed.add(replacement);
            }
        }
        return needed;
    }

    public static String fileRenamer(String folderPath, Controller.Request[] requests, String mask) throws SQLException, FileNotFoundException {
        StringBuilder requestColumnsToDatabase = new StringBuilder("SELECT ");
        ArrayList<String> toGet = getFilenameFromMask(mask, Controller.columns.keySet());
        if (!Controller.containsDependenciesFromDb && !toGet.isEmpty()) {
            return "Нет зависимостей от конфигурации в маске";
        }
        boolean isFirstColumn = true;
        for (String string : toGet) {
            if (!isFirstColumn) {
                requestColumnsToDatabase.append(", ");
            } else isFirstColumn = false;
            requestColumnsToDatabase.append(string);
        }

        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) {
            return "В этой папке нет .txt файлов";
        }
        Scanner scanner;
        boolean isOk;
        int changedAmount = 0;
        for (File file : files) {
            StringBuilder searchString = new StringBuilder(requestColumnsToDatabase);
            isOk = true;
            StringBuilder requestWhereToDatabase = new StringBuilder(" WHERE ");
            isFirstColumn = true;
            scanner = new Scanner(file);
            String word;
            fCheck:  for (Controller.Request request : requests) {
                if (scanner.hasNext()) word = scanner.next();
                else word = null;
                if (word == null) break;
                switch (request.type()) {
                    case Word -> {
                        String[] words = request.word().split(" ");
                        for (String s : words) {
                            if (!s.equals(word)) {
                                isOk = false;
                                break fCheck;
                            }
                            if (scanner.hasNext()) word = scanner.next();
                            else word = null;
                        }
                    }
                    case Column -> {
                        if (!isFirstColumn) {
                            requestWhereToDatabase.append(" AND ");
                        } else isFirstColumn = false;
                        requestWhereToDatabase.append(request.column()).append(" = '").append(word).append("'");
                    }
                    case ColumnWithSpecification -> {
                        if (!request.word().equals(word)) {
                            isOk = false;
                            break fCheck;
                        }
                        if (!isFirstColumn) {
                            requestWhereToDatabase.append(" AND ");
                        } else isFirstColumn = false;
                        requestWhereToDatabase.append(request.column()).append(" = '").append(word).append("'");
                    }
                }
            }
            if (isOk) {
                String newFileName = mask;
                newFileName = newFileName.replace("[filename]", file.getName().replace(".txt", ""));
                if (!requestWhereToDatabase.toString().equals(" WHERE ")) {
                    ArrayList<String> arrayList = DataBase.getDataFromTable(String.valueOf(searchString.append(" FROM ").append(Controller.tables.get(0)).append(requestWhereToDatabase)), toGet.size());
                    if (!arrayList.isEmpty()) {
                        for (int i = 0; i < toGet.size(); ++i) {
                            newFileName = newFileName.replace("[" + toGet.get(i) + "]", arrayList.get(i));
                        }
                    } else continue;
                }
                File toRename = new File(file.getParentFile(), newFileName);
                scanner.close();
                if (file.renameTo(toRename))
                    ++changedAmount;
            }
        }
        return "Успешно изменено " + changedAmount + " файлов";
    }
}
