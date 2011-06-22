<div id="uploadDialogDiv">
	<div class="box">
		<g:form controller="file" action="upload" method="post"
			name="uploadForm" enctype="multipart/form-data"
			target="upload_target">
			<fieldset id="verticalForm">
				<label for="">Parent Collection:</label>
				<g:textField name="collectionParentName"
					value="${irodsTargetCollection}" readonly="true" />
				<br /> <label for="infoTags">Tags:</label>
				<g:textField id="infoTags" name="tags"
					value="tags.spaceDelimitedTagsForDomain" />
				<br /> <label for="description">Description:</label>
				<g:textArea name="description" rows="5" cols="80" />
				<br/><label>File: (selecting a file will upload with provided metadata)</label>
				<br/>
				<div id="file_upload_container">
					<input type="file" name="file">
					<button>Upload</button>
					<div></div>
				</div>
				<div id="upload_message_area">
				</div>
				<table id="files"></table>
			</fieldset>
		</g:form>
	</div>
</div>


