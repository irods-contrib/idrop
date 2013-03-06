<g:form name="metadataDetailsForm" action="" id="metadataDetailsForm">
	<table cellspacing="0" cellpadding="0" border="0"
		id="metaDataDetailsTable" class="table table-striped table-hover"  style="width: 100%;">
		<thead>
			<tr>
				<th></th>
				<th><g:message code="text.attribute" /></th>
				<th><g:message code="text.value" /></th>
				<th><g:message code="text.unit" /></th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${metadata}" var="entry">
				<tr id="${entry.domainObjectUniqueName}">
					<td><g:checkBox name="selectedMetadata" />
					</td>
					<td class="editable avuAttribute">${entry.avuAttribute}</td>
					<td class="editable avuValue">${entry.avuValue}</td>
					<td class="editable avuUnit">${entry.avuUnit}</td>
				</tr>
			</g:each>
		</tbody>
		<tfoot>
			<tr>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</tfoot>
	</table>
	</g:form>
</div>