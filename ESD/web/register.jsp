<%-- 
    Document   : register
    Created on : 16-Nov-2016, 14:17:25
    Author     : wl2-lam
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"      "http://www.w3.org/TR/html4/loose.dtd">
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" />
  <script src="http://code.jquery.com/jquery-1.8.3.js"></script>
  <script src="http://code.jquery.com/ui/1.9.2/jquery-ui.js"></script>
  <script>
$(function() {
    $( "#datepicker" ).datepicker({
        dateFormat: "yy-mm-dd"
    });
});
</script>
        <title>Register</title>
        
    </head>
    <body>
        <h1>Please enter the following details to complete</h1>
        
        <form method="POST" action="register">
            please enter your Username: <input type="text" name="username" ><br>
            please enter your name:<input type="text" name="name"><br>
            please enter your Password:<input type="text" name="password"><br>
            please enter your Address:<input type="text" name="address"><br>
            Please enter your Date of birth:<input type="text" id="datepicker" name="DOB" /><br>
            
            <input type="submit" value="Submit"><br>
        </form>
    </body>
</html>
