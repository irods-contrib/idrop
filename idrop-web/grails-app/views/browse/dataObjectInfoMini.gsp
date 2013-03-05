<g:hiddenField id="infoAbsPath" name="absolutePath"
	value="${dataObject.absolutePath}" />

<div id="infoMessageArea">
	<!--  -->
</div>
<div id="idropLiteArea">
	<!--  area to show idrop lite applet -->
</div>
<div id="toggleHtmlArea">
	<div id="displayArea" >
	<div id="infoDialogArea"><!--  no empty divs --></div>
	
	
	<!-- display area lays out info in a main and side column -->
	<div id="infoDisplayLayout">
	
	
	
	<div style="display:table-row;">
		<div id="infoDisplayMain"  style="display:table-cell;">
		<image style="float:left;margin-right:10px;" src="<g:resource dir="images" file="file.png" alt="file icon" />"/>
		
		<!-- inner table for general data -->
		
			<div id="container" style="height:100%;width:100%;">
		
				
				<div >
					<div><label><g:message code="text.file.name" />:</label></div>
					<div style="overflow:auto;"><g:link url="${'file/download' + dataObject.absolutePath}">${dataObject.dataName}</g:link></div>
				</div>
				<div>
					<div style="width:20%;"><label><g:message code="text.parent" />:</label></div>
					<div style="overflow:auto;">${dataObject.collectionName}</div>
				</div>
			</div>
		</div>
	</div>
	
	<div style="display:table-row;">
	<div id="infoThumbnailAndMediaDisplay"  style="display:table-cell;">
	<div id="infoThumbnailLoadArea"></div>
	<g:if test="${renderMedia}">
			<a class="media" href="${resource(absolute:true,dir:'file/download',file:dataObject.absolutePath)}"></a>
	</g:if>
	<g:else>
		<a href="${resource(absolute:true,dir:'file/download',file:dataObject.absolutePath)}"></a>
	</g:else>
		
		</div>
	</div>
	
	<div style="display:table-row;">
		<div id="infoDisplaySecondary"  style="display:table-cell;">
		
		<!-- inner table for general data -->
		
			<div id="container" style="height:100%;width:100%;">
		
				<div >
					<div style="width:20%;"><label><g:message code="text.length" />:</label></div>
					<div>${dataObject.displayDataSize}</div>
				</div>
				<div>
					<div><label><g:message code="text.tags" />:</label></div>
					<div><g:textField id="infoTags" name="tags"
					value="${tags.spaceDelimitedTagsForDomain}" /></div>
				</div>
				<div>
					<div><label><g:message code="text.comment" />:</label></div>
					<div><g:textArea id="infoComment" name="comment" rows="5" cols="80"
					value="${comment}" /></div>
				</div>
				<div>
					<div></div>
					<div><button type="button" id="updateTags" value="updateTags" onclick="updateTagsFromDataObjectInfoMini()">Update Tags</button></div>
				</div>
				<div>
					<div><label><g:message code="text.created" />:</label></div>
					<div>${dataObject.createdAt}</div>
				</div>
				<div>
					<div><label><g:message code="text.modified" />:</label></div>
					<div>${dataObject.updatedAt}</div>
				</div>
				<div>
					<div><label><g:message code="text.owner" />:</label></div>
					<div>${dataObject.dataOwnerName}</div>
				</div>
				<div>
					<div><label><g:message code="text.owner.zone" />:</label></div>
					<div>${dataObject.dataOwnerZone}</div>
				</div>
				<div>
					<div><label><g:message code="text.data.path" />:</label></div>
					<div style="overflow:auto;"><span class="longText">${dataObject.dataPath}</span></div>
				</div>
				<div>
					<div><label><g:message code="text.resource.group" />:</label></div>
					<div>${dataObject.resourceGroupName}</div>
				</div>
				<div>
					<div><label><g:message code="text.checksum" />:</label></div>
					<div>${dataObject.checksum}</div>
				</div>
				<div>
					<div><label><g:message code="text.resource" />:</label></div>
					<div>${dataObject.resourceName}</div>
				</div>
				<div>
					<div><label><g:message code="text.replica.number" />:</label></div>
					<div>${dataObject.dataReplicationNumber}</div>
				</div>
				<div>
					<div><label><g:message code="text.replication.status" />:</label></div>
					<div>${dataObject.replicationStatus}</div>
				</div>
				<div>
					<div><label><g:message code="text.status" />:</label></div>
					<div>${dataObject.dataStatus}</div>
				</div>
				<div>
					<div><label><g:message code="text.type" />:</label></div>
					<div>${dataObject.dataTypeName}</div>
				</div>
				<div>
					<div><label><g:message code="text.version" />:</label></div>
					<div>${dataObject.dataVersion}</div>
				</div>
			</div>
		</div><!-- cell -->
	</div><!-- row -->
	
	
</div><!-- table -->
</div><!--  toggle html area -->

<script>

function updateTagsFromDataObjectInfoMini() {
	var infoTagsVal = $("#infoTags").val();
	var infoCommentVal = $("#infoComment").val();
	var absPathVal = $("#infoAbsPath").val();
	
	updateTagsAtPath(absPathVal, infoTagsVal, infoCommentVal);
}

</script>

<g:if test="${getThumbnail}">
	<script>

$(function() {
	requestThumbnailImageForInfoPane();
});



</script>
</g:if>
<g:else>
<script>
$(function() {
	//$.fn.media.mapFormat('pdf', 'quicktime');
	$('.media').media( { width: 300, height: 200, autoplay: true } ); 

});
</script>
</g:else>
