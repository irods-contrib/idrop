<g:render template="/common/panelmessages" />
<div id="detailsToolbar" class="fg-toolbar ui-widget-header">
	<div id="detailsMenu" class="fg-buttonset fg-buttonset-multi"
		style="float: left, clear: both;">
		<button type="button" id="addAclButton"
			class="ui-state-default ui-corner-all" value="addAcl"
			onclick="prepareAclDialog()")>Add Share</button>
		<button type="button" id="updateAclButton"
			class="ui-state-default ui-corner-all" value="updateAcl"
			onclick="updateAcl()")>Update Share</button>
		<button type="button" id="deleteAclButton"
			class="ui-state-default ui-corner-all" value="deleteAcl"
			onclick="deleteAcl()")>Delete Share</button>
	</div>
</div>
<div id="aclMessageArea">
	<!--  -->
</div>

<div id="aclDialogArea">
<!--  area for generating dialogs --></div>

<div id="aclTableDiv">
</div>
<script type="text/javascript">

	var messageAreaSelector="#aclMessageArea";
	
	$(function() {
		reloadAclTable(selectedPath);
	});

	</script>