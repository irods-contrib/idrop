<h4><g:message code="text.tickets" /></h4>
 <div id="ticketDialogArea"><!--  area for generating dialogs --></div>
 <div id="ticketMessageArea"><!--  area for messages --></div>

<g:javascript library="jquery.gchart.min" />

<div id="ticketDetailsTableArea">
 	<div class="alert alert-info">
 		<g:message code="heading.tickets" />
	</div>
	<div class="well" id="ticketDetailsTopSection" >
		<div id="detailsMenu"">
			<button type="button" id="addTicketButton"
				 value="addTicket"
				onclick="prepareTicketDetailsDialog()")>
				<g:message code="default.button.create.label" />
			</button>
			<button type="button" id="editTicketButton"
				 value="editTicket"
				onclick="editTicketDialog()")>
				<g:message code="default.button.edit.label" />
			</button>
			<button type="button" id="deleteTicketButton"
				 value="deleteTicket"
				onclick="deleteTicket()")>
				<g:message code="default.button.delete.label" />
			</button>
			<button type="button" id="reloadTickets"
				 value="reloadTickets"
				onclick="reloadTickets()")>
				<g:message code="default.button.reload.label" />
			</button>
		</div>
	</div>
	<g:hiddenField name='ticketDetailsAbsPath' id='ticketDetailsAbsPath' value='${objStat.absolutePath}'/>
	<div id="ticketTableDiv"><!-- ticket list --></div>
</div>

<script type="text/javascript">

	var messageAreaSelector="#ticketMessageArea";
	
	$(function() {
		var path = $("#ticketDetailsAbsPath").val();
		if (path == null) {
			path = baseAbsPath;
		}
		reloadTickets();
	});



	</script>