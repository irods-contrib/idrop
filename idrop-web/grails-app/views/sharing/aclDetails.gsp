<g:render template="/common/panelmessages" />
<div id="detailsToolbar" class="fg-toolbar ui-widget-header">
	<div id="detailsMenu" class="fg-buttonset fg-buttonset-multi"
		style="float: left">
		<button type="button" id="addAclButton"
			class="ui-state-default ui-corner-all" value="addAcl"
			onclick="addAcl()")>Add Share</button>
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

<div>
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
					<td><g:checkBox name="selectedAcl" />
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
</div>
<script type="text/javascript">


	var messageAreaSelector="#aclMessageArea";
	
	$(function() {
		dataTable = lcBuildTableInPlace("#aclDetailsTable", null, null);	

		$('.forSharePermission', dataTable.fnGetNodes()).editable(function(value, settings) {
			var userName = this.parentNode.getAttribute('id');
			return aclUpdate(value,settings, userName);}, {
			"callback": function( sValue, y ) {
				setMessageInArea(messageAreaSelector, "File sharing update successful");

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


// FIXME: diff div for loading gif
	
	/**
	* Called by data table upon submit of an acl change 
	*/
	function aclUpdate(value, settings, userName) {
		//lcShowBusyIconInDiv("#aclMessageArea");
		var url = '/sharing/updateAcl';
		//var aPos = dataTable.fnGetPosition( this );
		//alert("apos =" + aPos);
		if (selectedPath == null) {
			throw "no collection or data object selected";
		}

		lcShowBusyIconInDiv(messageAreaSelector);
		
		var params = {
			absPath : selectedPath,
			acl : value,
			userName: userName
		}

		 var jqxhr =  $.post(context + url, params, function(data, status, xhr) {
			 lcClearDivAndDivClass(messageAreaSelector);
		
			}, "html").error(function() {
			setMessageInArea(messageAreaSelector, "Error sharing file");
			//alert("in error, this is :" + this.html());
		});
	
		return value;
		
	}

	</script>