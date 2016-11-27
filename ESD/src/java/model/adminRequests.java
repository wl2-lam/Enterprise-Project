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
//TODO: filtering/searching of tables
public class adminRequests {
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    ResultSet resultSet;
    LocalDate now = LocalDate.now();
    String startOfYear = now.with(TemporalAdjusters.firstDayOfYear()).toString();
    String endOfYear = now.with(TemporalAdjusters.lastDayOfYear()).toString();
    String today = now.toString();
    
    static JdbcQry jd;
    //change the status field in members table
    public static void updateMemberStatus(String mem_id, String status){
        jd.executeQuery("Update Members Set status="+status+" where mem_id="+mem_id);
    }
    
    //change the status and subsidy field in claims table
    public static void updateClaimStatus(String claim_id, String status){        
        jd.executeQuery("Update Claims Set status="+status+" where claim_id="+claim_id);
    }
    
    //retrieve the claims table for viewing
    public static String getClaimsTable(){
        
        jd.executeQuery("Select * from Claims");        
        
        try{
        return jd.getTable();
        }catch(SQLException e){
            return null;
        }
    }
    
    //retrieve the members table for viewing
    public static String getMembersTable(){
        
        jd.executeQuery("Select * from Members");        
        
        try{
        return jd.getTable();
        }catch(SQLException e){
            return null;
        }}
    
    //calculate and return turnover of last financial year
    //TODO: edit to use date to select only last years
    public static String calculateTurnover(){
        
        int total = 0;
        java.util.ArrayList al;
        
        jd.executeQuery("Select amount from Payments");        
        
        try{
        al = jd.getList();
        }
        catch(SQLException e){
            return "NaN";
        }
        
        for(Object s : al){
            String value = ((String[])s)[0];
            total+=Integer.valueOf(value);
        }
                
        jd.executeQuery("Select subsidy from Claims");        
        
        try{
        al = jd.getList();
        }
        catch(SQLException e){
            return "NaN";
        }
        
        for(Object s : al){
            String value = ((String[])s)[0];
            total-=Integer.valueOf(value);
        }               
        
        return ""+total;
    }
     
