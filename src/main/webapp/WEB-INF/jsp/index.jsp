<%--
  Created by IntelliJ IDEA.
  User: Александр
  Date: 08.12.2016
  Time: 18:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Main</title>
    <link rel="stylesheet" type="text/css" href="/resources/css/main.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<h1>Веб-приложение для поиска плагиата в исходном коде, написанном на языке программирования Java</h1>
<div class="row">
    <div class="col-lg-8 col-lg-offset-1 col-sm-7 col-sm-offset-1"  data-parsley-validate>
        <form:form action="/check" method="post" modelAttribute="uploadForm" enctype="multipart/form-data">
            <div>
                <p><label for="minPlagiarismCoef">Минимальный коэффициент плагиата: </label></p>
                <p><input class="form-control" id="minPlagiarismCoef" name="minPlagiarismCoef"
                          data-parsley-min="0.0" data-parsley-max="1.0" value="0.3"></p>
            </div>
            <div>
                <p><label for="verifiableSrc">Исходный код для проверки на плагиат.</label></p>
                <p><input type="file" name="verifiableFiles" class="form-control" webkitdirectory directory multiple></p>
                <textarea rows="14" cols="90" name="verifiableSrc" id="verifiableSrc"  class="form-control"></textarea>
            </div>
            <div>
                <p><label for="uniqueSrc">Уникальный исходный код.</label></p>
                <div id="uniqueFilesDivId">
                    <p><input type="file" name="uniqueFiles" class="form-control" webkitdirectory directory multiple></p>
                </div>
                <textarea rows="14" cols="90" name="uniqueSrc" id="uniqueSrc" class="form-control"></textarea>
            </div>
            <input type="reset" value="Очистить">
            <input type="submit" value="Проверить">
        </form:form>
    </div>
</div>
<footer>
    <p>&copy; 2017 Авижень Александр<p>
</footer>
</body>
</html>
