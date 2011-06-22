
<div class="box">
	<fieldset id="verticalForm">
		<label for="">Collection:</label>
		<g:textField name="collectionName"
			value="${dataObject.collectionName}" readonly="true" />
		<br /> <label for="dataName">Name:</label>
		<g:textField name="dataName" value="${dataObject.dataName}"
			readonly="true" />
		<br /> <label for="size">Size:</label>
		<g:textField name="size" value="${dataObject.dataSize}" readonly="true "/>
		<br /> <label for="createdAt">Created At:</label>
		<g:textField name="createdAt" value="${dataObject.createdAt}"
			readonly="true" />
		<br /> <label for="updatedAt">Updated At:</label>
		<g:textField name="updatedAt" value="${dataObject.updatedAt}"
			readonly="true" />
			
			<br /> <label for="owner">Owner:</label>
		<g:textField name="owner" value="${dataObject.dataOwnerName}"
			readonly="true" />
			
			
			<br /> <label for="ownerZone">Owner Zone:</label>
		<g:textField name="ownerZone" value="${dataObject.dataOwnerZone}"
			readonly="true" />
			
		<br /> <label for="tags">Tags:</label>
		<g:textField id="infoTags" name="tags"
			value="${tags.spaceDelimitedTagsForDomain}" />
		<br />
		
		<g:hiddenField id="infoAbsPath" name="absolutePath"
			value="${dataObject.absolutePath}" />

		<button type="button" id="updateTags" value="updateTags"
			onclick="updateTags()")>Update Tags</button>

		<div id="infoUpdateArea">
			<!--  div for any updates in info -->
		</div>

	</fieldset>
</div>