<h4><g:message code="text.permissions" /></h4>

<div id="aclDialogArea"><!--  area for generating dialogs --></div>

<div id="aclDetailsArea">
	 <div class="alert alert-info">
	 	<g:message code="heading.permissions" />
	</div>
	<div id="detailsTopSection" >
		<div id="detailsToolbar" class="well btn-toolbar">
			<div id="detailsMenu">
				<div class="btn-group">
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

	/*
	* Cause a dialog to appear that has a link for a public path for the current path
	*/
	function makePublicLinkAtPath() {
		$("#aclDialogArea").html();
		var path = selectedPath;
		if (selectedPath == null) {
			return false;
		}

		// show the public link dialog
		var url = "/browse/preparePublicLinkDialog";
		var params = {
			absPath : path
		}

		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "", function(data) {
			fillInPublicLinkDialog(data);
		});
		
	}


	/**
	 * Close the public link dialog 
	 */
	function closePublicLinkDialog() {
		
			$("#aclDialogArea").hide("slow");
			$("#aclDialogArea").html();
			var path = $("#publicLinkDialogAbsPath").val();
			reloadAclTable(path);
			$("#aclDetailsArea").show("slow");
	}

	/**
	 * Grant public (anonymous access) via the public link dialog.  Submit dialog and present the response
	 */
	function grantPublicLink() {
		var path = $("#publicLinkDialogAbsPath").val();
		showBlockingPanel();
		if (path == null) {
			setMessage(jQuery.i18n.prop('msg.path.missing'));
			unblockPanel();		
		}
		
		var params = {
				absPath : path
			}
		
		lcSendValueViaPostAndCallbackHtmlAfterErrorCheck("/browse/updatePublicLinkDialog", params, null, "#aclDialogArea", null, null);
		unblockPanel();

	}
	        

	/*
	*Given the contents of the 'create public link' dialog, 
	*/
	function fillInPublicLinkDialog(data) {
		$("#aclDetailsArea").hide("slow");
		$("#aclDialogArea").html(data);
		$("#aclDialogArea").show("slow");
	}
	

	</script>