<div id="infoLeft" style="float: left; display: inline; width:60%;overflow:auto;">
	<fieldset id="verticalForm">
		<label>Collection:</label>
		${retObj.collectionName}
		<br />
		<label>Name:</label>
		${retObj.dataName}
		<br /> <label>Size:</label>
		${retObj.dataSize}
		<br /> <label>Created At:</label>
		${retObj.createdAt}
		<br /> <label>Updated At:</label>
		${retObj.updatedAt}
		<g:hiddenField id="infoAbsPath" name="absolutePath"
			value="${retObj.absolutePath}" />

	</fieldset>
</div>
<div id="infoRight" style="float: right; display: inline; width:40%;overflow:auto;">
	<div id="infoThumbnailLoadArea"></div>
</div>
<g:if test="${getThumbnail}">
	<script>

$(function() {
	requestThumbnailImageForInfoPane();
});

</script>
</g:if>
