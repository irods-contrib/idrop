<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>iDrop-web - iRODS Personal Cloud"</title>
<link rel="stylesheet" type="text/css" href="${resource (dir:'css',file:'main.css')}" />
<link rel="stylesheet" type="text/css" href="${resource (dir:'css',file:'base.css')}" />
<link rel="stylesheet" type="text/css" href="${resource (dir:'css',file:'style.css')}" />
</head>
<body style="height:100%;">

<div id="bannercontainer">
	<!--  image banner -->
</div>

 <g:form controller="j_spring_security_check"
	 method="post" style="width:560px;">
	
<div id="loginForm" class="roundedContainer" style="width: 60%;
    margin: 0px auto; ">
    <g:if test="${request.login_error}">
		<div class="message">
		${request.login_error}
		</div>
	</g:if>
	
	
	<div id="container" style="height:100%;width:100%;">
		<g:if test="${presetHost}">
	
			<g:hiddenField
				name="host" id="host" value="${presetHost}"/>
				<g:hiddenField
				name="port" id="port" value="${presetPort}"/>
				<g:hiddenField
				name="zone" id="zone" value="${presetZone}"/>
		   <g:hiddenField
				name="resource" id="resource" value="${presetResource}"/>
	</g:if>
	<g:else>
	
		<div>
			<div><label><g:message code="text.host" />:</label></div>
			<div><input type="text" class="input-text" name="host" id="host" value="${request.host}"/></div>
		</div>
		<div>
			<div><label><g:message code="text.port" />:</label></div>
			<div><input type="text" class="input-text" name="port" id="port" value="${request.port}"/></div>
		</div>
		<div>
			<div><label><g:message code="text.zone" />:</label></div>
			<div><input type="text" class="input-text" name="zone" id="zone" value="${request.zone}"/></div>
		</div>
		<div>
			<div><label><g:message code="text.resource" />:</label></div>
			<div><input type="text" class="input-text" name="resource" id="resource" value="${request.resource}"/></div>
		</div>

	</g:else>
	<div style="width:100%;">
		<div><label><g:message code="text.user" />:</label></div>
		<div><input type="text" class="input-text" name="user" id="user" value="${request.user}"/></div>
	</div>
	<div>
		<div><label><g:message code="text.password" />:</label></div>
		<div><input type="password" class="input-text" name="password" id="password" value="${request.password}"/></div>
	</div>

	<button type="submit" id="login" name="login" style="float:right;margin:10px;"><g:message code="text.login"/></button>
	
</div>
</g:form> 
</body>
</html>

