<%--
  Created by IntelliJ IDEA.
  User: Badrad
  Date: 11.10.2021
  Time: 14:37
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="/WEB-INF/functions.tld" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html lang="ru">
<head>
    <link href="style.css" rel="stylesheet" type="text/css" />
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<table>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>
    <c:forEach items="${requestScope.meals}" var="mealTo">
        <tr style="color:${mealTo.excess ? 'red' : 'green'}">
            <td>${f:formatLocalDateTime(mealTo.dateTime, 'yyyy-MM-dd HH:mm')}</td>
            <td>${mealTo.description}</td>
            <td>${mealTo.calories}</td>
            <th>Update</th>
            <th>Delete</th>
        </tr>
    </c:forEach>
</table>
</body>
</html>
