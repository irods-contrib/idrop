<head>
<meta name="layout" content="main" />
</head>
<g:renderErrors as="list"></g:renderErrors>

<div class="box" style="width:300px; margin:0px auto;"><g:form controller="j_spring_security_check"
	absolute="true" method="post">

	<g:if test="${request.login_error}">
		<div class="message">
		${request.login_error}
		</div>
	</g:if>

	<label> <span>Host:</span> <input type="text"
		class="input-text" name="host" id="host" value="${request.host}"/> </label>

	<label> <span>Port:</span> <input type="text"
		class="input-text" name="port" id="port" value="${request.port}"/> </label>

	<label> <span>Zone:</span> <input type="text"
		class="input-text" name="zone" id="zone" value="${request.zone}"/> </label>

	<label> <span>Resource:</span> <input type="text"
		class="input-text" name="resource" id="resource" value="${request.resource}"/> </label>

	<label> <span>User name:</span> <input type="text"
		class="input-text" name="user" id="user" value="${request.user}"/> </label>

	<label><span>Password:</span> <input type="password"
		class="input-text" name="password" id="password" value="${request.password}"/> </label>

	<div class="buttons"><span class="button"> <input
		type="submit" value="Login" /> </span></div>

</g:form></div>

<div></div>

