<%--

    Licensed under the GPL License. You may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      https://www.gnu.org/licenses/old-licenses/gpl-2.0.html

    THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
    WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
    PURPOSE.

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%-- "404 - Not Found" error handler. --%>

<html>

	<head><title><spring:message code="probe.jsp.notfound.title"/></title></head>

	<body>
		<div class="errorMessage">
			<p>
				<spring:message code="probe.jsp.notfound"/>
			</p>
		</div>
	</body>
</html>