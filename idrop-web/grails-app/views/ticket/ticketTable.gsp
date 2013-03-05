<g:form name="ticketTableForm" action="" id="ticketTableForm">
	<table cellspacing="0" cellpadding="0" border="0"
		id="ticketDetailsTable" style="width: 100%;" class="table table-striped table-hover" >
		<thead>       
			<tr>
				<th></th>
				<th></th>
				<th><g:message code="text.ticket.string" /></th>
				<th><g:message code="text.ticket.user" /></th>
			
				<th><g:message code="text.ticket.type" /></th>
				
			</tr>
		</thead>
		<tbody>
			<g:each in="${tickets}" var="ticket">
				<tr id="${ticket.ticketString}">
					 <td><span
                  class="ui-icon-circle-plus browse_detail_icon ui-icon"></span>
              </td>
              <td><g:checkBox name="selectedTicket" id="selectedTicket" value="${ticket.ticketString}" checked="false"/>
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
				<td></td>
			</tr>
		</tfoot>
	</table>
	</g:form>
</div>