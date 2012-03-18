<head>
<title>iDrop-web - iRODS Personal Cloud"</title>
<link rel="stylesheet" type="txt/css" href="${resource (dir:'css',file:'main.css')}" />
<link rel="stylesheet" type="txt/css" href="${resource (dir:'css',file:'base.css')}" />
<link rel="stylesheet" type="txt/css" href="${resource (dir:'css',file:'style.css')}" />
</head>
<body style="height:100%;">
<div id="hd"><!-- PUT MASTHEAD CODE HERE -->
<div id="bannercontainer">
	<!--  image banner -->
</div>
</div>
<div id="bd" style="height:100%;">
<div id="yui-main" style="height:100%;">
<div class="yui-b" style="height:100%;">
<div id="mainDiv" class="yui-ge" style="height:100%;">
<div id="mainDivCol1" class="yui-u first" style="height:100%;padding:20px;"><!-- PUT MAIN COLUMN 1 CODE HERE -->

 <g:form controller="j_spring_security_check"
	 method="post" style="width:560px;">
	
<div id="loginForm" class="roundedContainer" style="width: 60%;
    margin: 0px auto; ">
    <g:if test="${request.login_error}">
		<div class="message">
		${request.login_error}
		</div>
	</g:if>
	
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
	
	<label><g:message code="text.host" />:</label><input type="text"
		class="input-text" name="host" id="host" value="${request.host}"/>
	<br/>
	<label><g:message code="text.port" />:</label><input type="text"
		class="input-text" name="port" id="port" value="${request.port}"/>
	<br/>
	<label><g:message code="text.zone" />:</label><input type="text"
		class="input-text" name="zone" id="zone" value="${request.zone}"/>
	<br/>
	<label><g:message code="text.resource" />:</label><input type="text"
		class="input-text" name="resource" id="resource" value="${request.resource}"/>
	<br/>
	</g:else>
	<label><g:message code="text.user" />:</label><input type="text"
		class="input-text" name="user" id="user" value="${request.user}"/>
	<br/>
	<label><g:message code="text.password" />:</label>
		<input type="password" class="input-text" name="password" id="password" value="${request.password}"/>
	<br/>
	<input style="float:right;margin:15px;"
		type="submit" value="${message(code:'text.login')}" />
</div>
</g:form> 
</div>
</body>
</html>

