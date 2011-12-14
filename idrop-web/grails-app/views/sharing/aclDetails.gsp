<h2><g:message code="heading.sharing" /></h2>
<g:render template="/common/panelmessages" />
<div id="detailsTopSection" class="box">
<div id="detailsToolbar" class="fg-toolbar ui-widget-header">
	<div id="detailsMenu" class="fg-buttonset fg-buttonset-multi"
		style="float: left, clear: both;">
		<button type="button" id="addAclButton"
			class="ui-state-default ui-corner-all" value="addAcl"
			onclick="prepareAclDialog()")><g:message code="default.button.create.label" /></button>
		<button type="button" id="deleteAclButton"
			class="ui-state-default ui-corner-all" value="deleteAcl"
			onclick="deleteAcl()")><g:message code="default.button.delete.label" /></button>
			<button type="button" id="reloadAclButton"
			class="ui-state-default ui-corner-all" value="reloadAcl"
			onclick="reloadAclTable(selectedPath)")><g:message code="default.button.reload.label" /></button>
	</div>
</div>
<div id="aclMessageArea">
	<!--  area for general messages regarding acls-->
</div>

<div id="aclDialogArea">
<!--  area for generating dialogs --></div>

<div id="aclTableDiv">
</div>
</div>
<script type="text/javascript">

	var messageAreaSelector="#aclMessageArea";
	
	$(function() {
		reloadAclTable(selectedPath);
	});

	</script>