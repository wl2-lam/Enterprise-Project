/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.JdbcQry;
//import model.member_request;
import model.Member;
import java.util.*;
import java.text.ParseException;

import java.util.Date;

/**
 *
 * @author wl2-lam
 */
public class register extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Member m = new Member();
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("username");
        JdbcQry j= new JdbcQry( (Connection) request.getServletContext().getAttribute("connection"));       
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String address = request.getParameter("address");
        String DOB = request.getParameter("DOB");

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/dd");
            Date d = formatter.parse(DOB);
            java.sql.Date sql = new java.sql.Date(d.getTime());
           
            m.setDor(sql);

        } catch (ParseException e) {
        }
        
        m.setId(username);
        m.setAddress(address);     
        m.setName(name);
        boolean b = j.idExist(username);
        if (b == false) {
            PrintWriter out = response.getWriter();
            out.print("Sorry the username has been taken");
        }else{
            j.registerMember(m, password);
        }
        
//        JdbcQry j = new JdbcQry();
//        if (j.exists(data[0])) {
//            PrintWriter out = response.getWriter();
//             out.print("Sorry the username has been taken"); 
//        }else{
//           j.insert(data);
//           response.sendRedirect("home.html");
//        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
