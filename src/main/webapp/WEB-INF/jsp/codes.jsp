<%--
  Created by IntelliJ IDEA.
  User: Александр
  Date: 21.05.2017
  Time: 21:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Codes</title>
    <link href="/resources/css/main.css" rel="stylesheet" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<div class="container">
    <div class="row">
        <div class="col-lg-5 col-lg-offset-1 col-sm-5 col-sm-offset-1">
            <p>Проверяемый код: ${verifiableFileName}</p>
            ${verifiableCode}
        </div>
        <div class="col-lg-5 col-sm-5">
            <p>Исходный код: ${uniqueFileName}</p>
            ${uniqueCode}
        </div>
    </div>
</div>
</body>
</html>
