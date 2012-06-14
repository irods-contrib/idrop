
<div id="uploadToTicketDialog">
	
	<g:form controller="ticketAccess" action="uploadViaTicket" method="post"
			name="uploadForm" enctype="multipart/form-data"
			target="upload_target">
		
				<b><g:message code="heading.upload.ticket.collection" /></b>
				<g:hiddenField id="ticketString" name="ticketString" value="${ticketString}"/>
				<g:hiddenField id="irodsURI" name="irodsURI" value="${irodsURI}"/>
				<br /> 
				<div id="file_upload_container">
					<input type="file" name="file">
				<button type="button" id="upload" value="upload"
			onclick="uploadToTicketCollection()")>Upload</button>
				</div>
				<div id="upload_message_area">
				</div>
				<table id="files"></table>
			
		</g:form> 
	
</div>



