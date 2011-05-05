<div id="aclDialogDiv">
<div id="aclDialogMessageArea">
	<!--  message area for acl dialogs -->
</div>
	<div style="clear: both;">
		<g:form controller="sharing" action="updateAcl" method="post"
			name="aclUpdateForm">
			<fieldset id="verticalForm">
				<label for="absPath">Path:</label>
				<g:textField name="absPath" value="${absPath}" readonly="true" />
				<br /> <label for="userName">User Name:</label>
				<g:textField id="userName" name="userName" value="${userName}" />
				<br /> <label for="acl">Share Type:</label>
				<g:select name="acl" from="${userPermissionEnum}" />
				<br />
				
			</fieldset>
		</g:form>
	</div>
</div>
