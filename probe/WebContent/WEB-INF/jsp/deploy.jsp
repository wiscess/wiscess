<%--

    Licensed under the GPL License. You may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      https://www.gnu.org/licenses/old-licenses/gpl-2.0.html

    THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
    WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
    PURPOSE.

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
	<head>
		<title><spring:message code="probe.jsp.title.deployment"/></title>
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}<spring:theme code='deploy.css'/>"/>
	</head>

	<body>

		<c:set var="navTabDeploy" value="active" scope="request"/>

		<c:if test="${! empty errorMessage}">
			<div class="errorMessage">
				<p>
					${errorMessage}
				</p>
			</div>
		</c:if>

		<c:if test="${success}">
			<div id="successMessage">
				<c:set var="successLink">
					<a href="<c:url value='/appsummary.htm'><c:param name='webapp' value='${contextName}'/></c:url>">
						${contextName}
					</a>
				</c:set>
				<spring:message code="probe.jsp.deployment.war.success" arguments="${successLink}" />
				<c:if test="${compileSuccess}">
					<a href="<c:url value='/app/jsp.htm'><c:param name='webapp' value='${contextName}'/></c:url>">
						<spring:message code="probe.jsp.deployment.compilationDetails"/>
					</a>
				</c:if>
			</div>
		</c:if>
		
		<c:if test="${successFile}">
			<div id="successMessage">
				<c:set var="successLink">
					<a href="<c:url value='/appsummary.htm'><c:param name='webapp' value='${contextName}'/></c:url>">
						${contextName}
					</a>
				</c:set>
				<spring:message code="probe.jsp.deployment.file.success"/>
				<c:if test="${reloadContext}">
					<spring:message code="probe.jsp.deployment.file.reloadSuccess" arguments="${successLink}" />
				</c:if>
			</div>
		</c:if>

		<div id="deploy">
			<div id="deployScenario1" class="deploy">
				<span class="deployLabel"><spring:message code="probe.jsp.deployment.s1.title"/></span>

				<div class="deployDescription"><spring:message code="probe.jsp.deployment.s1.description"/></div>

				<form action="<c:url value='/adm/war.htm'/>" method="post" enctype="multipart/form-data">
					<dl>
						<dt><label for="war"><spring:message code="probe.jsp.deployment.s1.file.label"/> <em>*</em></label></dt>
						<dd><input id="war" type="file" name="war" size="90"/></dd>
						<dt><label for="context"><spring:message code="probe.jsp.deployment.s2.context.label"/>&#160;</label></dt>
						<dd><input id="context" type="text" name="context" size="90"/></dd>
						<dt><span class="cb"><input id="update" type="checkbox" name="update" value="yes"/><label for="update">&#160;
									<spring:message code="probe.jsp.deployment.s1.update.label"/></label></span></dt>
						<dt><span class="cb"><input id="discard" type="checkbox" name="discard" value="yes"/><label for="discard">&#160;
									<spring:message code="probe.jsp.deployment.s1.discard.label"/></label></span></dt>
						<dt><span class="cb"><input id="compile" type="checkbox" name="compile" value="yes"/><label for="compile">&#160;
									<spring:message code="probe.jsp.deployment.s1.compile.label"/></label></span></dt>

						<dd class="submit">
							<input class="b" type="submit" value="<spring:message code='probe.jsp.deployment.s1.submit'/>"/>
						</dd>
					</dl>
				</form>
			</div>

			<div id="deployScenario2" class="deploy">
				<span class="deployLabel"><spring:message code="probe.jsp.deployment.s2.title"/></span>

				<div class="deployDescription"><spring:message code="probe.jsp.deployment.s2.description"/></div>

				<form action="<c:url value='/adm/deploycontext.htm'/>" method="get">
					<dl>
						<dt><label for="context2"><spring:message code="probe.jsp.deployment.s2.context.label"/></label> <em>*</em></dt>
						<dd><input id="context2" type="text" name="context" size="90"/></dd>

						<dd class="submit">
							<input class="b" type="submit" value="<spring:message code='probe.jsp.deployment.s1.submit'/>"/>
						</dd>
					</dl>
				</form>
			</div>
			
			<div id="deployScenario3" class="deploy">
				<span class="deployLabel"><spring:message code="probe.jsp.deployment.s3.title"/></span>

				<div class="deployDescription"><spring:message code="probe.jsp.deployment.s3.description"/></div>

				<form action="<c:url value='/adm/deployfile.htm'/>" method="post" enctype="multipart/form-data">
					<dl>
						<dt><label for="file1"><spring:message code="probe.jsp.deployment.s3.file.label"/> <em>*</em></label></dt>
						<dd><input id="file1" type="file" name="file1" size="90"/></dd>
						<dt><label for="context"><spring:message code="probe.jsp.deployment.s3.context.label"/>&#160;</label></dt>
						<dd>
							<!--input id="context" type="text" name="context" size="90"/-->
							<select id="context" name="context" style="width: 90px">
								<c:forEach var="app" items="${apps}">
									<option value="${app.value}">${app.label}</option>
								</c:forEach>
							</select>
						</dd>
						<dt><label for="where"><spring:message code="probe.jsp.deployment.s3.where.label"/>&#160;</label></dt>
						<dd><input id="where" type="text" name="where" size="90"/></dd>
						<dt><span class="cb"><input id="reload" type="checkbox" name="reload" value="yes"/><label for="reload">&#160;
							<spring:message code="probe.jsp.deployment.s3.reload.label"/></label></span></dt>
						<dt><span class="cb"><input id="discard" type="checkbox" name="discard" value="yes"/><label for="discard">&#160;
							<spring:message code="probe.jsp.deployment.s1.discard.label"/></label></span></dt>
						<dd class="submit">
							<input class="b" type="submit" value="<spring:message code='probe.jsp.deployment.s3.submit'/>"/>
						</dd>
					</dl>
				</form>
			</div>
		</div>
	</body>

</html>
