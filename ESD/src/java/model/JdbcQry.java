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
    

    public JdbcQry(Connection con) {
       connection =con;
    }
    public boolean validateLogin(String username, String password) throws ClassNotFoundException, SQLException {

        boolean found = false;

        String query = "SELECT * FROM users";

        try {
            executeQuery(query);
            while (resultSet.next() && found == false) {
                if (username.equals(resultSet.getString("id"))) {
                    if (password.equals(resultSet.getString("password"))) {
                        found = true;
                    }
                }
            }

        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }
        return found;
    }
    public Member getMember(String username) {

        Member member = new Member();
        adminRequests a = new adminRequests();

        String query = "SELECT * FROM members WHERE id = '" + username + "'";

        try {
            a.suspendMember();
            executeQuery(query);
            while (resultSet.next()) {
                member.setId(resultSet.getString("id"));
                member.setName(resultSet.getString("name"));
                member.setAddress(resultSet.getString("address"));
                member.setDob(resultSet.getDate("dob"));
                member.setDor(resultSet.getDate("dor"));
                member.setStatus(resultSet.getString("status"));
                member.setBalance(resultSet.getFloat("balance"));
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }

        return member;
    }
      public boolean checkAdmin(String username) {

        boolean admin = false;
        String query = "SELECT * FROM users WHERE status ='ADMIN'";
        try {
            executeQuery(query);
            while (resultSet.next() && admin == false) {
                if (username.equals(resultSet.getString("id"))) {
                    admin = true;
                } else {
                    admin = false;
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }
        return admin;
    }
       public boolean idExist(String username) {

        boolean exist = false;
        String query = "SELECT * FROM users";
        try {
            executeQuery(query);
            while (resultSet.next()) {
                if (username.equals(resultSet.getString("id"))) {
                    exist = true;
                    break;
                } else {
                    exist = false;
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }

        return exist;
    }
       public void registerMember(Member member, String password) {

        PreparedStatement ps = null;
        java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        try {
            ps = connection.prepareStatement("INSERT INTO members VALUES (?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, member.getId());
            ps.setString(2, member.getName());
            ps.setString(3, member.getAddress());
            ps.setDate(4, member.getDob());
            ps.setDate(5, currentDate);
            ps.setString(6, "APPLIED");
            ps.setDouble(7, member.getBalance());

            ps.executeUpdate();
            ps.close();

            ps = connection.prepareStatement("INSERT INTO users VALUES (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, member.getId());
            ps.setString(2, password);
            ps.setString(3, "APPLIED");

            ps.executeUpdate();
            ps.close();
            adminRequests a = new adminRequests();

            a.updateBalance(member.getId(), 10);
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }
    }
       public ArrayList paymentList(String username) {

        ArrayList paymentList = new ArrayList();
        String query = "SELECT * FROM payments WHERE mem_id ='" + username + "'";

        try {
            executeQuery(query);
            if (!resultSet.isBeforeFirst()) {
                paymentList = new ArrayList();
            } else {
                while (resultSet.next()) {
                    Payment payment = new Payment();
                    payment.setId(resultSet.getInt("id"));
                    payment.setMemID(resultSet.getString("mem_id"));
                    payment.setTypeOfPayment(resultSet.getString("type_of_payment"));
                    payment.setAmount(resultSet.getFloat("amount"));
                    payment.setDate(resultSet.getDate("date"));
                    payment.setStatus(resultSet.getString("status"));
                    paymentList.add(payment);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }

        return paymentList;
    }
       public void makePayment(Payment payment) {

        PreparedStatement ps = null;
        java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        try {
            ps = connection.prepareStatement("INSERT INTO payments(mem_id,type_of_payment,amount,date,status) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, payment.getMemID());
            ps.setString(2, payment.getTypeOfPayment());
            ps.setDouble(3, payment.getAmount());
            ps.setDate(4, currentDate);
            ps.setString(5, "SUBMITTED");

            ps.executeUpdate();
            ps.close();
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }
    }
     
        public boolean claimLimit(String username) {

        boolean limit = false;
        int count = 0;
        String query = "SELECT COUNT(*) FROM claims WHERE mem_id = '" + username + "' AND status <> 'REJECTED' AND date BETWEEN '" + startOfYear + "' AND '" + endOfYear + "'";

        try {
            executeQuery(query);
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            if (count >= 2) {
                limit = true;
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }

        return limit;
    }
        public void submitClaim(Claim claim) {

        PreparedStatement ps = null;
        java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        try {
            ps = connection.prepareStatement("INSERT INTO claims(mem_id,date,rationale,status,amount) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, claim.getMemID());
            ps.setDate(2, currentDate);
            ps.setString(3, claim.getRationale());
            ps.setString(4, "SUBMITTED");
            ps.setDouble(5, claim.getAmount());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
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
