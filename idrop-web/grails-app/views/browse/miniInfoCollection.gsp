<h2>
<div class="roundedContainer">
<div id="infoLeft">
	<fieldset id="verticalForm">
		<label for="">Parent Collection:</label>
		<g:textField name="collectionParentName"
			value="${collection.collectionParentName}" readonly="true" />
		<br /> <label for="collectionName">Collection Name:</label>
		<g:textField name="collectionName"
			value="${collection.collectionName}" readonly="true" />
		<br /> <label for="createdAt">Created At:</label>
		<g:textField name="createdAt" value="${collection.createdAt}"
			readonly="true" />

		<br /> <br /> <label for="updatedAt">Updated At:</label>
		<g:textField name="updatedAt" value="${collection.modifiedAt}"
			readonly="true" />

		<br /> <label for="owner">Owner:</label>
		<g:textField name="owner" value="${collection.collectionOwnerName}"
			readonly="true" />


		<br /> <label for="ownerZone">Owner Zone:</label>
		<g:textField name="ownerZone"
			value="${collection.collectionOwnerZone}" readonly="true" />

		<g:hiddenField id="infoAbsPath" name="absolutePath"
			value="${collection.collectionName}" />

		<br /> <label for="tags">Tags:</label>
		<g:textField id="infoTags" name="tags"
			value="${tags.spaceDelimitedTagsForDomain}" />

		<button type="button" id="updateTags" value="updateTags"
			onclick="updateTags()")>Update Tags</button>

		<div id="infoUpdateArea">
			<!--  div for any updates in info -->
		</div>

	</fieldset>
	</div>
	<div id="infoRight" style="float:right">
		<div id="infoThumbnail">
		<span id="infoThumbnailLoadArea"><!--  thumbnail image --></span></div>
	</div>
</div>
<script>

$(function() {
	//alert("i am done loading miniinfo");
	//alert("abs path is:" + $("#infoAbsPath").val());
	
});

</script>