    public void executeQuery(String query) throws SQLException{
        statement = connection.createStatement();
        rs = statement.executeQuery(query);
        //statement.close();
    }
 public ArrayList memberList() {

        ArrayList memberList = new ArrayList();
        String query = "SELECT * FROM members";

        try {
            executeQuery(query);
            if (!resultSet.isBeforeFirst()) {
                memberList = new ArrayList();
            } else {
                while (resultSet.next()) {
                    Member member = new Member();
                    member.setId(resultSet.getString("id"));
                    member.setName(resultSet.getString("name"));
                    member.setAddress(resultSet.getString("address"));
                    member.setDob(resultSet.getDate("dob"));
                    member.setDor(resultSet.getDate("dor"));
                    member.setStatus(resultSet.getString("status"));
                    member.setBalance(resultSet.getFloat("balance"));
                    memberList.add(member);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
     
        
    }  
        return memberList;
 }

        public Member getMember(String criteria, String value) {

        Member member = new Member();
        String query = "SELECT * FROM members WHERE " + criteria + " = '" + value + "'";

        try {
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
         public ArrayList balanceList() {

        ArrayList balanceList = new ArrayList();
        String query = "SELECT * FROM members WHERE id NOT LIKE 'ADMIN' AND balance>0.00";

        try {
            executeQuery(query);
            if (!resultSet.isBeforeFirst()) {
                balanceList = new ArrayList();
            } else {
                while (resultSet.next()) {
                    Member member = new Member();
                    member.setId(resultSet.getString("id"));
                    member.setName(resultSet.getString("name"));
                    member.setAddress(resultSet.getString("address"));
                    member.setDob(resultSet.getDate("dob"));
                    member.setDor(resultSet.getDate("dor"));
                    member.setStatus(resultSet.getString("status"));
                    member.setBalance(resultSet.getFloat("balance"));
                    balanceList.add(member);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }

        return balanceList;
    }
          public ArrayList claimList(String username) {

        ArrayList claimList = new ArrayList();
        if (username.equals("")) {
            username = "%";
        }
        String query = "SELECT * FROM claims WHERE mem_id LIKE'" + username + "'";

        try {
            executeQuery(query);
            if (!resultSet.isBeforeFirst()) {
                claimList = new ArrayList();
            } else {
                while (resultSet.next()) {
                    Claim claim = new Claim();

                    claim.setAmount(resultSet.getFloat("amount"));
                    claim.setDate(resultSet.getDate("date"));
                    claim.setId(resultSet.getInt("id"));
                    claim.setMemID(resultSet.getString("mem_id"));
                    claim.setRationale(resultSet.getString("rationale"));
                    claim.setStatus(resultSet.getString("status"));
                    claimList.add(claim);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }

        return claimList;
    }
           public boolean processClaim(int id, String status) {

        boolean updated = false;
        PreparedStatement ps = null;
        String query = "SELECT id FROM claims WHERE status='SUBMITTED'";
        String update = "UPDATE claims SET status='" + status + "' WHERE id =" + id;

        try {
            executeQuery(query);
            while (resultSet.next() && updated == false) {
                if (id == resultSet.getInt(1)) {
                    ps = connection.prepareStatement(update);
                    ps.executeUpdate();
                    ps.close();
                    updated = true;
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }

        return updated;
    }
          
           public boolean processPayment(int id) {

        boolean updated = false;
        String update = "UPDATE payments SET status='APPROVED' WHERE id=" + id;
        String query = "SELECT * FROM payments WHERE status='SUBMITTED'";
        String username;
        String type;
        double amount = 0;
        PreparedStatement ps = null;

        try {
            executeQuery(query);
            while (resultSet.next() && updated == false) {
                if (id == resultSet.getInt("id")) {
                    username = resultSet.getString("mem_id");
                    type = resultSet.getString("tyoe_of_payment");
                    amount = resultSet.getDouble("amount");

                    ps = connection.prepareStatement(update);
                    ps.executeUpdate();
                    ps.close();

                    switch (type) {
                        case "MEMBER":
                            updateStatus(username, "APPROVED");
                            break;
                        case "CLAIM":
                            amount = 0 - amount;
                            updateBalance(username, amount);
                            break;
                    }
                    updated = true;
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }

        return updated;
    }
           public void claimFee() {

        PreparedStatement ps = null;
        double fee = calcClaimFee();
        String query = "UPDATE members SET balance = (balance+" + fee + ")";

        if (today.equals(endOfYear)) {
            try {
                ps = connection.prepareStatement(query);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException s) {
                System.out.println("SQL statement is not executed!");
            }
        }
    }
           public double calcTurnover() {

        double turnover = 0.0;
        String queryIncome = "SELECT SUM(amount) FROM payments WHERE status='APPROVED' AND date BETWEEN '" + startOfYear + "' AND '" + endOfYear + "'";
        String queryExpense = "SELECT SUM(amount) FROM claims WHERE status='APPROVED' AND date BETWEEN '" + startOfYear + "' AND '" + endOfYear + "'";
        double income = 0.0;
        double expense = 0.0;

        try {
            executeQuery(queryIncome);
            while (resultSet.next()) {
                income = resultSet.getFloat(1);
            }
            executeQuery(queryExpense);
            while (resultSet.next()) {
                expense = resultSet.getFloat(1);
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }

        turnover = income - expense;

        return turnover;
    }//method

    protected void suspendMember() {

        String query = "SELECT id FROM members WHERE status='APPROVED' AND dor <='" + today + "'";

        try {
            executeQuery(query);
            while (resultSet.next()) {
                String username = resultSet.getString(1);
                updateStatus(username, "SUSPENDED");
                updateBalance(username, 10);
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }
    }
    protected void updateStatus(String username, String status) {

        PreparedStatement ps = null;
        String queryApprove = "UPDATE members SET status ='APPROVED', dor =DATE_ADD(dor, INTERVAL 1 YEAR) WHERE id='" + username + "'";
        String querySuspend = "UPDATE members SET status ='SUSPENDED' WHERE id='" + username + "'";
        String queryUser = "UPDATE users SET status ='" + status + "' WHERE id='" + username + "'";

        try {
            if (status.equals("APPROVED")) {
                ps = connection.prepareStatement(queryApprove);
                ps.executeUpdate();
                ps.close();
            } else {
                ps = connection.prepareStatement(querySuspend);
                ps.executeUpdate();
                ps.close();
            }

            ps = connection.prepareStatement(queryUser);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }
    }
    protected void updateBalance(String username, double amount) {

        PreparedStatement ps = null;
        String queryUpdate = "UPDATE members SET balance=(balance+" + amount + ") WHERE id='" + username + "'";

        try {
            ps = connection.prepareStatement(queryUpdate);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }
    }
    private double calcClaimFee() {

        double fee = 0;
        double sum = 0;
        double count = 0;
        String queryCount = "SELECT COUNT(*) FROM members";
        String querySum = "SELECT SUM(amount) FROM claims WHERE status ='APPROVED' AND date BETWEEN '" + startOfYear + "' AND '" + endOfYear + "'";

        try {
            executeQuery(queryCount);
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            executeQuery(querySum);
            while (resultSet.next()) {
                sum = resultSet.getFloat(1);
            }
            fee = sum / count;
            fee = (fee * 100d) / 100d;
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
        }

        return fee;
    }    
}
