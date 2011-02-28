<fieldset id="verticalForm"><label for="collectionName">Parent
Collection:</label> <g:textField name="collectionName"
	value="${dataObject.collectionName}" readonly="true" /> <br />

<label for="dataName">File Name:</label> <g:textField name="dataName"
	value="${dataObject.dataName}" readonly="true" /> <br />

<label for="createdAt">Created At:</label> <g:textField name="createdAt"
	value="${dataObject.createdAt}" readonly="true" /> <br />

<label for="dataSize">Size:</label> <g:textField name="dataSize"
	value="${dataObject.dataSize}" readonly="true" /> <br />

<label for="checksum">Checksum:</label> <g:textField name="checksum"
	value="${dataObject.checksum}" readonly="true" /> <br />

<label for="tags">Tags:</label> <g:textField id="infoTags" name="tags"
	value="${tags.spaceDelimitedTagsForDomain}" /> <br />

<g:hiddenField id="infoAbsPath" name="absolutePath"
	value="${dataObject.absolutePath}" />

<button type="button" id="updateTags" value="updateTags"
	onclick="updateTags()")>Update Tags</button>
	
	<div id="infoUpdateArea"><!--  div for any updates in info --></div>

</fieldset>