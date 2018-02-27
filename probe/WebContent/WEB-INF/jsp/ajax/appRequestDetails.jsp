<%--

    Licensed under the GPL License. You may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      https://www.gnu.org/licenses/old-licenses/gpl-2.0.html

    THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
    WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
    PURPOSE.

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<c:if test="${! empty app}">
	<span class="name"><spring:message code="probe.jsp.app.summary.requestCount"/></span>&#160;${app.requestCount}
	&#160;
	<span class="name"><spring:message code="probe.jsp.app.summary.errorCount"/></span>&#160;${app.errorCount}
</c:if>