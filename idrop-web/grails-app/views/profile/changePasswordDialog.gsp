<div id="changePasswordDialog" class="roundedContainer">
	<h1><g:message code="text.change.password.header" /></h1>
	<g:form name="changePasswordForm" id="changePasswordForm">
	
	<g:if test="${flash.error}">
                <script>
                $(function() { setErrorMessage("${flash.error}"); });
                </script>
              </g:if>

		<g:hasErrors bean="${password}">
					<div class="errors">
				  <ul>
				   <g:eachError var="err" bean="${password}">
				       <li><g:message error="${err}" /></li>
				   </g:eachError>
				  </ul>
				  </div>
		</g:hasErrors>
	<div id="container" style="height:100%;width:100%;">
				<div>
					<div style="width:15%;"><label><g:message code="text.password"/></label></div>
					<div><g:passwordField name="password" value="${password.password}" /></div>
				</div>
				<div>
					<div style="width:15%;"><label><g:message code="text.confirm.password"/></label></div>					
					<div><g:passwordField name="confirmPassword" value="${password.confirmPassword}" /></div>
				</div>				
				<div>
					<div></div>
					<div><button type="button"  id="changePassword" value="changePassword" onclick="submitChangePassword()"><g:message code="text.update"/></button>
					<button type="button"  id="cancelChangePassword" value="cancelChangePassword" onclick="closePasswordDialog()"><g:message code="text.cancel"/></button>
					</div>
				</div>
	</div>
	</g:form>
</div>
