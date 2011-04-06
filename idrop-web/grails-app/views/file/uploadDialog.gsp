<div id="uploadDialogDiv">
	
	<div class="box">
		<g:form controller="file" action="upload" method="post"
			name="uploadForm" enctype="multipart/form-data"
			target="upload_target">
			<fieldset id="verticalForm">
				<label for="">Parent Collection:</label>
				<g:textField name="collectionParentName"
					value="${irodsTargetCollection}" readonly="true" />
				<br />
				<div id="file_upload_container">
					<input type="file" name="file" multiple>
					<button>Upload</button>
					<div>Upload files</div>
				</div>
				<g:textField id="infoTags" name="tags"
					value="tags.spaceDelimitedTagsForDomain" />
				<br />
				<table id="files"></table>
			</fieldset>
		</g:form>


	</div>
</div>


