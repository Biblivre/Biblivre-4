<%@ page import="biblivre.login.LoginDTO "%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.index.css" />
</layout:head>

<layout:body>
	<div class="picture">
		<img src="static/images/main_picture_1.jpg"/>
	</div>
	
	<div class="text">
		<%
			LoginDTO login = (LoginDTO) session.getAttribute(request.getAttribute("schema") + ".logged_user");
			pageContext.setAttribute("login", login);

			if (login != null) {
				pageContext.setAttribute("name", login.getFirstName());
			}
		%>

		<c:choose>
			<c:when test="${empty login}">
				<i18n:text key="text.main.logged_out" />
			</c:when>
			<c:otherwise>
				<i18n:text key="text.main.logged_in" param1="${name}" />
			</c:otherwise>
		</c:choose>
	</div>
</layout:body>
