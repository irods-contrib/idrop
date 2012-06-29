
	<table cellspacing="0" cellpadding="0" border="0"
		id="auditDetailsTable" style="width: 100%;">
		<thead>
			<tr>
				 <th></th>
				<th><g:message code="text.action" /></th>
				<th><g:message code="text.user" /></th>
				<th><g:message code="text.timestamp" /></th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${auditedActions}" var="entry">
				<tr id="${entry.objectId}">
					  <td><span
                  class="ui-icon-circle-plus browse_detail_icon ui-icon"></span></td>
					<td>${entry.auditActionEnum.textValue}</td>
					<td>${entry.userName}</td>
					<td>${entry.createdAt}</td>
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
	