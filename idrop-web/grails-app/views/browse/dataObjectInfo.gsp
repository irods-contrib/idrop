<g:hiddenField id="infoAbsPath" name="absolutePath"
	value="${dataObject.absolutePath}" />

<div id="infoMessageArea">
	<!--  -->
</div>
<div id="idropLiteArea">
	<!--  area to show idrop lite applet -->
</div>
<div id="toggleHtmlArea">
	<div id="displayArea" style="position:relative;width:100%;height:95%;display:block;">

	<div id="infoDialogArea"><!--  no empty divs --></div>
	
	
	<!-- display area lays out info in a main and side column -->
	<div id="infoDisplayLayout" style="display:table;width:100%;height:100%;">
	
	
	
	<div style="display:table-row;">
		<div id="infoDisplayMain"  style="display:table-cell;">
		
		<!-- inner table for general data -->
		
			<div id="container" style="height:100%;width:100%;">
		
				<div>
					<div style="width:20%;"><label>Collection:</label></div>
					<div style="overflow:auto;">${dataObject.collectionName}</div>
				</div>
				<div >
					<div><label>Name:</label></div>
					<div style="overflow:auto;"><g:link url="${'file/download' + dataObject.absolutePath}">${dataObject.dataName}</g:link></div>
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
					<div style="width:20%;"><label>Size:</label></div>
					<div>${dataObject.dataSize}</div>
				</div>
				<div>
					<div><label>Tags:</label></div>
					<div><g:textField id="infoTags" name="tags"
					value="${tags.spaceDelimitedTagsForDomain}" /></div>
				</div>
				<div>
					<div><label>Comment:</label></div>
					<div><g:textArea id="infoComment" name="comment" rows="5" cols="80"
					value="${comment}" /></div>
				</div>
				<div>
					<div></div>
					<div><button type="button" class="ui-state-default ui-corner-all" id="updateTags" value="updateTags" onclick="callUpdateTags()">Update Tags</button></div>
				</div>
				<div>
					<div><label>Created At:</label></div>
					<div>${dataObject.createdAt}</div>
				</div>
				<div>
					<div><label>Updated At:</label></div>
					<div>${dataObject.updatedAt}</div>
				</div>
				<div>
					<div><label>Owner:</label></div>
					<div>${dataObject.dataOwnerName}</div>
				</div>
				<div>
					<div><label>Owner Zone:</label></div>
					<div>${dataObject.dataOwnerZone}</div>
				</div>
				<div>
					<div><label>Data Path:</label></div>
					<div style="overflow:auto;"><span class="longText">${dataObject.dataPath}</span></div>
				</div>
				<div>
					<div><label>Resource Group:</label></div>
					<div>${dataObject.resourceGroupName}</div>
				</div>
				<div>
					<div><label>Checksum:</label></div>
					<div>${dataObject.checksum}</div>
				</div>
				<div>
					<div><label>Resource:</label></div>
					<div>${dataObject.resourceName}</div>
				</div>
				<div>
					<div><label>Replica Number:</label></div>
					<div>${dataObject.dataReplicationNumber}</div>
				</div>
				<div>
					<div><label>Replication Status:</label></div>
					<div>${dataObject.replicationStatus}</div>
				</div>
				<div>
					<div><label>Status:</label></div>
					<div>${dataObject.dataStatus}</div>
				</div>
				<div>
					<div><label>Type:</label></div>
					<div>${dataObject.dataTypeName}</div>
				</div>
				<div>
					<div><label>Version:</label></div>
					<div>${dataObject.dataVersion}</div>
				</div>
			</div>
		</div><!-- cell -->
	</div><!-- row -->
	
	
</div><!-- table -->
</div><!--  toggle html area -->

<script>
$(function() {
	showDetailsToolbar();
	$("#menuDownload").show();
	$("#menuUpload").hide();
	$("#menuBulkUpload").hide();
});

function callUpdateTags() {
	updateTags();
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
