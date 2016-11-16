/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.*;
import static java.sql.Types.NULL;
import java.util.*;


/**
 *
 * @author Owen Harvey
 */
public class JdbcQry {
    
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    String db = "MyUse";
    
    public JdbcQry(String db){
        this.db = db;
    }
    
    public void connect(){
    Connection conn = null;
                try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+db.trim(), "root", "");
        }
        catch(ClassNotFoundException | SQLException e){
            
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
    
    public String getTable() throws SQLException{
        return makeHtmlTable(rsToList());
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
}
