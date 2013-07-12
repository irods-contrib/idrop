<div id="aclSharingPanel">
	<div id="aclSharingPanelTopSection">
		<div id="sharingToolbar" class="well btn-toolbar">
			<g:if
				test="${grailsApplication.config.idrop.config.use.sharing==true && irodsSharedFileOrCollection != null}">
				<div>

					<h4>This file is marked as a share, and appears with the name: 
					<strong>${irodsSharedFileOrCollection.shareName}</strong> for users with access rights</h4>

				</div>
			</g:if>
			<div id="sharingMenu">

				<div class="btn-group">
					<g:if
						test="${grailsApplication.config.idrop.config.use.sharing==true && irodsSharedFileOrCollection != null}">
						<button type="button" onclick="editShareAtPath()">
							<g:message code="text.edit.share" />
						</button>
						<button onclick="removeShareAtPath()">
							<g:message code="text.remove.share" />
						</button>
					</g:if>
					<g:if
						test="${grailsApplication.config.idrop.config.use.sharing==true && irodsSharedFileOrCollection == null}">
						<h4>This file is not currently marked as a share.  Marking as a share will allow users with access rights to see this collection as shared with them</h4>
						
						<button onclick="addShareAtPath()">
							<g:message code="text.add.share" />
						</button>
					</g:if>
				</div>

			</div>
		</div>



	</div>
</div>
