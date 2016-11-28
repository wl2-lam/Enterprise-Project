/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Owen Harvey
 */
public class JdbcQry {
    
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    String db = "MyUse";
    ResultSet resultSet;
    LocalDate now = LocalDate.now();
    String startOfYear = now.with(TemporalAdjusters.firstDayOfYear()).toString();
    String endOfYear = now.with(TemporalAdjusters.lastDayOfYear()).toString();
    String today = now.toString();
    
//    public JdbcQry(String db){
//        this.db = db;
//    }
   
    
    public void connect() throws SQLException{
    Connection conn = null;
                try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+db.trim(), "root", "");
        }
        catch(SQLException e){
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JdbcQry.class.getName()).log(Level.SEVERE, null, ex);
        }
        connection = conn;
    }
    
    public void executeQuery(String query){
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            //statement.close();
        }
        catch(SQLException e) {
            System.out.println("way way"+e);
        }
    }
    

    public JdbcQry(Connection con) {
       connection =con;
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
    
   
    
    public ArrayList getList() throws SQLException{
        return rsToList();
    }
    
    private ArrayList rsToList() throws SQLException{
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
    }
    
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
    }

    String getTable() throws SQLException {
        return makeHtmlTable(rsToList());//To change body of generated methods, choose Tools | Templates.
    }
}
