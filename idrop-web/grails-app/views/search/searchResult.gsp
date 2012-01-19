<table cellspacing="0" cellpadding="0" border="0" id="searchResultTable"
	class="display" style="width: 90%;height:90%;overflow:auto;">
	<thead>
		<tr>
			<th></th>
			<th>Name</th>
			<th>Absolute path <i>Click to see in tree</i></th>
			<th>Type</th>
			<th>Modified date</th>
			<th>Length</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${results}" var="entry">
			<tr id="${entry.formattedAbsolutePath}">
				<td><div class="ui-icon-circle-plus search-detail-icon ui-icon " /></td>
				<td><g:if test="${entry.objectType.toString() == 'COLLECTION'}">${entry.nodeLabelDisplayValue}</g:if><g:else><g:link url="${'file/download' + entry.formattedAbsolutePath}">${entry.nodeLabelDisplayValue}</g:link>
				</g:else></td>
				<td><span id="${entry.formattedAbsolutePath}" onclick="clickOnPathInSearchResult(this.id)">${entry.formattedAbsolutePath}<span></td>
				<td>${entry.objectType}</td>
				<td>${entry.modifiedAt}</td>
				<td>${entry.dataSize}</td>
			</tr>
		</g:each>

	</tbody>

	<tfoot>
		<tr>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
	</tfoot>
</table>

<script type="text/javascript">
function clickOnPathInSearchResult(data) {
	if (data == null) {
		throw new Exception("no absolute path provided");
	}
	// show main browse tab
	  $(tabs).tabs('select', 0); // switch to home tab
	  splitPathAndPerformOperationAtGivenTreePath(data, null,
				null, function(path, dataTree, currentNode){

		  $.jstree._reference(dataTree).open_node(currentNode);
		  $.jstree._reference(dataTree).select_node(currentNode, true);
		 // updateBrowseDetailsForPathBasedOnCurrentModel(data);

			});
}

</script>