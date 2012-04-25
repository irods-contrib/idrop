<g:form name="ticketDetailsForm" action="" id="ticketDetailsForm">
	<table cellspacing="0" cellpadding="0" border="0"
		id="ticketDetailsTable" style="width: 100%;">
		<thead>
			<tr>
				<th></th>
				<th><g:message code="text.ticket.string" /></th>
				<th><g:message code="text.ticket.user" /></th>
			
				<th><g:message code="text.ticket.type" /></th>
				
			</tr>
		</thead>
		<tbody>
			<g:each in="${tickets}" var="ticket">
				<tr id="${ticket.ticketString}">
					<td><g:checkBox name="selectedTicket" />
					</td>
					<td>${ticket.ticketString}</td>
					<td>${ticket.ownerName}</td>
					
					<td>${ticket.type}</td>
					
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