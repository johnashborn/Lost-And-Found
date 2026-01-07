/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author arant
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;




public class DataBaseConnection {
    // data base nga url
    // jbdc:sql = nag kuan nga mariaDB atua databse
    //localhost = nag run sa local host
    // 3306 = atung port
    // lost and found = atung database name
    private static final String URL ="jdbc:mysql://localhost:3306/LostAndFound";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    //pang connect sa data base
    public static Connection getConnection(){
        Connection connection = null;
        try{
            // i load ang mysql nga driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // pang establish og connection
            connection = DriverManager.getConnection(URL,USER,PASSWORD);
          

        }catch(ClassNotFoundException exception){
            System.out.println("WALA NAKITA ANG DRIVER TAS ANG ERROR NAAS LINE 37");
            exception.printStackTrace();
        }catch(SQLException exception){
            System.out.println("WALA JUY CONNECTION");
            exception.printStackTrace();
        }
        
        return connection;
        
    }
    
    //I test and connection
    public static void main(String[] args) {
        Connection connection = getConnection();
        if(connection !=null){
            System.out.println("NA CONNECT NA!!!!!!");
        }
    }
    
}
