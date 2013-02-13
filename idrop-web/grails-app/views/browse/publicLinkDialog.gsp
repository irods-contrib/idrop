<div class="well" id="publicLinkDialog">
	<g:hiddenField name="publicLinkDialogAbsPath" id="publicLinkDialogAbsPath" value = "${absPath}" />
	<div>
			<g:if test="${accessSet == true}">
			
				<div class="alert alert-info">
	 				<g:message code="heading.anonymous.access.set" />
				</div>
				
			<fieldset>
				<g:textArea name="url" rows="4" columns="200" value="${accessUrlString}"/>
				<br />
				<div id="publicLinkDialogMenu" 
							class="pull-right">
							<button type="button" id="cancelPublicLinkButton"
								 value="cancelPublicLink"
								onclick="closePublicLinkDialog()")><g:message code="text.cancel" /></button>
						</div>
				
			</fieldset>
			</g:if>
			<g:else>
				<div class="alert alert-info">
	 				<g:message code="heading.anonymous.access.enable" />
				</div>
				
				<fieldset>
					<br />
					<div id="publicLinkDialogToolbar" >
						<div id="publicLinkDialogMenu" 
							class="pull-right">
							<button type="button" id="grantLinkButton"
								 value="grant"
								onclick="grantPublicLink()")><g:message code="text.grant" /></button>
							<button type="button" id="cancelPublicLinkButton"
								 value="cancelPublicLink"
								onclick="closePublicLinkDialog()")><g:message code="text.cancel" /></button>
						</div>
					</div>
				</fieldset>
			</g:else>
			
	</div>
</div>
