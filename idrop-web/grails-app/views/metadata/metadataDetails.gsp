<div id="detailsTopSection" class="box">

<div id="detailsToolbar" class="nav">
<button type="button" id="addMetadataButton" class="menuButton" value="addMetadata" onclick="addMetadata()")>Add Metadata</button>
<button type="button" id="updateMetadataButton" class="menuButton" value="updateMetadata" onclick="updateMetadata()")>Update Metadata</button>
<button type="button" id="deleteMetadataButton" class="menuButton" value="deleteMetadata" onclick="deleteMetadata()")>Delete Metadata</button>
</div>
</div>
<div>
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