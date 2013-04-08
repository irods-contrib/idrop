<g:hiddenField id="infoAbsPath" name="absolutePath"
	value="${collection.collectionName}" />
<div id="infoMessageArea">
		<!--  -->
	</div>

<div id="idropLiteArea">
			<!--  area to show idrop lite applet -->
</div>
<div id="toggleHtmlArea">
	<g:render template="/browse/collectionInfoToolbar" />
	<div id="infoDialogArea"><!--  no empty divs --></div>
		
		<!-- display area lays out info in a main and side column -->
	<div id="infoDisplayLayout" style="width:100%;height:100%;">
	
	<div class="well">
			<image style="float:left;margin-right:10px;" src="<g:resource dir="images" file="folder.png" alt="folder icon" />"/>
			<h3>${collection.collectionName}</h3>
	</div>	
</div>


<ul class="nav nav-tabs" id="infoTabs">
  <li class="active"><a href="#info" id="infoTab"><g:message code="text.info" /></a></li>
  <li><a href="#metadata" id="metadataTab"><g:message code="text.metadata" /></a></li>
  <li><a href="#permissions" id="permissionTab"><g:message code="text.sharing" /></a></li>
  <g:if test="${grailsApplication.config.idrop.config.use.tickets==true}">
  	<li><a href="#tickets" id="ticketTab"><g:message code="text.tickets" /></a></li>
  </g:if>
  <li><a href="#audit"  id="auditTab"><g:message code="text.audit" /></a></li>
  <g:if test="${grailsApplication.config.idrop.use.hive==true}">
  <li><a href="#hive"  id="hiveTab"><g:message code="text.hive" /></a></li>
  </g:if>
</ul>
 
<div class="tab-content">
  <div class="tab-pane active" id="info">
  
  	 <h4><g:message code="text.info" /></h4>
 		<div class="alert alert-info">
 			<g:message code="heading.info" />
		</div>
		
			<div id="container" style="height:100%;width:100%;">
				<div>
					<div style="width:15%;"><label><g:message code="text.created" />:</label></div>
					<div>${collection.createdAt}</div>
				</div>
				<div>
					<div><label><g:message code="text.updated" />:</label></div>
					<div>${collection.modifiedAt}</div>
				</div>
				<div>
					<div><label><g:message code="text.owner" />:</label></div>
					<div>${collection.collectionOwnerName}</div>
				</div>
				<div>
					<div><label><g:message code="text.owner.zone" />:</label></div>
					<div>${collection.collectionOwnerZone}</div>
				</div>
				<div>
					<div><label><g:message code="text.type" />:</label></div>
					<div>${collection.specColType}</div>
				</div>
				<div>
					<div><label><g:message code="text.object.path" />:</label></div>
					<div>${collection.objectPath}</div>
				</div>
				<div>
					<div><label><g:message code="text.description" />:</label></div>
					<div>${collection.comments}</div>
				</div>
				<div>
					<div><label><g:message code="text.info" />1:</label></div>
					<div>${collection.info1}</div>
				</div>
				<div>
					<div><label><g:message code="text.info" />2:</label></div>
					<div>${collection.info2}</div>
				</div>
				
			</div>
			<div id="container" style="height:100%;width:100%;">
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
					<div><button type="button"  id="updateTags" value="updateTags" onclick="updateTags()"><g:message code="text.update.tags" /></button></div>
				</div>
			</div>
  
  </div>
  <div class="tab-pane" id="metadata">
  	<div id="infoAccordionMetadataInner"></div>
  </div>
  <div class="tab-pane" id="permissions">
  	<div id="infoAccordionACLInner"></div>
  </div>
  <g:if test="${grailsApplication.config.idrop.config.use.tickets==true}">
	  <div class="tab-pane" id="tickets">
	   			<div id="infoAccordionTicketsInner">
				</div>
		</div>
	</g:if>
   <div class="tab-pane" id="audit">
   	<div id="infoAccordionAuditInner"></div>
   </div>
   <g:if test="${grailsApplication.config.idrop.use.hive==true}">
   <div class="tab-pane" id="hive">
   	<div id="infoAccordionHiveInner"></div>
   </div>
   </g:if>
</div>

</div><!--  toggle html area -->
	

    <script>
					$(function() {

						$(".idropLiteBulkUpload").show();
						$("#menuDownload").hide();
						$("#menuUpload").show();
						$("#menuBulkUpload").show();


						$('#infoTabs a').click(function (e) {
							  e.preventDefault();
							  $(this).tab('show');
						});
						
						$('#infoTab').on('shown', function (e) {
  							//e.target // activated tab
  							//e.relatedTarget // previous tab
							showMetadataView(selectedPath, "#infoAccordionMetadataInner");
						});
						
						$('#metadataTab').on('shown', function (e) {
							showMetadataView(selectedPath, "#infoAccordionMetadataInner");
						});

						$('#permissionTab').on('shown', function (e) {
							showSharingView(selectedPath, "#infoAccordionACLInner");
						});

						$('#ticketTab').on('shown', function (e) {
							showTicketView(selectedPath, "#infoAccordionTicketsInner");
						});

						$('#auditTab').on('shown', function (e) {
							showAuditView(selectedPath, "#infoAccordionAuditInner"); 
						});

						$('#hiveTab').on('shown', function (e) {
							showHiveView(selectedPath, "#infoAccordionHiveInner"); 
						});
							
					});

				</script>
