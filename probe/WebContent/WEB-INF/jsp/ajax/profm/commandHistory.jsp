<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<c:choose>
	<c:when test="${! empty commandHistory}">
		<ul>
			<c:forEach var="command" items="${commandHistory}" varStatus="id">
				<li>
					<a href="<c:url value='/profm/commandHistoryItem.ajax?id=${id.index}'/>" onClick="getCommandHistoryItem(this); return false;">
						<div><spring:escapeBody htmlEscape="true" javaScriptEscape="false">${command}</spring:escapeBody></div>
					</a>
				</li>
			</c:forEach>
		</ul>
	</c:when>
	<c:otherwise>
		<div id="historyEmpty"><spring:message code="probe.jsp.profm.commandHistory.empty"/></div>
	</c:otherwise>
</c:choose>