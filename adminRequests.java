/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.SQLException;

/**
 *
 * @author Owen Harvey
 */
//TODO: filtering/searching of tables
public class adminRequests {
    
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
}
