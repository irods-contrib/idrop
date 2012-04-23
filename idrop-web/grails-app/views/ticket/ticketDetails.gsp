<g:javascript library="mydrop/ticket" />
<h2><g:message code="text.tickets" /></h2>
<div id="ticketDialogArea"><!--  area for generating dialogs --></div>

<div id="ticketDetailsArea">
	<div id="ticketDetailsTopSection" >
		
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
		reloadTicketTable(path);
	});

	</script>