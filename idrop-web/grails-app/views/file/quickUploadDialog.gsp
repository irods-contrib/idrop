<div id="uploadDialogDiv">
	
	
	 <div class="modal-header">
    		<h3><g:message code="heading.upload.dialog"/></h3>
  	</div>
  	
  	 <div class="modal-body">
		<g:form controller="quickUpload" action="upload" method="post"
				name="uploadForm" enctype="multipart/form-data"
				target="upload_target">
			
					<label><g:message
							code="text.parent" />
					${irodsTargetCollection}</label>
					<g:hiddenField id="collectionParentName" name="collectionParentName" value="${irodsTargetCollection}"/>
					<br /> 
					<div id="file_upload_container">
						<input type="file" name="file">
					<button type="button" id="upload" value="upload"
				onclick="upload()")>Upload</button>
					</div>
					<div id="upload_message_area">
					</div>
					<table id="files"></table>
				
			</g:form> 
		</div>
		
		<div class="modal-footer">
		<button type="button" id="cancelUpload"
			 value="cancelUpload"
			onclick="closeUploadDialog()")><g:message code="text.cancel" /></button>
  		</div>
		
</div>

<script>
function closeUploadDialog() {
	$("#uploadDialog").dialog('close');
	$("#uploadDialog").remove();
}

</script>

