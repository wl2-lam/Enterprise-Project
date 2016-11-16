/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regular_class;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import static java.sql.Types.NULL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author me-aydin
 */
public class JdbcUserQry {
    
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    //String query = null;
    
    
    public JdbcUserQry(String query){
        //this.query = query;
    }

    public JdbcUserQry() {
        //this.query = "";
    }
    
    public void connect(Connection con){
       connection = con;
    }
    
    private ArrayList rsToList() throws SQLException {
        ArrayList aList = new ArrayList();

        int cols = rs.getMetaData().getColumnCount();
        while (rs.next()) { 
          String[] s = new String[cols];
          for (int i = 1; i <= cols; i++) {
            s[i-1] = rs.getString(i);
          } 
          aList.add(s);
        } // while    
        return aList;
    } //rsToList
 
    private String makeHtmlTable(ArrayList list) {
        StringBuilder b = new StringBuilder();
        String[] row;
        b.append("<table border=\"3\">");
        for (Object s : list) {
          b.append("<tr>");
          row = (String[]) s;
            for (String row1 : row) {
                b.append("<td>");
                b.append(row1);
                b.append("</td>");
            }
          b.append("</tr>\n");
        } // for
        b.append("</table>");
        return b.toString();
    }//makeHtmlTable
    
    private String makeTable(ArrayList list) {
        StringBuilder b = new StringBuilder();
        String[] row;
        b.append(String.format("%-12s %-12s\n","Username","Password"));
        b.append("================================");
        for (Object s : list) {
          b.append("\n");
          row = (String[]) s;
            for (String row1 : row) {
                //b.append("\t");
                b.append(String.format("%-12s",row1));
                //b.append("\t");
            }//for
         // b.append("\n");
        } // for
        b.append("\n");
        b.append("================================");
        return b.toString();
    }//makeTable
  
    private void select(String query){
        //Statement statement = null;
        
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            //statement.close();
        }
        catch(SQLException e) {
            System.out.println("way way"+e);
            //results = e.toString();
        }
    }
    public String retrieve(String query) throws SQLException {
        String results="";
        select(query);

        return makeTable(rsToList());//results;
    }
    
    public boolean exists(String user) {
        boolean bool = false;
        try  {
            select("select username from users where username='"+user+"'");
            if(rs.next()) {
                System.out.println(user+" exits in the DB");         
                bool = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JdbcUserQry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bool;
    }
    public void insert(String[] str){
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("INSERT INTO users VALUES (?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, str[0].trim()); 
            ps.setString(2, str[1]);
            ps.executeUpdate();
        
            ps.close();
            System.out.println("1 row added.");
        } catch (SQLException ex) {
            Logger.getLogger(JdbcUserQry.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
    public void update(String[] str) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("Update Users Set password=? where username=?",PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, str[1].trim()); 
            ps.setString(2, str[0].trim());
            ps.executeUpdate();
        
            ps.close();
            System.out.println("1 rows updated.");
        } catch (SQLException ex) {
            Logger.getLogger(JdbcUserQry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void delete(String user){
       
      String query = "DELETE FROM users " +
                   "WHERE username = '"+user.trim()+"'";
      
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        }
        catch(SQLException e) {
            System.out.println("way way"+e);
            //results = e.toString();
        }
    }
    public void closeAll(){
        try {
            rs.close();
            statement.close(); 		
            //connection.close();                                         
        }
        catch(SQLException e) {
            System.out.println(e);
        }
    }
//    public static void main(String[] args) throws SQLException {
//        String str = "select * from users";
//        String insert = "INSERT INTO `users` (`username`, `password`) VALUES ('meaydin', 'meaydin')";
//        String update = "UPDATE `users` SET `password`='eaydin' WHERE `username`='eaydin' ";
//        String db = "MyUse";
//        
//        JdbcUserQry jdbc = new JdbcUserQry(str);
//        Connection conn = null;
//                try {
//            Class.forName("com.mysql.jdbc.Driver");
//            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+db.trim(), "root", "");
//        }
//        catch(ClassNotFoundException | SQLException e){
//            
//        }
//        jdbc.connect(conn);
//        String [] users = {"birgul12","han","han"};
//        System.out.println(jdbc.retrieve(str));
//        if (!jdbc.exists(users[0]))
//            jdbc.insert(users);            
//        else {
//                jdbc.update(users);
//                System.out.println("user name exists, change to another");
//        }
//        String uToDel = "aydinme";
//        if (jdbc.exists(uToDel)) {
//            jdbc.delete(uToDel);
//            System.out.println(jdbc.retrieve(str));
//        }
//        else
//            System.out.println(uToDel+" does not exit in the DB");
//        
//        jdbc.closeAll();
//    }            
}