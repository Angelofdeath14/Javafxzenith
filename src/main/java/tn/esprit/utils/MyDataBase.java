package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private String URL="jdbc:mysql://localhost:3306/artyphoria";
    private String USERNAME="root";
    private String PASSWORD="";
    private Connection con;
    private static MyDataBase instance;
    private MyDataBase() {
        try {
            con= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static MyDataBase getInstance(){
        if(instance==null){
            instance=new MyDataBase();
        }else{
            System.out.println("Already connected");
        }
        return instance;
    }

    public Connection getCon() {
        return con;
    }
}
