<div id="infoMessageArea">
		<!--  -->
	</div>
<div>
<div id="infoLeft" style="float:left; display:inline;">
	<fieldset id="verticalForm">
		<label>Parent Collection:</label>${collection.collectionParentName}
		<br /><label>Name:</label>${collection.collectionName}
		<br /> <label>Created At:</label>${collection.createdAt}
		<br /> <label>Updated At:</label>${collection.modifiedAt}
		<br /> <label>Owner:</label>${collection.collectionOwnerName}
		<br /> <label>Owner Zone:</label>${collection.collectionOwnerZone}
		<br /> <label for="tags">Tags:</label>
		<g:textField id="infoTags" name="tags"
			value="${tags.spaceDelimitedTagsForDomain}" />
		<br />
		
		<br /> <label for="comment">Comment:</label>
		<g:textArea id="infoComment" name="comment" rows="5" cols="80"
			value="${comment}" />
		<br />
		
		<g:hiddenField id="infoAbsPath" name="absolutePath"
			value="${collection.collectionName}" />

		<button type="button" id="updateTags" value="updateTags"
			onclick="updateTags()")>Update Tags</button>

		<div id="infoUpdateArea">
			<!--  div for any updates in info -->
		</div>

	</fieldset>
	</div>
	<div id="infoRight" style="float:right">
		<div id="infoThumbnail">
		<span id="infoThumbnailLoadArea"><image src="<g:resource dir="images" file="folder.png" alt="folder icon" />"/></span></div>
	</div>
</div>
</div>

    <script>

$(function() {
	
});

</script>
