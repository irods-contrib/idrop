<div id="aclSharingPanel">
	<div id="aclSharingPanelTopSection">
		<div id="sharingToolbar" class="well btn-toolbar">
			<g:if
				test="${grailsApplication.config.idrop.config.use.sharing==true && irodsSharedFileOrCollection != null}">
				<div>

					<h4>This file is marked as a share, and appears with the name: 
					${irodsSharedFileOrCollection.shareName} for users with access rights</h4>

				</div>
			</g:if>
			<div id="sharingMenu">

				<div class="btn-group">
					<g:if
						test="${grailsApplication.config.idrop.config.use.sharing==true && irodsSharedFileOrCollection != null}">
						<button onclick="editShareAtPath()">
							<g:message code="text.edit.share" />
						</button>
						<button onclick="removeShareAtPath()">
							<g:message code="text.remove.share" />
						</button>
					</g:if>
					<g:if
						test="${grailsApplication.config.idrop.config.use.sharing==true && irodsSharedFileOrCollection == null}">
						<button onclick="addShareAtPath()">
							<g:message code="text.add.share" />
						</button>
					</g:if>
				</div>

			</div>
		</div>



	</div>
</div>
