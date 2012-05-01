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
	<div id="infoDisplayLayout" style="width:100%;height:100%;">
	
	<div class="roundedContainer">
			<image style="float:left;margin-right:10px;" src="<g:resource dir="images" file="folder.png" alt="folder icon" />"/>
			<h3>${collection.collectionName}</h3>
	</div>
	
</div><!-- table -->

<div id="infoAccordion" style="width:98%;margin:10px;">
 <h3  id="infoAccordionBasicInfo"><a href="#infoAccordionBasicInfo"><g:message code="text.info" /></a></h3>
    	<div>
		
			<div id="container" style="height:100%;width:100%;">
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
					<div><label>CollectionType:</label></div>
					<div>${collection.collectionType}</div>
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
		</div>

    <h3 id="infoAccordionTags"><a href="#infoAccordionTags"><g:message code="text.tags" /></a></h3>
   	<div >
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
		</div>
		 <h3  id="infoAccordionMetadata"><a href="#infoAccordionMetadata" ><g:message code="text.metadata" /></a></h3>
   			<div id="infoAccordionMetadataInner">
			</div>
			<h3 id="infoAccordionACL"><a href="#infoAccordionACL" ><g:message code="text.permissions" /></a></h3>
   			<div id="infoAccordionACLInner">
			</div>
			<g:if test="${grailsApplication.config.idrop.config.use.tickets==true}">
			<h3 id="infoAccordionTickets"><a href="#infoAccordionTickets" ><g:message code="text.tickets" /></a></h3>
   			<div id="infoAccordionTicketsInner">
			</div>
			</g:if>
			<h3 id="infoAccordionAudit"><a href="#infoAccordionAudit"><g:message code="text.audit" /></a></h3>
   			<div >
			</div>
</div>


</div><!--  toggle html area -->
	

    <script>
					$(function() {
						
						$(".idropLiteBulkUpload").show();
						$("#menuDownload").hide();
						$("#menuUpload").show();
						$("#menuBulkUpload").show();

						$("#infoAccordion").accordion({ 
							  clearStyle: true,
							  autoHeight: false
							}).bind("accordionchange", function(event, ui) {
							 
								var infoSection = ui.newHeader[0].id;
								updateInfoSection(infoSection);
							});
					});

					function callUpdateTags() {
						updateTags();
					}


					/**
					Update the info for a section in the info accordion based on the provided section id
					*/
					function updateInfoSection(sectionToUpdate) {
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
