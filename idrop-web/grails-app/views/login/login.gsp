<head>
<meta name="layout" content="main" />
</head>
 
<div class="box" style="width:300px; margin:0px auto;"><g:form controller="j_spring_security_check"
	absolute="true" method="post">

	<g:if test="${request.login_error}">
		<div class="message">
		${request.login_error}
		</div>
	</g:if>

	<label> <span><g:message code="text.host" />:</span> <input type="text"
		class="input-text" name="host" id="host" value="${request.host}"/> </label>

	<label> <span><g:message code="text.port" />:</span> <input type="text"
		class="input-text" name="port" id="port" value="${request.port}"/> </label>

	<label> <span><g:message code="text.zone" />:</span> <input type="text"
		class="input-text" name="zone" id="zone" value="${request.zone}"/> </label>

	<label> <span><g:message code="text.resource" />:</span> <input type="text"
		class="input-text" name="resource" id="resource" value="${request.resource}"/> </label>

	<label> <span><g:message code="text.user" />:</span> <input type="text"
		class="input-text" name="user" id="user" value="${request.user}"/> </label>

	<label><span><g:message code="text.password" />:</span> <input type="password"
		class="input-text" name="password" id="password" value="${request.password}"/> </label>

	<div class="buttons"><span class="button"> <input
		type="submit" value="${message(code:'text.login')}" /> </span></div>

</g:form></div>

<div></div>

