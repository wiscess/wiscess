<%--

    Licensed under the GPL License. You may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      https://www.gnu.org/licenses/old-licenses/gpl-2.0.html

    THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
    WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
    PURPOSE.

--%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>


<html>
	<head>
		<title><spring:message code="probe.jsp.title.profm"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">    
		<script type="text/javascript" language="javascript" src="<c:url value="/js/prototype.js"/>"></script>
		<script type="text/javascript" language="javascript" src="<c:url value="/js/behaviour.js"/>"></script>
		<script type="text/javascript" language="javascript" src="<c:url value="/js/func.js"/>"></script>
		<script type="text/javascript" language="javascript" src="<c:url value="/js/jsencrypt.min.js"/>"></script>
		<script>
		function checkForm(){
			$("password").value=encryptStr($("pass").value);
			return true;
		}
		</script>
	</head>
	<body>
<form name="loginForm" method="post" action="<c:url value="/login"/>">
<%--//=new SimpleDateFormat("HH:mm:ss").format(new Date()) --%>
	<div id="form">
		<div id="form_user">
			<div class="l">用户名</div>
			<span class="r dl"> 
			<input name="username" type="text" id="username" autocomplete="off">
			</span>
		</div>
		<div id="form_psw">
			<div class="l">密&nbsp;&nbsp;码</div>
			<span class="r dl"> 
			<input type="password" id="pass" autocomplete="off"/>
			<input type="hidden" name="password" id="password" value="" autocomplete="off"/>
			</span>
		</div>
		<div id="form_submit">
			<input type="submit" value="登录" onclick="return checkForm();"/>
		</div>
	</div>
</form>
</body>

</html>
