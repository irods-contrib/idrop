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
	
			<div class="roundedContainer">
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
				<div id="infoThumbnailLoadArea"></div>
					<g:if test="${renderMedia}">
						<a class="media" href="${resource(absolute:true,dir:'file/download',file:dataObject.absolutePath)}"></a>
					</g:if>
					<g:else>
						<a href="${resource(absolute:true,dir:'file/download',file:dataObject.absolutePath)}"></a>
					</g:else>
				</div>
			</div>
		</div>
		

<div id="infoAccordion" style="width:98%;margin:10px;">
 <h3  id="infoAccordionBasicInfo"><a href="#infoAccordionBasicInfo"><g:message code="text.info" /></a></h3>
    	
			<div id="container" style="height:100%;width:100%;">
		
				<div >
					<div style="width:20%;"><label>Size:</label></div>
					<div>${dataObject.displayDataSize}</div>
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

    <h3 id="infoAccordionTags"><a href="#infoAccordionTags"><g:message code="text.tags" /></a></h3>
  
			<div id="container" style="height:100%;width:100%;">
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
			</div>
	
		 <h3  id="infoAccordionMetadata"><a href="#infoAccordionMetadata" ><g:message code="text.metadata" /></a></h3>
   			<div id="infoAccordionMetadataInner"></div>
			<h3 id="infoAccordionACL"><a href="#infoAccordionACL" ><g:message code="text.permissions" /></a></h3>
   			<div id="infoAccordionACLInner">
			</div>
			
			<g:if test="${grailsApplication.config.idrop.config.use.tickets==true}">
			<h3 id="infoAccordionTickets"><a href="#infoAccordionTickets" ><g:message code="text.tickets" /></a></h3>
   			<div id="infoAccordionTicketsInner"></div>
			</g:if>
			<h3 id="infoAccordionAudit"><a href="#infoAccordionAudit"><g:message code="text.audit" /></a></h3>
   			<div >
			</div>
</div>
	


<script>

	$(function() {
		showDetailsToolbar();
		$(".idropLiteBulkUpload").hide();
		$("#menuDownload").show();
		$("#menuUpload").hide();
		$("#menuBulkUpload").hide();

		$("#infoAccordion").accordion({ 
			  clearStyle: true,
			  autoHeight: false
			}).bind("accordionchange", function(event, ui) {
				var infoSection = ui.newHeader[0].id;
				updateDataObjectInfoSection(infoSection);
			});
		
	});
	

	
	/**
	Update the info for a section in the info accordion based on the provided section id
	*/
	function updateDataObjectInfoSection(sectionToUpdate) {
		//alert("sectionToUpdate:" + sectionToUpdate);
		if (sectionToUpdate=="infoAccordionMetadata") {
			showMetadataView(selectedPath, "#infoAccordionMetadataInner");
		} else if (sectionToUpdate=="infoAccordionACL") {
			showSharingView(selectedPath, "#infoAccordionACLInner");
		} else if (sectionToUpdate=="infoAccordionTickets") {
			showTicketView(selectedPath, "#infoAccordionTicketsInner");
		} else if (sectionToUpdate=="infoAccordionAudit") {
		} else {
		}
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
		$('.media').media({
			width : 300,
			height : 200,
			autoplay : true
		});

	});
</script>
</g:else>
