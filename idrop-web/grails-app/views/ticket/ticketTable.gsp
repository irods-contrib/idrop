<g:form name="ticketDetailsForm" action="" id="ticketDetailsForm">
	<table cellspacing="0" cellpadding="0" border="0"
		id="ticketDetailsTable" style="width: 100%;">
		<thead>
			<tr>
				<th></th>
				<th><g:message code="text.ticket.user" /></th>
				<th><g:message code="text.ticket.valid" /></th>
				<th><g:message code="text.ticket.type" /></th>
				<th><g:message code="text.ticket.string" /></th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${tickets}" var="ticket">
				<tr id="${ticket.ticketString}">
					<td><g:checkBox name="selectedTicket" />
					</td>
					<td>${ticket.ownerName}</td>
					<td>valid here</td>
					<td>${ticket.type}</td>
					td>${ticket.ticketString}</td>
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
			</tr>
		</tfoot>
	</table>
	</g:form>
</div>