package com.example.tes;
import javax.crypto.*;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;


public class LocalBase implements Closeable {
    public static final String DB_URL = "jdbc:h2:/c:/JavaPrj/SQLDemo/db/stockExchange";
    public static final String DB_Driver = "org.h2.Driver";

    private static Connection connection;  // JDBC-соединение для работы с таблицей
    private static final String tableName = "my_table_firsts";


    LocalBase() {
        try {
            Class.forName(DB_Driver);
            Connection connection = DriverManager.getConnection(DB_URL);//соединениесБД
            connection.close();// отключение от БД
            deleteTable();
            createTable();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void createTable() throws SQLException {
        String str = "CREATE TABLE IF NOT EXISTS "+ tableName+"(" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "login VARCHAR(255) NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "publicKeyPerson TEXT," +
                "publicKey TEXT," +
                "privateKey  TEXT,"+
                "zametkaPuth TEXT,"+
                "imagePuth TEXT,"+
                "uuid TEXT,"+
                "uuidAesKey TEXT)";
        executeUpdate(str);
    }

    //Возвращаем строку вида Публичный ключ:UUID. UUID Зашифрован
    public static synchronized String makePerson(String login,String password,String pkKey) throws NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        String pkKeyStr = "";
        String uuidStr = "";
        KeyPair keyPair = generateKeys();
        SecretKey uuidKey = generateSecretKey();
        UUID uuid = UUID.randomUUID();
        //Делаем заспос к БД о создании человека
        String str = "INSERT INTO "+tableName+" (login, password, publicKeyPerson, publicKey, privateKey, uuid, uuidAesKey) \n"
                +" VALUES ("+"'"+login+"'"+", "+"'"+password+"'"+", "+"'"+pkKey+"'"+", "
                +"'"+ Arrays.toString(keyPair.getPublic().getEncoded())+"'"+", " +"'"+Arrays.toString(keyPair.getPrivate().getEncoded())+"'"+","+
                "'"+uuid.toString()+"'"+", "+"'"+uuidKey.toString()+"'"+")";
        try {
            executeUpdate(str);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return "";
        }

        //Готовим возврат
        pkKeyStr = Arrays.toString(keyPair.getPublic().getEncoded());
        uuidStr = encodeUuid(uuidStr,uuidKey);

        return pkKey + ":" + uuidStr;

    }
    public static synchronized String getPublKey(String login) throws SQLException {
        String str = "SELECT "+"publicKey "
                +"FROM " + tableName
                +" WHERE " + "login" + "="+"'"+login+"'";
        ResultSet resultSet = executeQuery(str);
        String request = null;
        if(resultSet.next()) request = resultSet.getString("publicKey");
        resultSet.close();
        return request;
    }


    private static ResultSet executeQuery(String sql) throws SQLException {
        ResultSet resultSet;

        reopenConnection(); // переоткрываем (если оно неактивно) соединение с СУБД
        Statement statement = connection.createStatement();  // Создаем statement для выполнения sql-команд
        resultSet = statement.executeQuery(sql); // Выполняем statement - sql команду
        return resultSet;
    };
    private static int executeUpdate(String sql) throws SQLException {
        int resultSet;

        reopenConnection(); // переоткрываем (если оно неактивно) соединение с СУБД
        Statement statement = connection.createStatement();  // Создаем statement для выполнения sql-команд
        resultSet = statement.executeUpdate(sql); // Выполняем statement - sql команду
        statement.close();      // Закрываем statement для фиксации изменений в СУБД
        connection.close();
        return resultSet;
    };



    private static String encodeUuid(String data,SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        return Arrays.toString(cipher.doFinal(data.getBytes()));

    }
    private static SecretKey generateSecretKey() throws NoSuchPaddingException, NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey;
    }
    private static KeyPair generateKeys(){
        KeyPair keyPair = null;
        try {
            keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return keyPair;
    }
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    private static void reopenConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = getConnection();
        }
    }
    // Получить новое соединение с БД
    @Override
    public void close() throws IOException {

    }
    private void deleteTable() throws SQLException {
        String str = "DROP TABLE "+tableName;
        executeUpdate(str);
    }
}
