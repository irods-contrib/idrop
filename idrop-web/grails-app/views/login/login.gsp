<head>
<meta name="layout" content="main" />
</head>
 <g:form controller="j_spring_security_check"
	absolute="true" method="post">
	<g:if test="${request.login_error}">
		<div class="message">
		${request.login_error}
		</div>
	</g:if>
	
<div id="container" style="width:350px; margin: auto;">

	
	<g:if test="${request.presetHost}">
	<g:hiddenField
		name="host" id="host" value="${request.presetHost}"/>
		<g:hiddenField
		name="port" id="port" value="${request.presetPort}"/>
		<g:hiddenField
		name="zone" id="zone" value="${request.presetZone}"/>
   <g:hiddenField
		name="resource" id="resource" value="${request.presetResource}"/>
	</g:if>
	<g:else>
	
	
	<div>
	<div style="width:30%;"><label style="float:right;"><g:message code="text.host" />:</label></div><div><input type="text"
		class="input-text" name="host" id="host" value="${request.host}"/></div> 
	</div>
	<div>
	<div><label style="float:right;"><g:message code="text.port" />:</label></div><div><input type="text"
		class="input-text" name="port" id="port" value="${request.port}"/></div>
	</div>
	<div>
	<div><label style="float:right;"><g:message code="text.zone" />:</label></div><div><input type="text"
		class="input-text" name="zone" id="zone" value="${request.zone}"/></div>
	</div>
	<div>
	<div><label style="float:right;"><g:message code="text.resource" />:</label></div><div><input type="text"
		class="input-text" name="resource" id="resource" value="${request.resource}"/></div>
	</div>
	</g:else>
	<div>
	<div><label style="float:right;"><g:message code="text.user" />:</label></div><div><input type="text"
		class="input-text" name="user" id="user" value="${request.user}"/></div>
	</div>
	<div>
	<div><label style="float:right;"><g:message code="text.password" />:</label></div><div><input type="password" class="input-text" name="password" id="password" value="${request.password}"/></div>
	</div>
	<div>
	<div></div><div><input style="float:right;margin:15px;"
		type="submit" value="${message(code:'text.login')}" /></div>
</div>


</g:form>


