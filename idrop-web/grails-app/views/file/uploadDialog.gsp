<div id="uploadDialogDiv">
	<div class="box">
	<g:form controller="file" action="upload" method="post"
			name="uploadForm" enctype="multipart/form-data"
			target="upload_target">
			<fieldset id="verticalForm">
				<label for="">Parent Collection:</label>
				<h2>${irodsTargetCollection}</h2>
				<br /> 
				<div id="file_upload_container">
					<input type="file" name="file">
				<button type="button" id="upload" value="upload"
			onclick="upload()")>Upload</button>
				</div>
				<div id="upload_message_area">
				</div>
				<table id="files"></table>
			</fieldset>
		</g:form> 
	</div>
</div>



