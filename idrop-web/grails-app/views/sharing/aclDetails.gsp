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

<div>
	<g:form name="aclDetailsForm" action="" id="aclDetailsForm">
	<table cellspacing="0" cellpadding="0" border="0" id="aclDetailsTable"
		style="width: 100%;">
		<thead>
			<tr>
				<th></th>
				<th>Person</th>
				<th>Access</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${acls}" var="acl">
				<tr id="${acl.userName}">
					<td><g:checkBox name="selectedAcl" value="${acl.userName}" checked="false"/>
					</td>
					<td>
						${acl.userName}
					</td>
					<td class="forSharePermission" id="${acl.userName}">
						${acl.filePermissionEnum}
					</td>

				</tr>
			</g:each>

		</tbody>

		<tfoot>
			<tr>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
			</tr>
		</tfoot>
	</table>
	</g:form>
</div>
<script type="text/javascript">

	var messageAreaSelector="#aclMessageArea";
	
	$(function() {
		lcPrepareForCall();
		dataTable = lcBuildTableInPlace("#aclDetailsTable", null, null);	

		$('.forSharePermission', dataTable.fnGetNodes()).editable(function(value, settings) {
			var userName = this.parentNode.getAttribute('id');
			return aclUpdate(value,settings, userName);}, {
			"callback": function( sValue, y ) {
				var aPos = dataTable.fnGetPosition( this );
				dataTable.fnUpdate( sValue, aPos[0], aPos[1] );
			},
			'data': "{'OWN':'OWN','READ':'READ','WRITE':'WRITE'}",
			'type': 'select',
			'submit': 'OK',
			'cancel': 'Cancel',
			'indicator': 'Saving'
		} );
		
	});

	</script>