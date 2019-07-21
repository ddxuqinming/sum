<%--
  Created by IntelliJ IDEA.
  User: sun2
  Date: 2018/3/28
  Time: 19:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="sum.dataprovider.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="sum.dataprovider.DataTable.DataTable" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%
 DataTable dtb=new DataTable();
     dtb.columns().add("FID","ID", java.sql.Types.TINYINT );
     dtb.columns().add("FName","姓名", Types.VARCHAR );
       DataTable.DataRow drw=dtb.newRow();
       drw.Add("FID",1);
       drw.Add("FName","徐秦敏");

    System.out.println("size=" + dtb.rows(0).getValue("FName"));
%>
sfsfsdf
</body>
</html>
