<h4><g:message code="text.permissions" /></h4>

<div id="aclDialogArea"><!--  area for generating dialogs --></div>

<div id="aclDetailsArea">
	 <div class="alert alert-info">
	 	<g:message code="heading.permissions" />
	</div>
	<div id="sharingPanelContainingDiv">
		<g:render template="/sharing/sharingPanel" />
	</div>
	<div id="detailsTopSection" >
		<div id="detailsToolbar" class="well btn-toolbar">
			<div id="detailsMenu">
				<div class="btn-group">
					<button type="button" id="addAclButton"
						value="addAcl"
						onclick="prepareAclDialog()"><g:message code="default.button.create.label" /></button>
					<button type="button" id="deleteAclButton"
						 value="deleteAcl"
						onclick="deleteAcl()"> <g:message code="default.button.delete.label" /> </button>
					<button type="button" id="reloadAclButton"
						value="reloadAcl"
						onclick="reloadAclTable(selectedPath)"><g:message code="default.button.reload.label" /></button>
				</div>
				<div class="btn-group">
					<button onclick="makePublicLinkAtPath()"><g:message code="text.create.public.link" /></button>
				</div>
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