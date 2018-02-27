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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="https://github.com/psi-probe/psi-probe/jsp/tags" prefix="probe" %>

<%-- Displays various system information including System.properties. This page helps to evaluate
 the environment Tomcat is running in. --%>

<html>
	<head>
		<title><spring:message code="probe.jsp.title.sysinfo"/></title>
	</head>

	<body>

		<c:set var="navTabSystem" value="active" scope="request"/>
		<c:set var="systemTabOverview" value="active" scope="request"/>
		<c:set var="use_decorator" value="system" scope="request"/>

		<div id="memoryInfo">
			<h3><spring:message code="probe.jsp.sysinfo.memory.title"/></h3>
			<c:set var="memUsed"
					value="${(systemInformation.totalMemory - systemInformation.freeMemory) * 100 / systemInformation.maxMemory}"/>

			<table style="border-spacing:0;border-collapse:separate;">
				<tr>
					<td><span class="name"><spring:message code="probe.jsp.sysinfo.memory.usage"/></span>&#160;</td>
					<td style="padding:0; margin:0">
						<probe:score value="${memUsed}" partialBlocks="5" fullBlocks="10" showEmptyBlocks="true" showA="true" showB="true">
							<img src="<c:url value='/css/classic/gifs/rb_{0}.gif'/>" alt="+"
								title="<spring:message code='probe.jsp.sysinfo.memory.usage.alt'/>"/>
						</probe:score>
					</td>
					<td>&#160;<fmt:formatNumber maxFractionDigits="1" value="${memUsed}"/>%</td>
					<td>&#160;<a href="<c:url value='/adm/advisegc.htm'/>"><spring:message code="probe.jsp.sysinfo.memory.adviseGC"/></a></td>
				</tr>
			</table>
			<span class="name"><spring:message code="probe.jsp.sysinfo.memory.free"/></span>&#160;<probe:volume value="${systemInformation.freeMemory}" fractions="2"/>
			<span class="name"><spring:message code="probe.jsp.sysinfo.memory.total"/></span>&#160;<probe:volume value="${systemInformation.totalMemory}" fractions="2"/>
			<span class="name"><spring:message code="probe.jsp.sysinfo.memory.max"/></span>&#160;<probe:volume value="${systemInformation.maxMemory}" fractions="2"/>
		</div>

		<div id="osInformation">
			<h3><spring:message code="probe.jsp.sysinfo.os.title"/></h3>
			<span class="name"><spring:message code="probe.jsp.sysinfo.os.jvm"/></span>&#160;<a href="${systemInformation.systemProperties['java.vendor.url']}" target="_blank">
				${systemInformation.systemProperties['java.runtime.name'] }&#160;
				${systemInformation.systemProperties['java.runtime.version'] }&#160;
				${systemInformation.systemProperties['java.vm.name'] })
			</a>
			<br/>
			<span class="name"><spring:message code="probe.jsp.sysinfo.os.name"/></span>&#160;${systemInformation.systemProperties['os.name'] }
			(${systemInformation.systemProperties['sun.os.patch.level'] })
			${systemInformation.systemProperties['os.arch'] }
			${systemInformation.systemProperties['os.version'] }<br/>
			<span class="name"><spring:message code="probe.jsp.sysinfo.os.processors"/></span>&#160;${systemInformation.cpuCount }<br/>
			<span class="name"><spring:message code="probe.jsp.sysinfo.os.currentTime"/></span>&#160;${systemInformation.date}<br/>
			<span class="name"><spring:message code="probe.jsp.sysinfo.os.workingDir"/></span>&#160;${systemInformation.workingDir}<br/>
		</div>

		<div>
			<h3><spring:message code="probe.jsp.sysinfo.container.title"/></h3>
			<span class="name"><spring:message code="probe.jsp.sysinfo.os.container"/></span>&#160;${systemInformation.serverInfo}<br/>
			<span class="name"><spring:message code="probe.jsp.sysinfo.os.catalinaBase"/></span>&#160;${systemInformation.systemProperties['catalina.base'] }<br/>
			<span class="name"><spring:message code="probe.jsp.sysinfo.os.catalinaHome"/></span>&#160;${systemInformation.systemProperties['catalina.home'] }<br/>
			<span class="name"><spring:message code="probe.jsp.sysinfo.os.applicationBase"/></span>&#160;${systemInformation.appBase}<br/>
			<span class="name"><spring:message code="probe.jsp.sysinfo.os.configBase"/></span>&#160;${systemInformation.configBase}<br/>

		</div>
	</body>

</html>
