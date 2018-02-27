<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ taglib uri="http://acegisecurity.org/authz" prefix="authz"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="x-ua-compatible" content="ie=7" /> 
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>微信菜单管理</title>
<script type="text/javascript" src="${ctx }/jquery/jquery-1.7.1.min.js"></script>
<script src="${ctx}/js/searchpanel.js" type="text/javascript"></script>
<script src="${ctx}/js/validata.js" type="text/javascript"></script>
<script src="${ctx}/js/func.js" type="text/javascript"></script>
<link href="${ctx}/css/szhxy.css" rel="stylesheet" type="text/css"/>
<script src="${ctx}/js/cover.js" type="text/javascript"></script>

<script type="text/javascript">
function delMethod(obj){
	if(confirm('确定删除吗？')){
		var oldAction = document.getElementById("frm").action;
		document.getElementById("frm").action = "${ctx}/system/WxMenuAction.a?doDel&menuId="+obj;
		document.getElementById("frm").submit();
		document.getElementById("frm").action = oldAction;
	}
}

function enabledMethod(obj,e){
	var oldAction = document.getElementById("frm").action;
	document.getElementById("frm").action = "${ctx}/system/WxMenuAction.a?"+obj+"&menuId="+e;
	document.getElementById("frm").submit();
	document.getElementById("frm").action = oldAction;
}


function checkForm(){
	var fields=[
		{id:'menuName',	text:'功能名称',allowBlank:false},
		{id:'menuUrl',	text:'功能URL',allowBlank:false},
		{id:'menuOrder',	text:'功能序号',type:'int',allowBlank:false}
	];
	
	var flag=checkFields(fields);
	if(!flag){
		return false;
	}
	
	return true;
}

</script>

</head>

<body>
<form action="${ctx}/system/WxMenuAction.a" method="post" id="frm" name="frm">

<div id="main">
	<div id="titlearea">
	<h1>当前操作：</h1>
	<h2>微信菜单管理</h2>
	<c:if test="${message!=null}">
		<div id="successmsg">${message}</div>
	</c:if> <c:if test="${error!=null}">
		<div id="errormsg">${error}</div>
	</c:if>
	</div>
</div>
<div style="display: none" id="searchclosearea">
<div style="display: none" id="searchpanelleft"></div>
<div style="display: none" id="searchpanelopenbtn"
	onclick="showsearchpanel(event);" onmouseover="showopeninfo();"
	onmouseout="hideopeninfo();">
<div onclick="showsearchpanel(event);" id="searchpanelopeninfo"
	style="display: none"></div>
</div>
</div>
<div id="searcharea"><input name="hidepanelflag"
	id="hidepanelflag" type="hidden" value="" />
<div id="searchpanelclosebtn" onclick="hidesearchpanel(event);"
	onmouseover="showcloseinfo();" onmouseout="hidecloseinfo();">
<div onclick="hidesearchpanel(event);" id="searchpanelcloseinfo"
	style="display: none;"></div>
</div>
</div>
<div id="resulttitlearea"><span>微信菜单列表</span></div>
  <div id="resultlistarea">
    <table width="100%" border="0" cellpadding="0" cellspacing="1">
      <tr>
        <th width="20%">功能名称</th>
        <th >功能路径</th>
        <th width="8%">功能排序</th>
        <th width="8%">菜单类型</th>
        <th width="8%">是否授权</th>
        <th width="8%">是否启用</th>
        <th width="20%">操作</th>
      </tr>
	<c:forEach items="${wxMenus}" var="obj" varStatus="i">
		<tr class="even">
			<td>${obj.name}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>${obj.url}</td>
			<td>${obj.order}</td>
			<td></td>
			<td>${objs.isAuth?'<div style="color: green">是</div>':'<div style="color: red">否</div>'}</td>
			<td> ${obj.isUsed?'<div style="color: green">是</div>':'<div style="color: red">否</div>'}</td>
			<td><a href="javascript:void(0);" onclick="delMethod('${obj.id}')" >[ 删 除 ]</a>&nbsp;
			<a href="javascript:void(0);" onclick="enabledMethod('doEnabled','${obj.id}')" >[ 启 用 ]</a>&nbsp;
			<a href="javascript:void(0);" onclick="enabledMethod('doCancelEnabled','${obj.id}')" >[ 禁 用 ]</a></td>
		</tr>
		<c:if test="${not empty obj.funs}">
			<c:forEach items="${obj.funs}" var="objs"  >
				<tr class="odd">
					<td>>> ${objs.name}</td>
					<td>${objs.url}</td>
					<td>${objs.order}</td>
					<td><app:dictname dictid="${objs.menuType}"/></td>
					<td>${objs.isAuth?'<div style="color: green">是</div>':'<div style="color: red">否</div>'}</td>
					<td>${objs.isUsed?'<div style="color: green">是</div>':'<div style="color: red">否</div>'}</td>
					<td><a href="javascript:void(0);" onclick="delMethod('${objs.id}')" >[ 删 除 ]</a>&nbsp;
					<a href="javascript:void(0);" onclick="enabledMethod('doEnabled','${objs.id}')" >[ 启 用 ]</a>&nbsp;
					<a href="javascript:void(0);" onclick="enabledMethod('doCancelEnabled','${objs.id}')" >[ 禁 用 ]</a></td>
				</tr>
			</c:forEach>
		</c:if>
	</c:forEach>
