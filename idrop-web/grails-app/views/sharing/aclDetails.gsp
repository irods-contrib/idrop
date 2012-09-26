<h2><g:message code="heading.sharing" /></h2>
<div id="aclDialogArea"><!--  area for generating dialogs --></div>

<div id="aclDetailsArea">
	<div id="detailsTopSection" >
		<div id="detailsToolbar" class="well">
			<div id="detailsMenu" 
				style="float: left, clear: both;">
				<button type="button" id="addAclButton"
					value="addAcl"
					onclick="prepareAclDialog()")><g:message code="default.button.create.label" /></button>
				<button type="button" id="deleteAclButton"
					 value="deleteAcl"
					onclick="deleteAcl()")><g:message code="default.button.delete.label" /></button>
				<button type="button" id="reloadAclButton"
					value="reloadAcl"
					onclick="reloadAclTable(selectedPath)")><g:message code="default.button.reload.label" /></button>
			</div>
		</div>
	</div>
	<g:hiddenField name='aclDetailsAbsPath' id='aclDetailsAbsPath' value='${absPath}'/>
	<div id="aclTableDiv"><!-- acl user list --></div>
</div>

<script type="text/javascript">

	var messageAreaSelector="#aclMessageArea";
	
	$(function() {
		var path = $("#aclDetailsAbsPath").val();
		if (path == null) {
			path = baseAbsPath;
		}
		reloadAclTable(path);
	});

	</script>