 <h3><a ><g:message code="text.tickets" /></a></h3>
<g:javascript library="jquery.gchart.min" />
<div id="ticketDialogArea"><!--  area for generating dialogs --></div>
<div id="ticketMessageArea"><!--  area for messages --></div>

<div id="ticketDetailsTableArea">
	<div class="well" id="ticketDetailsTopSection" >
		<div id="detailsMenu" class="fg-buttonset fg-buttonset-multi"
			style="float: left, clear :   both;">
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