/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;



/**
 *
 * @author Owen Harvey
 */
public class JdbcQry {
    
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    String db = "xyz_assoc";
    ResultSet resultSet;
    LocalDate now = LocalDate.now();
    String startOfYear = now.with(TemporalAdjusters.firstDayOfYear()).toString();
    String endOfYear = now.with(TemporalAdjusters.lastDayOfYear()).toString();
    String today = now.toString();
    

   
    


    Connection con;
   
    DecimalFormat df = new DecimalFormat("#.##");

    public JdbcQry(Connection con) {
        this.con = con;
    }

    
    public boolean idcheck(String username, String password) throws ClassNotFoundException, SQLException {

        boolean found = false;

        String query = "SELECT * FROM users";

        try {
            selectQuery(query);
            while (resultSet.next() && found == false) {
                if (username.equals(resultSet.getString("id"))) {
                    if (password.equals(resultSet.getString("password"))) {
                        found = true;
                    }
                }
            }

        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }
        return found;
    }

    public Member getMember(String username) {

        Member member = new Member();

        String query = "SELECT * FROM members WHERE id = '" + username + "'";

        try {
            suspendMember();
            selectQuery(query);
            while (resultSet.next()) {
                member.setId(resultSet.getString("id"));
                member.setName(resultSet.getString("name"));
                member.setAddress(resultSet.getString("address"));
                member.setDob(resultSet.getDate("dob"));
                member.setDor(resultSet.getDate("dor"));
                member.setStatus(resultSet.getString("status"));
                member.setBalance(resultSet.getDouble("balance"));
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return member;
    }

    public boolean checkAdmin(String username) {

        boolean admin = false;
        String query = "SELECT * FROM users WHERE status ='ADMIN'";
        try {
            selectQuery(query);
            while (resultSet.next() && admin == false) {
                if (username.equals(resultSet.getString("id"))) {
                    admin = true;
                } else {
                    admin = false;
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }
        return admin;
    }//method


    public boolean idExist(String username) {

        boolean exist = false;
        String query = "SELECT * FROM users";
        try {
            selectQuery(query);
            while (resultSet.next()) {
                if (username.equals(resultSet.getString("id"))) {
                    exist = true;
                    break;
                } else {
                    exist = false;
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return exist;
    }//method

    public void registerMember(Member member, String password) {

        PreparedStatement ps = null;
        
             Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
       
       

        try {
            ps = con.prepareStatement("INSERT INTO members VALUES (?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, member.getId());
            ps.setString(2, member.getName());
            ps.setString(3, member.getAddress());
            ps.setDate(4, member.getDob());
            ps.setDate(5, currentDate);
            ps.setString(6, "APPLIED");
            ps.setDouble(7, 10);

            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("INSERT INTO users VALUES (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, member.getId());
            ps.setString(2, password);
            ps.setString(3, "APPLIED");

            ps.executeUpdate();
            ps.close();

        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }
    }

   
    public ArrayList paymentList(String username) {

        ArrayList paymentList = new ArrayList();
        String query = "SELECT * FROM payments WHERE mem_id ='" + username + "'";

        try {
            selectQuery(query);
            if (!resultSet.isBeforeFirst()) {
                paymentList = new ArrayList();
            } else {
                while (resultSet.next()) {
                    Payment payment = new Payment();
                    payment.setId(resultSet.getInt("id"));
                    payment.setMemID(resultSet.getString("mem_id"));
                    payment.setTypeOfPayment(resultSet.getString("type_of_payment"));
                    payment.setAmount(resultSet.getDouble("amount"));
                    payment.setDate(resultSet.getDate("date"));
                    payment.setStatus(resultSet.getString("status"));
                    paymentList.add(payment);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return paymentList;
    }//method

    public String makePayment(Payment payment) {

        PreparedStatement ps = null;
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        Member user = getMember(payment.getMemID());
        double newBalance = user.getBalance() - payment.getAmount();
        double balance = 0;
        String message = "";

        if (newBalance >= 0) {
            try {
                ps = con.prepareStatement("INSERT INTO payments(mem_id,type_of_payment,amount,date,status) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, payment.getMemID());
                ps.setString(2, payment.getTypeOfPayment());
                ps.setDouble(3, payment.getAmount());
                ps.setDate(4, currentDate);
                balance = balance - payment.getAmount();
                if (user.getStatus().equals("APPLIED")) {
                    ps.setString(5, "SUBMITTED");
                } else {
                    ps.setString(5, "APPROVED");
                    updateBalance(payment.getMemID(), balance);
                    if (user.getStatus().equals("SUSPENDED")) {
                        updateStatus(payment.getMemID(), "APPROVED");
                    }
                }
                ps.executeUpdate();
                ps.close();

                message = "Payment submitted.";
            } catch (SQLException s) {
                System.out.println("SQL statement is not executed! " + s.getMessage());
            }
        } else {
            message = "Payment failed.";
        }
        return message;
    }//method

    private boolean claimLimit(String username) {

        boolean limit = false;
        int count = 0;
        String query = "SELECT COUNT(*) FROM claims WHERE mem_id = '" + username + "' AND status <> 'REJECTED' AND date BETWEEN '" + startOfYear + "' AND '" + endOfYear + "'";

        try {
            selectQuery(query);
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            if (count >= 2) {
                limit = true;
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return limit;
    }//method

    private boolean claimDate(String username, Date claimDate) {
        boolean valid = false;
        String query = "SELECT dor FROM members WHERE id='" + username + "'";
        Date dorDate = null;
        try {
            selectQuery(query);
            while (resultSet.next()) {
                dorDate = resultSet.getDate(1);
            }
            int start = claimDate.getYear() * 12 + claimDate.getMonth();
            int end = dorDate.getYear() * 12 + dorDate.getMonth();
            if (end > start) {
                if ((end - start) <= 6) {
                    valid = true;
                }
            }

        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }
        return valid;
    }

    public String submitClaim(Claim claim) {

        PreparedStatement ps = null;
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        String username = claim.getMemID();
        boolean limit = claimLimit(username);
        String response = null;

        if (claimDate(username, currentDate) == true) {
            if (claimLimit(username) == false) {
                try {
                    ps = con.prepareStatement("INSERT INTO claims(mem_id,date,rationale,status,amount) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, username);
                    ps.setDate(2, currentDate);
                    ps.setString(3, claim.getRationale());
                    ps.setString(4, "SUBMITTED");
                    ps.setDouble(5, claim.getAmount());

                    ps.executeUpdate();
                    ps.close();
                } catch (SQLException s) {
                    System.out.println("SQL statement is not executed! " + s.getMessage());
                }
                response = "Claim submitted.";
            } else {
                response = "You have already made 2 claims this year.";
            }
        } else {
            response = "You must be an approved member for at least 6 months before making claim.";
        }
        return response;
    }//method

   
    public ArrayList memberList() {

        ArrayList memberList = new ArrayList();
        String query = "SELECT * FROM members";

        try {
            selectQuery(query);
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
                    member.setBalance(resultSet.getDouble("balance"));
                    memberList.add(member);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return memberList;
    }//method

    public Member getMember(String criteria, String value) {

        Member member = new Member();
        String query = "SELECT * FROM members WHERE " + criteria + " = '" + value + "'";

        try {
            selectQuery(query);
            while (resultSet.next()) {
                member.setId(resultSet.getString("id"));
                member.setName(resultSet.getString("name"));
                member.setAddress(resultSet.getString("address"));
                member.setDob(resultSet.getDate("dob"));
                member.setDor(resultSet.getDate("dor"));
                member.setStatus(resultSet.getString("status"));
                member.setBalance(resultSet.getDouble("balance"));
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return member;
    }//method

    public ArrayList balanceList() {

        ArrayList balanceList = new ArrayList();
        String query = "SELECT * FROM members WHERE id NOT LIKE 'ADMIN' AND balance>0.00";

        try {
            selectQuery(query);
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
                    member.setBalance(resultSet.getDouble("balance"));
                    balanceList.add(member);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return balanceList;
    }//method

    public ArrayList claimList(String username) {

        ArrayList claimList = new ArrayList();
        if (username.equals("")) {
            username = "%";
        }
        String query = "SELECT * FROM claims WHERE mem_id LIKE'" + username + "'";

        try {
            selectQuery(query);
            if (!resultSet.isBeforeFirst()) {
                claimList = new ArrayList();
            } else {
                while (resultSet.next()) {
                    Claim claim = new Claim();

                    claim.setAmount(resultSet.getDouble("amount"));
                    claim.setDate(resultSet.getDate("date"));
                    claim.setId(resultSet.getInt("id"));
                    claim.setMemID(resultSet.getString("mem_id"));
                    claim.setRationale(resultSet.getString("rationale"));
                    claim.setStatus(resultSet.getString("status"));
                    claimList.add(claim);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return claimList;
    }//method

    public ArrayList applicationList() {

        ArrayList applicationList = new ArrayList();
        String query = "SELECT * FROM payments WHERE EXISTS(SELECT * FROM members WHERE members.id=payments.mem_id AND members.status='APPLIED' AND payments.type_of_payment='MEMBER' AND payments.status='SUBMITTED')";

        try {
            selectQuery(query);
            if (!resultSet.isBeforeFirst()) {
                applicationList = new ArrayList();
            } else {
                while (resultSet.next()) {
                    Payment payment = new Payment();
                    payment.setId(resultSet.getInt("id"));
                    payment.setMemID(resultSet.getString("mem_id"));
                    payment.setTypeOfPayment(resultSet.getString("type_of_payment"));
                    payment.setAmount(resultSet.getDouble("amount"));
                    payment.setDate(resultSet.getDate("date"));
                    payment.setStatus(resultSet.getString("status"));
                    applicationList.add(payment);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return applicationList;
    }//method

    public boolean processClaim(int id, String status) {

        boolean updated = false;
        PreparedStatement ps = null;
        String query = "SELECT id FROM claims WHERE status='SUBMITTED'";
        String update = "UPDATE claims SET status='" + status + "' WHERE id =" + id;

        try {
            selectQuery(query);
            while (resultSet.next() && updated == false) {
                if (id == resultSet.getInt(1)) {
                    ps = con.prepareStatement(update);
                    ps.executeUpdate();
                    ps.close();
                    updated = true;
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return updated;
    }//method

    public boolean processApplication(int id) {

        boolean updated = false;
        String update = "UPDATE payments SET status='APPROVED' WHERE id=" + id;
        String query = "SELECT * FROM payments WHERE status='SUBMITTED'";
        String username;
        PreparedStatement ps = null;

        try {
            selectQuery(query);
            while (resultSet.next() && updated == false) {
                if (id == resultSet.getInt("id")) {
                    username = resultSet.getString("mem_id");

                    ps = con.prepareStatement(update);
                    ps.executeUpdate();
                    ps.close();

                    updateStatus(username, "APPROVED");
                    updateBalance(username, -10);

                    updated = true;
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }

        return updated;
    }//method

    public String claimFee() {

        PreparedStatement ps = null;
        double fee = calcClaimFee();
        String query = "UPDATE members SET balance = (balance+" + fee + ") WHERE status!='APPLIED'";
        String message = "The claim fee for each member this year is: £" + fee;

//        if (today.equals(endOfYear)) {
        try {
            ps = con.prepareStatement(query);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }
        return message;
//        }
    }//method

    public ArrayList listIncome() {
        ArrayList incomeList = new ArrayList();
        String query = "SELECT * FROM payments WHERE status='APPROVED' AND date BETWEEN '" + startOfYear + "' AND '" + endOfYear + "'";
        try {
            selectQuery(query);
            if (!resultSet.isBeforeFirst()) {
                incomeList = new ArrayList();
            } else {
                while (resultSet.next()) {
                    Payment payment = new Payment();
                    payment.setId(resultSet.getInt("id"));
                    payment.setMemID(resultSet.getString("mem_id"));
                    payment.setTypeOfPayment(resultSet.getString("type_of_payment"));
                    payment.setAmount(resultSet.getDouble("amount"));
                    payment.setDate(resultSet.getDate("date"));
                    payment.setStatus(resultSet.getString("status"));
                    incomeList.add(payment);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }
        return incomeList;
    }//method

    public ArrayList listExpense() {
        ArrayList expenseList = new ArrayList();
        String query = "SELECT * FROM claims WHERE status='APPROVED' AND date BETWEEN '" + startOfYear + "' AND '" + endOfYear + "'";
        try {
            selectQuery(query);
            if (!resultSet.isBeforeFirst()) {
                expenseList = new ArrayList();
            } else {
                while (resultSet.next()) {
                    Claim claim = new Claim();
                    claim.setId(resultSet.getInt("id"));
                    claim.setMemID(resultSet.getString("mem_id"));
                    claim.setDate(resultSet.getDate("date"));
                    claim.setRationale(resultSet.getString("rationale"));
                    claim.setStatus(resultSet.getString("status"));
                    claim.setAmount(resultSet.getDouble("amount"));

                    expenseList.add(claim);
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());
        }
        return expenseList;
    }//method

    private void suspendMember() {

        String query = "SELECT id FROM members WHERE status='APPROVED' AND dor <='" + today + "'";

        try {
            selectQuery(query);
            while (resultSet.next()) {
                String username = resultSet.getString(1);
                updateStatus(username, "SUSPENDED");
                updateBalance(username, 10);
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());

        }
    }//method

    private void updateStatus(String username, String status) {

        PreparedStatement ps = null;
        String queryApprove = "UPDATE members SET status ='APPROVED', dor =DATE_ADD(dor, INTERVAL 1 YEAR) WHERE id='" + username + "'";
        String querySuspend = "UPDATE members SET status ='SUSPENDED' WHERE id='" + username + "'";
        String queryUser = "UPDATE users SET status ='" + status + "' WHERE id='" + username + "'";

        try {
            if (status.equals("APPROVED")) {
                ps = con.prepareStatement(queryApprove);
                ps.executeUpdate();
                ps.close();
            } else {
                ps = con.prepareStatement(querySuspend);
                ps.executeUpdate();
                ps.close();
            }

            ps = con.prepareStatement(queryUser);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());

        }
    }//method

    private void updateBalance(String username, double amount) {

        PreparedStatement ps = null;
        String queryUpdate = "UPDATE members SET balance=(balance+" + amount + ") WHERE id='" + username + "'";
        String query = "SELECT * FROM members WHERE id='" + username + "'";

        try {
            ps = con.prepareStatement(queryUpdate);
            ps.executeUpdate();
            ps.close();

            selectQuery(query);
            while (resultSet.next()) {
                String status = resultSet.getString("status");
                Double balance = resultSet.getDouble("balance");
                if (status.equals("SUSPENDED")) {
                    if (balance == 0) {
                        updateStatus(username, "APPROVED");
                    }
                }
            }
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());

        }
    }//method

    private double calcClaimFee() {

        double fee = 0;
        double sum = 0;
        double count = 0;
        double currency = 0;
        df.setRoundingMode(RoundingMode.FLOOR);        
        String queryCount = "SELECT COUNT(*) FROM members";
        String querySum = "SELECT SUM(amount) FROM claims WHERE status ='APPROVED' AND date BETWEEN '" + startOfYear + "' AND '" + endOfYear + "'";

        try {
            selectQuery(queryCount);
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            selectQuery(querySum);
            while (resultSet.next()) {
                sum = resultSet.getDouble(1);
            }
            fee = sum / count;
            currency = new Double(df.format(fee));
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());

        }

        return currency;
    }//method

    private void selectQuery(String query) {

        try {
            statement = con.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed! " + s.getMessage());

        }
    }//method
}
//class