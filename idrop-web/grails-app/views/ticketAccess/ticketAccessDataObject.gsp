<head>
<meta name="layout" content="basic" />
</head>
<div  class="wrapper"
	style="height: 820px;">
	<div class="roundedContainer">
	<h1>Landing page for Data Object</h1>
	<h2>This ticket allows you to download a file from iRODS that has been shared with you.  Click the button below to download the file to your local file system</h2>
	</div>
	<div id="container" style="height:auto;width:100%;">
				<g:hiddenField name='ticketString' id='ticketString' value='${ticketString}'/>
				<g:hiddenField name='irodsURI' id='irodsURI' value='${irodsURI}'/>
				<div>
					<div style="width:20%;"><label><g:message code="text.ticket.string" />:</label></div>
					<div>${ticketString}</div>
				</div>
				<div>
					<div style="width:20%;"><label><g:message code="text.ticket.type" />:</label></div>
					<div>${ticketType}</div>
				</div>
				<div>
					<div style="width:20%;"><label><g:message code="text.irods.uri" />:</label></div>
					<div>${irodsURI}</div>
				</div>
				<div>
					<div style="width:20%;"><label><g:message code="text.actions" />:</label></div>
					<div><button type="button" id="downloadDataObjectButton"
								onclick="downloadDataObjectButton()")><g:message code="text.download" /></button></div>
				</div>
	</div>
	
	
</div>


<script>

function downloadDataObjectButton() {
	var ticketString = $("#ticketString").val();
	var irodsURI = $("#irodsURI").val();
	
	window.open(context + '/ticketAccess/redeemTicket?ticketString=' + encodeURI(ticketString) + "&irodsURI=" + encodeURI(irodsURI), '_blank');
}


</script>