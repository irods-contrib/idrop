<div id="detailsTopSection" class="box">
<div id="detailsToolbar" class="fg-toolbar ui-widget-header">
<div id="detailsMenu" class="fg-buttonset fg-buttonset-multi"
							style="float: left">
<button type="button" id="addMetadataButton" class="ui-state-default ui-corner-all"  value="addMetadata" onclick="addMetadata()")>Add Metadata</button>
<button type="button" id="updateMetadataButton" class="ui-state-default ui-corner-all"  value="updateMetadata" onclick="updateMetadata()")>Update Metadata</button>
<button type="button" id="deleteMetadataButton" class="ui-state-default ui-corner-all" value="deleteMetadata" onclick="deleteMetadata()")>Delete Metadata</button>
</div>
</div>
<g:render template="/common/panelmessages"/>

	<table cellspacing="0" cellpadding="0" border="0"
		id="metaDataDetailsTable" style="width: 100%;">
		<thead>
			<tr>
				<th></th>
				<th>Attribute</th>
				<th>Value</th>
				<th>Unit</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${metadata}" var="entry">
				<tr id="${entry.domainObjectUniqueName}">
					<td><g:checkBox name="selectedMetadata" />
					</td>
					<td>
						${entry.avuAttribute}
					</td>
					<td>
						${entry.avuValue}
					</td>
					<td>
						${entry.avuUnit}
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
	
		dataTable = lcBuildTableInPlace("#metaDataDetailsTable", null, null);	
	});

	</script>