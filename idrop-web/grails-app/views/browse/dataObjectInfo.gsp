<g:hiddenField id="infoAbsPath" name="absolutePath"
	value="${dataObject.absolutePath}" />

<div id="infoMessageArea">
	<!--  -->
</div>
<div id="idropLiteArea">
	<!--  area to show idrop lite applet -->
</div>
<div id="toggleHtmlArea">
	<div id="displayArea">
		<g:render template="/browse/dataObjectInfoToolbar" />
		<div id="infoDialogArea">
			<!--  no empty divs -->
		</div>


		<div class="well">
			<image style="float:left;margin-right:10px;"
				src='<g:resource dir="images" file="file.png" alt="file icon" />' />


			<h3>
				${dataObject.dataName}
			</h3>
		</div>



		<ul class="nav nav-tabs" id="infoTabs">
			<li class="active"><a href="#info" id="infoTab"><g:message
						code="text.info" /></a></li>
			<li><a href="#metadata" id="metadataTab"><g:message
						code="text.metadata" /></a></li>
			<li><a href="#permissions" id="permissionTab"><g:message
						code="text.sharing" /></a></li>
			<g:if
				test="${grailsApplication.config.idrop.config.use.tickets==true}">
				<li><a href="#tickets" id="ticketTab"><g:message
							code="text.tickets" /></a></li>
			</g:if>
			<li><a href="#audit" id="auditTab"><g:message
						code="text.audit" /></a></li>
			<g:if test="${grailsApplication.config.idrop.use.hive==true}">
				<li><a href="#hive" id="hiveTab"><g:message
							code="text.hive" /></a></li>
			</g:if>
		</ul>

		<div class="tab-content">
			<div class="tab-pane active" id="info">
				<div class="container">
					<div class="row">
						<div class="span12">
							<h4>
								<g:message code="text.info" />
							</h4>
						</div>
					</div>
					<div class="row alert alert-info">
						<div class="span12">
							<g:message code="heading.info" />
						</div>
					</div>

					<div class="row" id="infoThumbnailLoadArea">
						<div class="span12">
							<g:if test="${renderMedia}">
								<a class="media"
									href="${resource(absolute:true,dir:'file/download',file:dataObject.absolutePath)}"></a>
							</g:if>
							<g:else>
								<a
									href="${resource(absolute:true,dir:'file/download',file:dataObject.absolutePath)}"></a>
							</g:else>
						</div>
					</div>

					<div class="row">
						<div class="span2">
							<strong><g:message code="text.length" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.displayDataSize}
						</div>
					</div>

					<div class="row">
						<div class="span2">
							<strong><g:message code="text.created" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.createdAt}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.modified" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.updatedAt}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.owner" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.dataOwnerName}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.owner.zone" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.dataOwnerZone}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.data.path" /> :</strong>
						</div>
						<div class="span10 longText" style="overflow: auto;">
							${dataObject.dataPath}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.resource.group" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.resourceGroupName}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.checksum" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.checksum}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.resource" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.resourceName}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.replica.number" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.dataReplicationNumber}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.replication.status" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.replicationStatus}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.status" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.dataStatus}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.type" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.dataTypeName}
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.version" /> :</strong>
						</div>
						<div class="span10">
							${dataObject.dataVersion}
						</div>
					</div>

					<div class="row">
						<div class="span2">
							<strong><g:message code="text.tags" /> :</strong>
						</div>
						<div class="span10">
							<g:textField id="infoTags" name="tags"
								value="${tags.spaceDelimitedTagsForDomain}" />
						</div>
					</div>
					<div class="row">
						<div class="span2">
							<strong><g:message code="text.comment" /> :</strong>
						</div>
						<div class="span10">
							<g:textArea id="infoComment" name="comment" rows="5" cols="80"
								value="${comment}" />
						</div>
					</div>
					<div class="row">
						<div class="span2"></div>
						<div class="span10">
							<button type="button" id="updateTags" value="updateTags"
								onclick="updateTags()">
								<g:message code="text.update.tags" />
							</button>
						</div>
					</div>


				</div>
			</div>
			<div class="tab-pane" id="metadata">
				<div id="infoAccordionMetadataInner"></div>
			</div>
			<div class="tab-pane" id="permissions">
				<div id="infoAccordionACLInner"></div>
			</div>
			<g:if
				test="${grailsApplication.config.idrop.config.use.tickets==true}">
				<div class="tab-pane" id="tickets">
					<div id="infoAccordionTicketsInner"></div>
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

	</div>
	<!--  toggle html area -->

	<script>
	$(function() {
		$(".idropLiteBulkUpload").hide();
		$("#menuDownload").show();
		$("#menuUpload").hide();
		$("#menuBulkUpload").hide();

		$('#infoTabs a').click(function(e) {
			e.preventDefault();
			$(this).tab('show');
		});

		$('#infoTab').on('shown', function(e) {
			//e.target // activated tab
			//e.relatedTarget // previous tab
			showMetadataView(selectedPath, "#infoAccordionMetadataInner");
		});

		$('#metadataTab').on('shown', function(e) {
			showMetadataView(selectedPath, "#infoAccordionMetadataInner");
		});

		$('#permissionTab').on('shown', function(e) {
			showSharingView(selectedPath, "#infoAccordionACLInner");
		});

		$('#ticketTab').on('shown', function(e) {
			showTicketView(selectedPath, "#infoAccordionTicketsInner");
		});

		$('#auditTab').on('shown', function(e) {
			showAuditView(selectedPath, "#infoAccordionAuditInner");
		});

		$('#hiveTab').on('shown', function(e) {
			showHiveView(selectedPath, "#infoAccordionHiveInner");
		});

	});
	</script>
	
	<g:if test="${getThumbnail}">
		<script type="text/javascript">
			$(function() {
				requestThumbnailImageForInfoPane();
			});
		</script>
	</g:if>
	<g:else>
		<script type="text/javascript">
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
</div>
</div>
