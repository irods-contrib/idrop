<div class="alert alert-info">
 <g:message code="heading.enter.user.or.search" />
</div>
<div id="aclDialogDiv">



				<form id="userDialogForm" name="userFormForm">
	
			<fieldset id="verticalForm">
				<g:hiddenField name="create" value="${create}" />
		
				<g:hiddenField name="absPath" value="${absPath}"  />
				<label for="acl"><g:message code="text.share.type" />:</label>
				<g:select name="acl" from="${userPermissionEnum}" />
				<br />
				<label for="userName"><g:message code="text.user" />:</label>
				<g:textField id="userName" name="userName" value="${userName}" />
				<br/>
				<button type="button" id="lookUpUserNames"
							value="lookUpUserNames"
							onclick="lookUpUserNameFromACLDialogClicked()")><g:message code="text.search" /></button>
				<br/>
				
				<br />
				
				<div id="aclUserPickList" style="display:block;position:relative;overflow:auto;">
					<!-- div for sharing pick list -->
				</div>
				<br/>
				<div id="detailsDialogToolbar" class="well">
					<div id="detailsDialogMenu"
						style="float: left, clear :   both; width: 90%;">
						<button type="button" id="updateAclDetailButton"
							value="addAcl"
							onclick="submitAclDialog()")><g:message code="text.update" /></button>
						<button type="button" id="cancelAddAclButton"
							 value="cancelAdd"
							onclick="closeAclAddDialog()")><g:message code="default.button.cancel.label" /></button>
					</div>
				</div>
			</fieldset>
</div>

<script type="text/javascript">

/**
 * Handle click on search from ACL dialog to build user list
 */
function lookUpUserNameFromACLDialogClicked() {
	 var userName = $("#userName").val();
	 searchUsers(1, userName, "#aclUserPickList");
}

</script>