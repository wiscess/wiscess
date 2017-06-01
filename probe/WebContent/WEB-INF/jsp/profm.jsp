<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>


<html>
	<head>
		<title><spring:message code="probe.jsp.title.profm"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">    
		
		<link type="text/css" rel="stylesheet" href="<c:url value="/css/classic/profm.css"/>"/>
		<link type="text/css" rel="stylesheet" href="<c:url value="/css/classic/scroller.css"/>"/>
		<link type="text/css" rel="stylesheet" href="<c:url value="/css/classic/deploy.css"/>"/>
		
		<script type="text/javascript" language="javascript" src="<c:url value="/jquery/jquery.js"/>"></script>
		
		<script type="text/javascript" language="javascript" src="<c:url value="/js/prototype.js"/>"></script>
		<script type="text/javascript" language="javascript" src="<c:url value="/js/behaviour.js"/>"></script>
		<script type="text/javascript" language="javascript" src="<c:url value="/js/scriptaculous.js"/>"></script>
		<script type="text/javascript" language="javascript" src="<c:url value="/js/func.js"/>"></script>
		<script type="text/javascript" language="javascript" src="<c:url value="/js/profm.js"/>"></script>
	</head>
	<body>
		<c:set var="navTabProfm" value="active" scope="request"/>
		<c:if test="${! empty errorMessage}">
			<div class="errorMessage">
				<p>
					${errorMessage}
				</p>
			</div>
		</c:if>
		<c:if test="${success}">
			<div id="successMessage">
				<spring:message code="probe.jsp.profm.upload.success"></spring:message>
			</div>
		</c:if>
		<div class="profmMenu">
			<ul class="options">
				<li id="showUpload">
					<a href="#">
						<spring:message code="probe.jsp.profm.menu.showUpload"/>
					</a>
				</li>
				<li id="hideUpload" style="display: none;">
					<a href="#">
						<spring:message code="probe.jsp.profm.menu.hideUpload"/>
					</a>
				</li>
				<li id="executeCommand">
					<a href="#">
						<spring:message code="probe.jsp.profm.menu.execute"/>
					</a>
				</li>
				<li id="showHistory">
					<a href="#">
						<spring:message code="probe.jsp.profm.menu.showHistory"/>
					</a>
				</li>
				<li id="hideHistory" style="display: none;">
					<a href="#">
						<spring:message code="probe.jsp.profm.menu.hideHistory"/>
					</a>
				</li>
				<li id="showOptions">
					<a href="#">
						<spring:message code="probe.jsp.profm.menu.showOptions"/>
					</a>
				</li>
				<li id="hideOptions" style="display: none;">
					<a href="#">
						<spring:message code="probe.jsp.profm.menu.hideOptions"/>
					</a>
				</li>
				<li id="abbreviations">
					<a href="#">
						<spring:message code="probe.jsp.profm.menu.abbreviations"/>
					</a>
				</li>
			</ul>
		</div>
		<div class="profmContent">
			<div id="help" class="helpMessage" style="display: none;">
				<div class="ajax_activity"></div>
			</div>
		</div>
		<form id="executeForm" action="<c:url value='/profm/upload.htm'/>" method="post" enctype='multipart/form-data'>
		<div class="profmContent">
			<dl id="uploadDL" style="display:none;">
			<div id="deployScenario1" class="deploy">
				<span class="deployLabel"><spring:message code="probe.jsp.profm.title"/></span>
				<div class="deployDescription"><spring:message code="probe.jsp.profm.description"/></div>
					<dt><label for="file"><spring:message code="probe.jsp.profm.file.label"/> <em>*</em></label></dt>
					<dd><input id="file" type="file" name="fileItem" size="90"/>
						<input class="b" type="submit" id="doImport" value="<spring:message code='probe.jsp.profm.submit'/>"/>
					</dd>
			</div>
			</dl>
			<dl id="optionsDL" style="display:none;">
				<dt><label for="rootPath"><spring:message code='probe.jsp.profm.server.path'/></label></dt>
				<dd><input type="text" id="rootPath" name="rootPath" class="txtInput" value="${rootPath }"/></dd>
				<dt><label for="webPath"><spring:message code='probe.jsp.profm.webapps.path'/></label></dt>
				<dd><input type="text" id="webPath" name="webPath" class="txtInput" value="${webPath }"/></dd>
				<dt><label for="historySize"><spring:message code="probe.jsp.dataSourceTest.sqlForm.historySize.label"/></label></dt>
				<dd><input type="text" id="historySize" name="historySize"  class="txtInput" value="${historySize}" size="6"/></dd>
			</dl>
			<div id="commandHistoryContainer" style="display: none;">
				<h3 id="commandHistoryH3"><spring:message code="probe.jsp.profm.h3.commandHistory"/></h3>
				<div id="commandHistoryBorder">
					<div id="commandHistoryHolder"></div>
					<div id="historyDragHandle">&nbsp;</div>
				</div>
			</div>
			<dl id="resultDL" >
				<dt><label for="commandLine"><spring:message code='probe.jsp.profm.commandline'/></label></dt>
				<dd><input type="text" id="commandLine" name="commandLine" value="" size="90"/>
				<input type="button" value="<spring:message code='probe.jsp.profm.commandline.execute'/>" onclick="return executeCommand()"/>
				</dd>
			</dl>
			<dl id="resultDL">
				<dt><label for="result"><spring:message code='probe.jsp.profm.result'/></label></dt>
				<dd id="resultContainer">
					<textarea id="result" name="result" style="height:200px" rows="15" cols="80"></textarea>
					<div id="resultDragHandle">&nbsp;</div>
				</dd>
			</dl>
		</div>
		</form>

<script type="text/javascript">
setupAjaxActions(
		'<c:url value="/profm/commandExecute.ajax"/>',
		'<c:url value="/profm/commandHistory.ajax"/>');
setupShortcuts();
setupHelpToggle('<c:url value="/help/profm.ajax"/>');

new Draggable('historyDragHandle', {
	constraint: 'vertical',
	change: resizeCommandHistory,
	revert: revertDragHandle
});
new Draggable('resultDragHandle', {
	constraint: 'vertical',
	change: resizeTextArea,
	revert: revertDragHandle
});

</script>
</body>

</html>
