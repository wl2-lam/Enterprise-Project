/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author wl2-lam
 */
import java.sql.*;
public class Payment {
    private final int[] d = new int[16];
    private int id;
    private String memID;
    private String typeOfPayment;
    private double amount;
    private Date date;
    private String status;
    
    public boolean credircheck(String s){
          try {

            
            for (int i = 0; i < s.length(); i++) {
                d[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
            }
            for (int x = 0; x < d.length; x += 2) {
                int check = d[x] + d[x];

                if (check >= 10) {
                    check = check - 9;
                }

                d[x] = check;

            }

            int cal = (d[0] + d[1] + d[2] + d[3] + d[4] + d[5] + d[6] + d[7] + d[8] + d[9] + d[10] + d[11] + d[12] + d[13] + d[14] + d[15]);
            cal = cal % 10;

            if (cal == 0) {
               
                return true;

            } else {
              return false;
            }
            
        } catch (NumberFormatException e) {
            return false;
        }
         
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Payment() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTypeOfPayment() {
        return typeOfPayment;
    }

    public void setTypeOfPayment(String typeOfPayment) {
        this.typeOfPayment = typeOfPayment;
    }

    public String getMemID() {
        return memID;
    }

    public void setMemID(String memID) {
        this.memID = memID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    
}