</table>
</div>

<div id="operationarea">
<div id="operationcontent">
	<input name="button" type="button" value=" 增 加 " onClick="showcover(1);" class="btnop_mouseout"/>
	<input name="doGenerate" type="submit" value=" 生成微信菜单 " class="btnop_mouseout"/>
</div>
</div>
<div id="bottomarea">
<div id="bottomarea_l"></div>
<div id="bottomarea_r"></div>
</div>
</div>

<div id="cover" style="display:none;"><iframe src="" frameborder="0" style="position:absolute; visibility:inherit; top:0px; left:0px; height:expression(eval(document.body.clientHeight)); width:100%; z-index:-1; FILTER: alpha(opacity=0); "> </iframe></div>
<div id="covercontainer" style="display:none;">
	<div id="covercontent1" style="display:none;width:500px;margin-top:-220px;background-color:white;">
		<div id="resulttitlearea"><span>微信菜单编辑</span></div>
	  	<div id="formarea"> 
	      	<table style="width:495px;" border="0" cellspacing="1" cellpadding="0">
		      <tr>
		        <td width="150px"  align="right">一级菜单：</td>
		        <td align="left">
					<select name="parentMenuId" id="parentMenuId">
						<c:if test="${oneMenuCnt ne 3}">
							<option value="0">--无--</option>
						</c:if>
						<c:forEach items="${wxMenus}" var="obj" >
							<option value="${obj.id}">${obj.name}</option>
						</c:forEach>
					</select>
				</td>
			  </tr>
			  <tr>
				<td align="right">功能名称：</td>
		        <td align="left">
					<input type="text" name="menuName" id="menuName" size="50"/>
				</td>
			</tr>
			<tr>
			<td align="right">事件类型：</td>
	        <td align="left">
				<select name="menuType">
					<app:dictselect dictType="1090" />
				</select>
			</td>
		  </tr>
			  <tr>
				<td align="right">功能Url：</td>
		        <td align="left">
					<input type="text" name="menuUrl" id="menuUrl" size="50" />
				</td>
			</tr>
			  <tr>
				<td align="right">功能序号：</td>
		        <td align="left">
					<input type="text" name="menuOrder" id="menuOrder" size="8" maxlength="8" />
				</td>
			</tr>
			  <tr>
				<td align="right">网页授权：</td>
		        <td align="left">
					<input type="radio" name="isAuth" value="1" checked />是&nbsp;<input type="radio" name="isAuth" value="0" />否
				</td>
			</tr>
			  <tr>
				<td align="right">是否启用：</td>
		        <td align="left">
					<input type="radio" name="isUsed" value="1" checked />是&nbsp;<input type="radio" name="isUsed" value="0" />否
				</td>
		      </tr>
	    	</table>
	  	</div>
	   	<div id="operationarea"> 
		  <div id="operationcontent">
		  	<input name="doAdd" id="doAdd" type="submit" value=" 确定 " onclick="return checkForm();" class=btnop_mouseout onmouseover="this.className='btnop_mouseover'" onmouseout="this.className='btnop_mouseout'">
			<input name="Submit" type="button" value=" 取消 " onClick="hidecover(1);" class=btnop_mouseout onmouseover="this.className='btnop_mouseover'" onmouseout="this.className='btnop_mouseout'">
		  </div>
	  	</div>
	</div>
</div>
</form>
</body>
</html>