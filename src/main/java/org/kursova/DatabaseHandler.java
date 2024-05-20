package org.kursova;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DatabaseHandler extends Configs {
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort +"/" +dbName + "?" + "autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser , dbPass);

        return dbConnection;
    }

    public void signUpUser(User user) {
        String insert = "INSERT INTO " + Const.USER_TABLE + "(" + Const.USER_FIRSTNAME + "," + Const.USER_LASTNAME + "," + Const.USER_PASSWORD + "," + Const.USER_USERNAME + "," +
                Const.USER_PHONENUMBER + "," + Const.USER_EMAIL + ")" + "VALUES(?,?,?,?,?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1 , user.getFirstname());
            prSt.setString(2 , user.getLastname());
            prSt.setString(3 , user.getPassword());
            prSt.setString(4 , user.getUsername());
            prSt.setString(5 , user.getPhonenumber());
            prSt.setString(6 , user.getEmail());

            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void signUpProduct(Item item) {
        String insert = "INSERT INTO " + Const.PRODUCT_TABLE + "(" + Const.PRODUCT_NUMBER + "," + Const.PRODUCT_NAME + "," + Const.PRODUCT_STATUS + "," + Const.PRODUCT_TYPE + "," + Const.PRODUCT_QUANTITY + "," +
                Const.PRODUCT_PRICE + "," + Const.PRODUCT_DATE + "," + Const.PRODUCT_AUTHOR + ")" + "VALUES(?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1 , item.getProductNum());
            prSt.setString(2 , item.getProductName());
            prSt.setString(3 , item.getStatus());
            prSt.setString(4 , item.getType());
            prSt.setString(5 , item.getQuantity());
            prSt.setString(6 , item.getPrice());
            prSt.setString(7 , item.getDate());
            prSt.setString(8 , item.getAuthor());

            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getUser(User user) {
        ResultSet resultSet = null;
        String query = "SELECT * FROM " + Const.USER_TABLE + " WHERE " +
                Const.USER_USERNAME + "=? AND " + Const.USER_PASSWORD + "=?";

        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());

            resultSet = preparedStatement.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resultSet;
    }



    public ObservableList<Item> getAllProducts() {
        ObservableList<Item> productList = FXCollections.observableArrayList();

        String select = "SELECT * FROM " + Const.PRODUCT_TABLE;

        try (PreparedStatement prSt = getDbConnection().prepareStatement(select);
             ResultSet resultSet = prSt.executeQuery()) {

            while (resultSet.next()) {
                String num = resultSet.getString(Const.PRODUCT_NUMBER);
                String name = resultSet.getString(Const.PRODUCT_NAME);
                String status = resultSet.getString(Const.PRODUCT_STATUS);
                String type = resultSet.getString(Const.PRODUCT_TYPE);
                String quantity = resultSet.getString(Const.PRODUCT_QUANTITY);
                String price = resultSet.getString(Const.PRODUCT_PRICE);
                String date = resultSet.getString(Const.PRODUCT_DATE);
                String author = resultSet.getString(Const.PRODUCT_AUTHOR);

                Item item = new Item(num, name, status, type, quantity, price, date,author);
                productList.add(item);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public int getProductCount() {
        int count = 0;
        String selectCount = "SELECT COUNT(*) FROM " + Const.PRODUCT_TABLE;

        try (PreparedStatement prSt = getDbConnection().prepareStatement(selectCount);
             ResultSet resultSet = prSt.executeQuery()) {
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return count;
    }

    public ObservableList<Item> getProductsByStatus(String status) {
        ObservableList<Item> productList = FXCollections.observableArrayList();

        String select = "SELECT * FROM " + Const.PRODUCT_TABLE + " WHERE " + Const.PRODUCT_STATUS + " = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {
            prSt.setString(1, status);

            try (ResultSet resultSet = prSt.executeQuery()) {
                while (resultSet.next()) {
                    String num = resultSet.getString(Const.PRODUCT_NUMBER);
                    String name = resultSet.getString(Const.PRODUCT_NAME);
                    String type = resultSet.getString(Const.PRODUCT_TYPE);
                    String quantity = resultSet.getString(Const.PRODUCT_QUANTITY);
                    String price = resultSet.getString(Const.PRODUCT_PRICE);
                    String date = resultSet.getString(Const.PRODUCT_DATE);
                    String author = resultSet.getString(Const.PRODUCT_AUTHOR);

                    Item item = new Item(num, name, status, type, quantity, price, date,author);
                    productList.add(item);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productList;
    }

    //-------------------------------------------------------------------------------------------//

    public ObservableList<Item> searchProductByName(String productName) {
        ObservableList<Item> productList = FXCollections.observableArrayList();

        String select = "SELECT * FROM " + Const.PRODUCT_TABLE + " WHERE " + Const.PRODUCT_NAME + " LIKE ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {
            prSt.setString(1, "%" + productName + "%");

            try (ResultSet resultSet = prSt.executeQuery()) {
                while (resultSet.next()) {
                    String num = resultSet.getString(Const.PRODUCT_NUMBER);
                    String name = resultSet.getString(Const.PRODUCT_NAME);
                    String status = resultSet.getString(Const.PRODUCT_STATUS);
                    String type = resultSet.getString(Const.PRODUCT_TYPE);
                    String quantity = resultSet.getString(Const.PRODUCT_QUANTITY);
                    String price = resultSet.getString(Const.PRODUCT_PRICE);
                    String date = resultSet.getString(Const.PRODUCT_DATE);
                    String author = resultSet.getString(Const.PRODUCT_AUTHOR);

                    Item item = new Item(num, name, status, type, quantity, price, date, author);
                    productList.add(item);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public ObservableList<Item> searchProductByType(String productType) {
        ObservableList<Item> productList = FXCollections.observableArrayList();

        String select = "SELECT * FROM " + Const.PRODUCT_TABLE + " WHERE " + Const.PRODUCT_TYPE + " LIKE ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {
            prSt.setString(1, "%" + productType + "%");

            try (ResultSet resultSet = prSt.executeQuery()) {
                while (resultSet.next()) {
                    String num = resultSet.getString(Const.PRODUCT_NUMBER);
                    String name = resultSet.getString(Const.PRODUCT_NAME);
                    String status = resultSet.getString(Const.PRODUCT_STATUS);
                    String type = resultSet.getString(Const.PRODUCT_TYPE);
                    String quantity = resultSet.getString(Const.PRODUCT_QUANTITY);
                    String price = resultSet.getString(Const.PRODUCT_PRICE);
                    String date = resultSet.getString(Const.PRODUCT_DATE);
                    String author = resultSet.getString(Const.PRODUCT_AUTHOR);

                    Item item = new Item(num, name, status, type, quantity, price, date, author);
                    productList.add(item);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public ObservableList<Item> searchProductByQuantity(String productQuantity) {
        ObservableList<Item> productList = FXCollections.observableArrayList();

        String select = "SELECT * FROM " + Const.PRODUCT_TABLE + " WHERE " + Const.PRODUCT_QUANTITY + " LIKE ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {
            prSt.setString(1, "%" + productQuantity + "%");

            try (ResultSet resultSet = prSt.executeQuery()) {
                while (resultSet.next()) {
                    String num = resultSet.getString(Const.PRODUCT_NUMBER);
                    String name = resultSet.getString(Const.PRODUCT_NAME);
                    String status = resultSet.getString(Const.PRODUCT_STATUS);
                    String type = resultSet.getString(Const.PRODUCT_TYPE);
                    String quantity = resultSet.getString(Const.PRODUCT_QUANTITY);
                    String price = resultSet.getString(Const.PRODUCT_PRICE);
                    String date = resultSet.getString(Const.PRODUCT_DATE);
                    String author = resultSet.getString(Const.PRODUCT_AUTHOR);

                    Item item = new Item(num, name, status, type, quantity, price, date, author);
                    productList.add(item);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public ObservableList<Item> searchProductByPrice(String productPrice) {
        ObservableList<Item> productList = FXCollections.observableArrayList();

        String select = "SELECT * FROM " + Const.PRODUCT_TABLE + " WHERE " + Const.PRODUCT_PRICE + " LIKE ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {
            prSt.setString(1, "%" + productPrice + "%");

            try (ResultSet resultSet = prSt.executeQuery()) {
                while (resultSet.next()) {
                    String num = resultSet.getString(Const.PRODUCT_NUMBER);
                    String name = resultSet.getString(Const.PRODUCT_NAME);
                    String status = resultSet.getString(Const.PRODUCT_STATUS);
                    String type = resultSet.getString(Const.PRODUCT_TYPE);
                    String quantity = resultSet.getString(Const.PRODUCT_QUANTITY);
                    String price = resultSet.getString(Const.PRODUCT_PRICE);
                    String date = resultSet.getString(Const.PRODUCT_DATE);
                    String author = resultSet.getString(Const.PRODUCT_AUTHOR);

                    Item item = new Item(num, name, status, type, quantity, price, date, author);
                    productList.add(item);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public ObservableList<Item> searchProductByDate(String productDate) {
        ObservableList<Item> productList = FXCollections.observableArrayList();

        String select = "SELECT * FROM " + Const.PRODUCT_TABLE + " WHERE " + Const.PRODUCT_DATE + " LIKE ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {
            prSt.setString(1, "%" + productDate + "%");

            try (ResultSet resultSet = prSt.executeQuery()) {
                while (resultSet.next()) {
                    String num = resultSet.getString(Const.PRODUCT_NUMBER);
                    String name = resultSet.getString(Const.PRODUCT_NAME);
                    String status = resultSet.getString(Const.PRODUCT_STATUS);
                    String type = resultSet.getString(Const.PRODUCT_TYPE);
                    String quantity = resultSet.getString(Const.PRODUCT_QUANTITY);
                    String price = resultSet.getString(Const.PRODUCT_PRICE);
                    String date = resultSet.getString(Const.PRODUCT_DATE);
                    String author = resultSet.getString(Const.PRODUCT_AUTHOR);

                    Item item = new Item(num, name, status, type, quantity, price, date, author);
                    productList.add(item);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public ObservableList<Item> searchProductByAuthor(String productAuthor) {
        ObservableList<Item> productList = FXCollections.observableArrayList();

        String select = "SELECT * FROM " + Const.PRODUCT_TABLE + " WHERE " + Const.PRODUCT_AUTHOR + " LIKE ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {
            prSt.setString(1, "%" + productAuthor + "%");

            try (ResultSet resultSet = prSt.executeQuery()) {
                while (resultSet.next()) {
                    String num = resultSet.getString(Const.PRODUCT_NUMBER);
                    String name = resultSet.getString(Const.PRODUCT_NAME);
                    String status = resultSet.getString(Const.PRODUCT_STATUS);
                    String type = resultSet.getString(Const.PRODUCT_TYPE);
                    String quantity = resultSet.getString(Const.PRODUCT_QUANTITY);
                    String price = resultSet.getString(Const.PRODUCT_PRICE);
                    String date = resultSet.getString(Const.PRODUCT_DATE);
                    String author = resultSet.getString(Const.PRODUCT_AUTHOR);

                    Item item = new Item(num, name, status, type, quantity, price, date, author);
                    productList.add(item);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productList;
    }

    //---------------------------------------------------------------------------------------------//

    public void updateProduct(Item item) {
        String updateQuery = "UPDATE " + Const.PRODUCT_TABLE + " SET " + Const.PRODUCT_NAME + " = ?, " +
                Const.PRODUCT_TYPE + " = ?, " + Const.PRODUCT_QUANTITY + " = ?, " + Const.PRODUCT_PRICE + " = ?, " +
                Const.PRODUCT_AUTHOR + " = ?, " + Const.PRODUCT_STATUS + " = ? WHERE " + Const.PRODUCT_NUMBER + " = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, item.getProductName());
            preparedStatement.setString(2, item.getType());
            preparedStatement.setString(3, item.getQuantity());
            preparedStatement.setString(4, item.getPrice());
            preparedStatement.setString(5, item.getAuthor());
            preparedStatement.setString(6, item.getStatus());
            preparedStatement.setString(7, item.getProductNum());

            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //---------------------------------------------------------------------------------------------//
    public void updateUser(User user) {
        String query = "UPDATE " + Const.USER_TABLE + " SET " +
                Const.USER_FIRSTNAME + "=?, " +
                Const.USER_LASTNAME + "=?, " +
                Const.USER_PASSWORD + "=?, " +
                Const.USER_PHONENUMBER + "=?, " +
                Const.USER_EMAIL + "=? WHERE " +
                Const.USER_USERNAME + "=?";

        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getPhonenumber());
            preparedStatement.setString(5, user.getEmail());
            preparedStatement.setString(6, user.getUsername());

            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //---------------------------------------------------------------------------------------------//
    public void deleteItem(Item item) {
        String deleteQuery = "DELETE FROM " + Const.PRODUCT_TABLE + " WHERE " + Const.PRODUCT_NUMBER + " = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, item.getProductNum());

            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}



