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
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="https://github.com/psi-probe/psi-probe/jsp/tags" prefix="probe" %>

<%-- Session attributes view, nothing spectacular. --%>
<html>

	<head>
		<title>
			<spring:message code="probe.jsp.title.sessionAttibutes"/>
		</title>
	</head>

	<%-- Make Tab #1 visually "active". --%>
	<c:set var="navTabApps" value="active" scope="request"/>
	<c:set var="use_decorator" value="application" scope="request"/>
	<c:set var="appTabSessions" value="active" scope="request"/>


	<body>
		<ul class="options">
			<li id="back">
				<a href="<c:url value='/sessions.htm'><c:param name='webapp' value='${param.webapp}'/><c:param name='size'><c:out value='${param.size}'/></c:param></c:url>">
					<spring:message code="probe.jsp.sessionAttibutes.menu.back"/>
				</a>
			</li>
			<c:if test="${! empty session}">

				<li id="delete">
					<a href="<c:url value='/app/expire.htm'><c:param name='webapp' value='${param.webapp}' /><c:param name='sid' value='${param.sid}' /></c:url>">
						<spring:message code="probe.jsp.sessionAttibutes.menu.destroy"/>
					</a>
				</li>

				<c:choose>
					<c:when test="${param.size}">
						<li id="size">
							<a href="?<probe:toggle param='size'/>">
								<spring:message code="probe.jsp.hidesize"/>
							</a>
						</li>
					</c:when>
					<c:otherwise>
						<li id="size">
							<a href="?<probe:toggle param='size'/>">
								<spring:message code="probe.jsp.showsize"/>
							</a>
						</li>
					</c:otherwise>
				</c:choose>

			</c:if>
		</ul>

		<div class="embeddedBlockContainer">
			<c:choose>
				<c:when test="${! empty session}">
					<h3><spring:message code="probe.jsp.sessionAttibutes.h3.card"/></h3>

					<div class="shadow">
						<div class="info">
							<p>
							<spring:message code="probe.jsp.sessionAttibutes.card.application"/>&#160;<span class="value"><c:out value="${param.webapp}" /></span>
							<spring:message code="probe.jsp.sessionAttibutes.card.id"/>&#160;<span class="value">${session.id}</span>
							<spring:message code="probe.jsp.sessionAttibutes.card.serializable"/>&#160;
							<c:choose>
								<c:when test="${session.serializable}">
									<span class="okValue"><spring:message code="probe.jsp.sessionAttibutes.card.serializable.yes"/></span>
								</c:when>
								<c:otherwise>
									<span class="errorValue"><spring:message code="probe.jsp.sessionAttibutes.card.serializable.no"/></span>
								</c:otherwise>
							</c:choose>
							<spring:message code="probe.jsp.sessionAttibutes.card.age"/>&#160;<span class="value"><probe:duration value="${session.age}"/></span>
							<spring:message code="probe.jsp.sessionAttibutes.card.idleTime"/>&#160;<span class="value"><probe:duration value="${session.idleTime}"/></span>
							<spring:message code="probe.jsp.sessionAttibutes.card.manager"/>&#160;<span class="value">${session.managerType}</span>
							<spring:message code="probe.jsp.sessionAttibutes.card.info"/>&#160;<span class="value">${session.info}</span>
							<spring:message code="probe.jsp.sessionAttibutes.card.created"/>&#160;<span class="value">${session.creationTime}</span>
							<spring:message code="probe.jsp.sessionAttibutes.card.lastAccessed"/>&#160;<span class="value">${session.lastAccessTime}</span>
							<spring:message code="probe.jsp.sessionAttibutes.card.maxIdleTime"/>&#160;<span class="value"><probe:duration value="${session.maxIdleTime}"/></span>
							<spring:message code="probe.jsp.sessionAttibutes.card.expiryTime"/>&#160;<span class="value">${session.expiryTime}</span>
							<c:if test="${param.size}">
								<spring:message code="probe.jsp.sessionAttibutes.card.size"/>&#160;<span class="value"><probe:volume value="${session.size}"/></span>
							</c:if>
							</p>
						</div>
					</div>

					<h3><spring:message code="probe.jsp.sessionAttibutes.h3.attributes"/></h3>
					<c:choose>
						<c:when test="${! empty session.attributes}">
							<display:table name="session.attributes" uid="attribute"
									class="genericTbl" style="padding:0;border-spacing:0;border-collapse:separate;"
									requestURI="">
								<display:column title="&#160;" class="leftmost" style="width: 20px;">
									<c:url value="/app/rmsattr.htm" var="rmsattr_url">
										<c:param name="webapp" value="${param.webapp}"/>
										<c:param name="sid" value="${param.sid}"/>
										<c:param name="attr" value="${attribute.name}"/>
									</c:url>
									<a href="${rmsattr_url}" class="imglink">
										<img class="lnk" src="${pageContext.request.contextPath}<spring:theme code='remove.img'/>"
												alt="<spring:message code='probe.jsp.sessionAttibutes.col.delete'/>"
												title="<spring:message code='probe.jsp.sessionAttibutes.col.delete.title'/>"/>
									</a>
								</display:column>
								<display:column property="name" sortable="true"
										titleKey="probe.jsp.sessionAttibutes.col.name"/>
								<display:column property="type" sortable="true" titleKey="probe.jsp.sessionAttibutes.col.type"/>
								<display:column sortable="true" sortProperty="serializable"
										titleKey="probe.jsp.sessionAttibutes.col.serializable">
									<c:choose>
										<c:when test="${attribute.serializable}">
											<span class="okValue"><spring:message
													code="probe.jsp.sessionAttibutes.card.serializable.yes"/></span>
										</c:when>
										<c:otherwise>
											<span class="errorValue"><spring:message
													code="probe.jsp.sessionAttibutes.card.serializable.no"/></span>
										</c:otherwise>
									</c:choose>
								</display:column>
								<c:if test="${param.size}">
									<display:column sortProperty="size" sortable="true"
											titleKey="probe.jsp.sessionAttibutes.col.size" class="highlighted">
										<probe:volume value="${attribute.size}"/>
									</display:column>
								</c:if>
								<c:if test="${session.allowedToViewValues}">
									<display:column sortProperty="value" sortable="true"
											titleKey="probe.jsp.sessionAttibutes.col.value">
										<c:catch var="displayException">
											<c:out value="${attribute.value}" escapeXml="true"/>
										</c:catch>
										<c:if test="${not empty displayException}">
											<span class="errorValue">**************</span>
											<c:remove var="displayException" />
										</c:if>
									</display:column>
								</c:if>
							</display:table>
						</c:when>
						<c:otherwise>
							<spring:message code="probe.jsp.sessionAttibutes.noattributes"/>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<div class="errorMessage">
						<p>
							<spring:message code="probe.jsp.sessionAttibutes.invalidSid"/>
						</p>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</body>
</html>
