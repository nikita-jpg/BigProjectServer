package com.example.tes;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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
            //deleteTable();
            //createTable();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void createTable() throws SQLException {
        String str = "CREATE TABLE IF NOT EXISTS "+ tableName+"(" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "LOGIN VARCHAR(255) NOT NULL," +
                "PASSWORD VARCHAR(255) NOT NULL," +
                "NOTE_PATH TEXT,"+
                "IMAGE_PATH TEXT,"+
                "UUID TEXT,"+
                "UUID_AES_KEY TEXT)";
        executeUpdate(str);
    }

    //Возвращаем строку вида Публичный ключ:UUID. UUID Зашифрован
    public static synchronized String makePerson(String login,String password){
        String requestCode = "0";

        //Проверяем,занят ли логин
        String check = "SELECT * FROM " + tableName + " WHERE LOGIN = " +"'"+login+"'";
        ResultSet resultSet;
        try {
            resultSet = executeQuery(check);
            resultSet.last();
            if(resultSet.getRow() == 0)
            {
                requestCode = "1"; //логин свободен
            }
            else
            {
                requestCode = "2";//Логин занят
                return requestCode + ":" + "'[]'";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            requestCode = "0";//Сервер не доступен
            return requestCode + ":" + "'[]'";
        }


        //Создаём запись
        String uuidStr = "";
        SecretKey uuidKey = null;
        try
        {
            uuidKey = generateSecretKey();
            UUID uuid = UUID.randomUUID();
            //Делаем заспос к БД о создании человека
            String str = "INSERT INTO "+tableName+" (LOGIN, PASSWORD, UUID, UUID_AES_KEY) \n"
                    +" VALUES ("+"'"+login+"'"+", "+"'"+password+"'"+", "+
                    "'"+uuid.toString()+"'"+", "+"'"+Arrays.toString(uuidKey.getEncoded())+"'"+")";

            executeUpdate(str);

            //Готовим строку для автоавторизации
            uuidStr = encodeUuid(uuidStr,uuidKey);

            return "'" + requestCode + ":"  + uuidStr + "'";

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return "0,'[]'";
    }
    public static synchronized String autPerson(String login,String password){
        String requestCode = "0";
        String check = "SELECT * FROM " + tableName + " WHERE LOGIN = " +"'"+login+"'"
                +" AND PASSWORD = " +"'"+password+"'";
        ResultSet resultSet;
        try {
            resultSet = executeQuery(check);
            resultSet.last();
            if(resultSet.getRow() != 0)
            {
                requestCode = "1"; //Запись найдена
            }
            else
            {
                requestCode = "-1";//Запись не найдена
                return "'" + requestCode + ":" + "[]" + "'";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            requestCode = "0"; //Cервер не доступен
            return "'" + requestCode + ":" + "[]" + "'";
        }


        try {
            //Достаём из базы uuidStr
            String uuidStr;
            String query = "SELECT UUID FROM " + tableName + " WHERE login = " +"'"+login+"'";
            uuidStr = executeQuery(query).getString("uuid");

            //Достаём из базы uuidKey
            String uuidKey;
            String query1 = "SELECT UUID_AES_KEY FROM " + tableName + " WHERE login = " +"'"+login+"'";

            query1 = executeQuery(query1).getString(7);

            //Делаем SecretKey из строки
            String[] byteValues = query1.substring(1, query1.length() - 1).split(",");
            byte[] bytes = new byte[byteValues.length];

            for (int i=0, len=bytes.length; i<len; i++) {
                bytes[i] = java.lang.Byte.parseByte(byteValues[i].trim());
            }
            SecretKey pkToUuid;
            KeyFactory factory = KeyFactory.getInstance("AES");
            pkToUuid =  new SecretKeySpec(bytes, 0, bytes.length, "AES");

            //Шифруем файл для автоавторизации
            uuidStr = encodeUuid(uuidStr,pkToUuid);

            return "'" + requestCode + ":" + uuidStr + "'";
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return "'0:[]'";
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
    private static String decodeUuid(String data,SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
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
