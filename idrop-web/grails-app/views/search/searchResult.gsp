<table cellspacing="0" cellpadding="0" border="0" id="searchResultTable"
	class="display" style="width: 100%;">
	<thead>
		<tr>
			<th></th>
			<th>Name</th>
			<th>Absolute path</th>
			<th>Type</th>
			<th>Modified date</th>
			<th>Length</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${results}" var="entry">
			<tr id="${entry.formattedAbsolutePath}">
				<td><div class="ui-icon-circle-plus user_detail_icon ui-icon " />
				</td>
				<td><g:if test="${entry.objectType.toString() == 'COLLECTION'}">
						${entry.nodeLabelDisplayValue}
					</g:if> <g:else>

						<g:link url="${'file/download' + entry.formattedAbsolutePath}">
							${entry.nodeLabelDisplayValue}
						</g:link>
					</g:else></td>

				<td>
					${entry.formattedAbsolutePath}
				</td>
				<td>
					${entry.objectType}
				</td>
				<td>
					${entry.modifiedAt}
				</td>
				<td>
					${entry.dataSize}
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
			<th></th>
			<th></th>
		</tr>
	</tfoot>
</table>