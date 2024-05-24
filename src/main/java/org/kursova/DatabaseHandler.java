package org.kursova;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DatabaseHandler extends Configs {
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort +"/" +dbName + "?" + "autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser , dbPass);

        return dbConnection;
    }

    //VVVVVV------------Controller_control.java------------------Controller_registration.java------------------------------//
    public void signUpUser(User user) {
        String insert = "INSERT INTO " + Const.USER_TABLE + "(" + Const.USER_FIRSTNAME + "," + Const.USER_LASTNAME + "," + Const.USER_PASSWORD + "," + Const.USER_USERNAME + "," +
                Const.USER_PHONENUMBER + "," + Const.USER_EMAIL + "," + Const.USER_ROLE + ")" + "VALUES(?,?,?,?,?,?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1 , user.getFirstname());
            prSt.setString(2 , user.getLastname());
            prSt.setString(3 , user.getPassword());
            prSt.setString(4 , user.getUsername());
            prSt.setString(5 , user.getPhonenumber());
            prSt.setString(6 , user.getEmail());
            prSt.setString(7 , user.getRole());

            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //VVVVVV----------------------------Controller_add.java-----------------------------------------------//
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
    //VVVVVV--------Controller_control.java--------------------------Controller_profile.java-------------------------------//
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
    //VVVVVV------------------------Controller_home.java-------------------------------------------------//
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
    //VVVVVV-----------------------------Controller_add.java-------------------------------------------//
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
    //VVVVVV------------------------------Controller_home.java------------------------------------------//
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
    //VVVVVV------------------------------Controller_home.java------------------------------------------//
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
    //VVVVVV------------------------------Controller_home.java------------------------------------------//
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
    //VVVVVV------------------------------Controller_home.java------------------------------------------//
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
    //VVVVVV------------------------------Controller_home.java------------------------------------------//
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
    //VVVVVV------------------------------Controller_home.java------------------------------------------//
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
    //VVVVVV------------------------------Controller_home.java------------------------------------------//
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
    //VVVVVV------------------------------Controller_edit.java----------------------------------------//
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
    //VVVVVV-------------------------------Controller_profile.java---------------------------------------------//
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
    //VVVVVV-------------------------------Controller_home.java--------------------------------------//
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
    //VVVVVV------------------------------Controller_control.java----------------------------------//
    public ObservableList<User> getAllUsers() {
        ObservableList<User> userList = FXCollections.observableArrayList();
        String query = "SELECT * FROM " + Const.USER_TABLE;

        try (Connection connection = getDbConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                User user = new User();
                user.setFirstname(resultSet.getString(Const.USER_FIRSTNAME));
                user.setLastname(resultSet.getString(Const.USER_LASTNAME));
                user.setPassword(resultSet.getString(Const.USER_PASSWORD));
                user.setUsername(resultSet.getString(Const.USER_USERNAME));
                user.setPhonenumber(resultSet.getString(Const.USER_PHONENUMBER));
                user.setEmail(resultSet.getString(Const.USER_EMAIL));
                user.setRole(resultSet.getString(Const.USER_ROLE));

                userList.add(user);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return userList;
    }
    //VVVVVV-------------------------------Controller_control.java--------------------------------------//
    public void deleteUser(String username) {
        String query = "DELETE FROM " + Const.USER_TABLE + " WHERE " + Const.USER_USERNAME + " = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //VVVVVV---------------------------------Controller_control.java------------------------------------//
    public void updateUserRole(String username, String newRole) {
        String updateQuery = "UPDATE " + Const.USER_TABLE + " SET " +
                Const.USER_ROLE + " = ? WHERE " +
                Const.USER_USERNAME + " = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newRole);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //VVVVVV-------------------------------Controller_control.java----------------------------------------//
    public ObservableList<User> searchUserByFirstName(String firstName) {
        ObservableList<User> userList = FXCollections.observableArrayList();
        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " + Const.USER_FIRSTNAME + " LIKE ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {
            prSt.setString(1, "%" + firstName + "%");

            try (ResultSet resultSet = prSt.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setFirstname(resultSet.getString(Const.USER_FIRSTNAME));
                    user.setLastname(resultSet.getString(Const.USER_LASTNAME));
                    user.setPassword(resultSet.getString(Const.USER_PASSWORD));
                    user.setUsername(resultSet.getString(Const.USER_USERNAME));
                    user.setPhonenumber(resultSet.getString(Const.USER_PHONENUMBER));
                    user.setEmail(resultSet.getString(Const.USER_EMAIL));
                    user.setRole(resultSet.getString(Const.USER_ROLE));

                    userList.add(user);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userList;
    }
    //VVVVVV---------------------------------Controller_control.java---------------------------------------//
    public ObservableList<User> searchUserByUsername(String username) {
        ObservableList<User> userList = FXCollections.observableArrayList();
        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " + Const.USER_USERNAME + " LIKE ?";

        try (Connection connection = getDbConnection();
             PreparedStatement prSt = connection.prepareStatement(select)) {
            prSt.setString(1, "%" + username + "%");

            try (ResultSet resultSet = prSt.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setFirstname(resultSet.getString(Const.USER_FIRSTNAME));
                    user.setLastname(resultSet.getString(Const.USER_LASTNAME));
                    user.setPassword(resultSet.getString(Const.USER_PASSWORD));
                    user.setUsername(resultSet.getString(Const.USER_USERNAME));
                    user.setPhonenumber(resultSet.getString(Const.USER_PHONENUMBER));
                    user.setEmail(resultSet.getString(Const.USER_EMAIL));
                    user.setRole(resultSet.getString(Const.USER_ROLE));

                    userList.add(user);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userList;
    }
    //VVVVVV------------Controller_control.java-------------------Controller_registration.java-------------------//
    public boolean userExists(String username) {
        String query = "SELECT COUNT(*) FROM " + Const.USER_TABLE + " WHERE " + Const.USER_USERNAME + " = ?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(query);
            prSt.setString(1, username);
            ResultSet rs = prSt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    //VVVVVV---------------------------------Controller_control.java------------------------------------//
    public String getUserRoleByUsername(String username) {
        String role = "";
        String query = "SELECT " + Const.USER_ROLE + " FROM " + Const.USER_TABLE + " WHERE " + Const.USER_USERNAME + " = ?";

        try (Connection connection = getDbConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    role = resultSet.getString(Const.USER_ROLE);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return role;
    }
    //VVVVVV--------------------------------Controller_registration.java---------------------------------------//
    public boolean isFirstUser() {
        String query = "SELECT COUNT(*) FROM " + Const.USER_TABLE;
        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}