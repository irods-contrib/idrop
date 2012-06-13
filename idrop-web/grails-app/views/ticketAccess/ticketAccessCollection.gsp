<head>
<meta name="layout" content="basic" />
</head>
<div  class="wrapper"
	style="height: 820px;">
	<div class="roundedContainer">
	<h1>Landing page for a collection</h1>
	<h2>This ticket allows you to upload a file to an iRODS location.  Click the button below to select a file to you local file system and send it to iRODS</h2>
	</div>
	<div id="container" style="height:auto;width:100%;">
				<g:hiddenField name='ticketString' id='ticketString' value='${ticketString}'/>
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
					<div><button type="button" id="uploadToCollectionButton"
								class="ui-state-default ui-corner-all" 
								onclick="uploadToCollectionButton()")><g:message code="text.upload" /></button></div>
				</div>
	</div>
			
</div>