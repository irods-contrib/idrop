<div id="uploadDialogDiv">
	
	<g:form controller="file" action="upload" method="post"
			name="uploadForm" enctype="multipart/form-data"
			target="upload_target">
		
				<b>Parent Collection:</b>
				${irodsTargetCollection}
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



