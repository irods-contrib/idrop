<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/profile" />
</head>
<div class="row-fluid">
	<div class="span5 offset2">
		<h4><g:message code="text.change.password.header" /></h4>
	</div>
</div>
<div class="row-fluid">
	<div class="span5 offset2">
	<g:form class="form-horizontal" controller="login" action="changePassword">
	
	 <g:if test="${flash.message}">
        <div class="alert alert-success" >${flash.message}</div>
      </g:if>
	
	
		<g:hasErrors bean="${password}">
					<div  class="alert alert-error">
				  <ul>
				   <g:eachError var="err" bean="${password}">
				       <li><g:message error="${err}" /></li>
				   </g:eachError>
				  </ul>
				  </div>
		</g:hasErrors>
	<div id="container">
				<div class="control-group">
				<label class="control-label"><g:message code="text.password"/></label>
					<div  class="controls"><g:passwordField name="password" value="${password.password}" /></div>
				</div>
				<div class="control-group">
					<label class="control-label"><g:message code="text.confirm.password"/></label>			
					<div  class="controls"><g:passwordField name="confirmPassword" value="${password.confirmPassword}" /></div>
				</div>				
				<div class="control-group">
					<div></div>
					<div><button id="changePassword" value="changePassword" type="submit" ><g:message code="text.update"/></button>
					</div>
				</div>
	</div>
	</g:form>
    </div>
</div>