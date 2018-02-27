<%--

    Licensed under the GPL License. You may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      https://www.gnu.org/licenses/old-licenses/gpl-2.0.html

    THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
    WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
    PURPOSE.

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<p>
	<em><spring:message code="probe.jsp.applications.col.sessionCount"/></em> <spring:message code="probe.jsp.applications.help.sessionCount"/>,
	<em><spring:message code="probe.jsp.applications.col.sessionAttributeCount"/></em> <spring:message code="probe.jsp.applications.help.sessionAttributeCount"/>,
	<em><spring:message code="probe.jsp.applications.col.contextAttributeCount"/></em> <spring:message code="probe.jsp.applications.help.contextAttributeCount"/>,
	<em><spring:message code="probe.jsp.applications.col.distributable"/></em> <spring:message code="probe.jsp.applications.help.distributable"/>,
	<em><spring:message code="probe.jsp.applications.col.serializable"/></em> <spring:message code="probe.jsp.applications.help.serializable"/>,
	<em><spring:message code="probe.jsp.applications.col.requestCount"/></em> <spring:message code="probe.jsp.applications.help.requestCount"/>
</p>