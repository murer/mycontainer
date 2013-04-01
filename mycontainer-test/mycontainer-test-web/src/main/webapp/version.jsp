<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http:www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
  <head>
   <title>greeting page</title>
  </head>    
  <body>
     <f:view>
     	<h3><h:outputText value="#{testMB.version}" /></h3>
     </f:view>
 </body>	
</html> 
