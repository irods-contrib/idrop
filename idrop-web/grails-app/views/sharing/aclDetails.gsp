
<div id="detailsToolbar" class="fg-toolbar ui-widget-header">
<div id="detailsMenu" class="fg-buttonset fg-buttonset-multi"
							style="float: left">
<button type="button" id="addAclButton" class="ui-state-default ui-corner-all"  value="addAcl" onclick="addAcl()")>Add Share</button>
<button type="button" id="updateAclButton" class="ui-state-default ui-corner-all"  value="updateAcl" onclick="updateAcl()")>Update Share</button>
<button type="button" id="deleteAclButton" class="ui-state-default ui-corner-all" value="deleteAcl" onclick="deleteAcl()")>Delete Share</button>
</div>
</div>
<g:render template="/common/panelmessages"/>

<div>
	<table cellspacing="0" cellpadding="0" border="0"
		id="aclDetailsTable" style="width: 100%;">
		<thead>
			<tr>
				<th></th>
				<th>Person</th>
				<th>Access</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${acls}" var="acl">
				<tr id="${acl.userId}">
					<td><g:checkBox name="selectedAcl" />
					</td>
					<td>
						${acl.userName}
					</td>
					<td>
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
</div>
<script type="text/javascript">

	$(function() {
		dataTable = lcBuildTableInPlace("#aclDetailsTable", null, null);	
	});

	</script>