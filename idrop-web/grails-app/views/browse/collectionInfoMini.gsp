<g:hiddenField id="infoAbsPath" name="absolutePath"
					value="${collection.collectionName}" />
<div id="infoMessageArea">
		<!--  -->
	</div>

<div id="idropLiteArea">
			<!--  area to show idrop lite applet -->
		</div>
		<div id="toggleHtmlArea">
		<div id="infoDialogArea"><!--  no empty divs --></div>
		
		<!-- display area lays out info in a main and side column -->
	<div id="infoDisplayLayout" style="display:table;width:100%;height:100%;">
	
	<div style="display:table-row;">
		<div id="infoThumbnailAndMediaDisplay"  style="display:table-cell;">
			<div id="infoThumbnailLoadArea"><image src="<g:resource dir="images" file="folder.png" alt="folder icon" />"/></div>
		</div>
	</div>
	
	<div style="display:table-row;">
		<div id="infoDisplayMain"  style="display:table-cell;">
		
		<!-- inner table for general data -->
		
			<div id="container" style="height:100%;width:100%;">
		
				<div>
					<div style="width:20%;"><label>Collection:</label></div>
					<div style="overflow:auto;">${collection.collectionName}</div>
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

					<div><button type="button" id="updateTags" value="updateTags" onclick="updateTagsFromCollectionInfoMini()">Update Tags</button></div>

				</div>
				<div>
					<div><label>Created At:</label></div>
					<div>${collection.createdAt}</div>
				</div>
				<div>
					<div><label>Updated At:</label></div>
					<div>${collection.modifiedAt}</div>
				</div>
				<div>
					<div><label>Owner:</label></div>
					<div>${collection.collectionOwnerName}</div>
				</div>
				<div>
					<div><label>Owner Zone:</label></div>
					<div>${collection.collectionOwnerZone}</div>
				</div>
				<div>
					<div><label>Collection Type:</label></div>
					<div>${collection.specColType}</div>
				</div>
				<div>
					<div><label>Object Path:</label></div>
					<div>${collection.objectPath}</div>
				</div>
				<div>
					<div><label>Description:</label></div>
					<div>${collection.comments}</div>
				</div>
				<div>
					<div><label>Info1:</label></div>
					<div>${collection.info1}</div>
				</div>
				<div>
					<div><label>Info2:</label></div>
					<div>${collection.info2}</div>
				</div>
				
			</div>
			
		</div><!-- cell -->
	</div><!-- row -->
	
	
</div><!-- table -->
</div><!--  toggle html area -->
	

    <script>


function updateTagsFromCollectionInfoMini() {
	var infoTagsVal = $("#infoTags").val();
	var infoCommentVal = $("#infoComment").val();
	var absPathVal = $("#infoAbsPath").val();
	
	updateTagsAtPath(absPathVal, infoTagsVal, infoCommentVal);
}

</script>
