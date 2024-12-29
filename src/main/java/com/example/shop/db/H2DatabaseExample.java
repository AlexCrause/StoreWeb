//package com.example.shop.db;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class H2DatabaseExample {
//    public static void main(String[] args) {
//        String url = "jdbc:h2:./data/testdb"; // Путь к файлу базы данных в папке data
//        String user = "sa"; // Пользователь по умолчанию для H2
//        String password = ""; // Пустой пароль по умолчанию
//
//        try (Connection connection = DriverManager.getConnection(url, user, password)) {
//            System.out.println("Подключение успешно!");
//
//            // Получаем список всех таблиц в базе данных
//            String tablesQuery = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'TABLE'";
//            try (PreparedStatement stmt = connection.prepareStatement(tablesQuery);
//                 ResultSet rs = stmt.executeQuery()) {
//
//                System.out.println("Существующие таблицы в базе данных:");
//                while (rs.next()) {
//                    String tableName = rs.getString("TABLE_NAME");
//                    System.out.println(tableName); // Выводим имена всех таблиц
//
//                    // Получаем данные из каждой таблицы
//                    String dataQuery = "SELECT * FROM " + tableName;
//                    try (PreparedStatement dataStmt = connection.prepareStatement(dataQuery);
//                         ResultSet dataRs = dataStmt.executeQuery()) {
//
//                        System.out.println("Данные из таблицы " + tableName + ":");
//                        while (dataRs.next()) {
//                            int columnCount = dataRs.getMetaData().getColumnCount();
//                            for (int i = 1; i <= columnCount; i++) {
//                                System.out.print(dataRs.getString(i) + "\t");
//                            }
//                            System.out.println();
//                        }
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
