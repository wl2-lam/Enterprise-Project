package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class payment_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("    <head>\n");
      out.write("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("        <title>Payment</title>\n");
      out.write("    </head>\n");
      out.write("    <body>\n");
      out.write("        <form method=\"POST\" action=\"payment\">\n");
      out.write("        <h1>Please enter you card  credentials to proceed</h1>\n");
      out.write("       Card number: <input type=\"text\" name=\"cardnum\" ><br>\n");
      out.write("       Card Holder Name: <input type=\"text\" name=\"name\" ><br>\n");
      out.write("       <table border=\"0\" cellspacing=\"0\" >\n");
      out.write("card expiry date:\n");
      out.write("<tr><td  align=left  >   \n");
      out.write("\n");
      out.write("<select name=month value=''>Select Month</option>\n");
      out.write("<option value='01'>January</option>\n");
      out.write("<option value='02'>February</option>\n");
      out.write("<option value='03'>March</option>\n");
      out.write("<option value='04'>April</option>\n");
      out.write("<option value='05'>May</option>\n");
      out.write("<option value='06'>June</option>\n");
      out.write("<option value='07'>July</option>\n");
      out.write("<option value='08'>August</option>\n");
      out.write("<option value='09'>September</option>\n");
      out.write("<option value='10'>October</option>\n");
      out.write("<option value='11'>November</option>\n");
      out.write("<option value='12'>December</option>\n");
      out.write("</select>\n");
      out.write("</td><td  align=left  >   \n");
      out.write("\n");
      out.write("<select name=Year>\n");
      out.write("\n");
      out.write("<option value='17'>17</option>\n");
      out.write("<option value='18'>18</option>\n");
      out.write("<option value='19'>19</option>\n");
      out.write("<option value='20'>20</option>\n");
      out.write("<option value='21'>21</option>\n");
      out.write("<option value='22'>22</option>\n");
      out.write("<option value='23'>23</option>\n");
      out.write("<option value='24'>24</option>\n");
      out.write("<option value='25'>25</option>\n");
      out.write("<option value='26'>26</option>\n");
      out.write("<option value='27'>27</option>\n");
      out.write("<option value='28'>28</option>\n");
      out.write("<option value='29'>29</option>\n");
      out.write("<option value='30'>30</option>\n");
      out.write("<option value='31'>31</option>\n");
      out.write("\n");
      out.write("</select>\n");
      out.write("        <input type=\"submit\" value=\"Submit\">\n");
      out.write("    </body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
