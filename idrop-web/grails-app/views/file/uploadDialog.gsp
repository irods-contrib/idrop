<div id="uploadDialogDiv">
<script>


</script>

	<div class="box">
		<g:form controller="file" action="upload" method="post"
			name="uploadForm" enctype="multipart/form-data">
			<fieldset id="verticalForm">
				<label for="">Parent Collection:</label>
				<g:textField name="collectionParentName"
					value="${irodsTargetCollection}" readonly="true" />
				<br /> <input type="file" name="file" /> <br /> <label
					for="tags">Tags:</label>
				<g:textField id="infoTags" name="tags"
					value="tags.spaceDelimitedTagsForDomain" />
				<button type="button" id="upload" value="upload"
					onclick="doUploadFromDialog()">Upload File</button>
				<div id="uploadInfoUpdateAra">
					<!--  div for any updates -->
				</div>
			</fieldset>
		</g:form>

	</div>
</div>


