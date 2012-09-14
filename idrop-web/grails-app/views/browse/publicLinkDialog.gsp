<div class="roundedContainer" id="publicLinkDialog">
	<g:hiddenField name="publicLinkDialogAbsPath"  id="publicLinkDialogAbsPath" value = "${absPath}" />
	<div style="clear: both;">
			<g:if test="${accessSet == true}">
			<h1><g:message code="heading.anonymous.access.set" /></h1>
			<fieldset id="verticalForm">
			
				
				<g:textArea name="url" rows="4" columns="200" value="${accessUrlString}"/>
				<br />
				<div id="publicLinkDialogMenu" class="fg-buttonset fg-buttonset-multi"
							style="float: right; clear :   both; width: 90%;">
							<button type="button" id="cancelPublicLinkButton"
								class="ui-state-default ui-corner-all" value="cancelPublicLink"
								onclick="closePublicLinkDialog()")><g:message code="text.cancel" /></button>
						</div>
				
			</fieldset>
			</g:if>
			<g:else>
			<h1><g:message code="heading.anonymous.access.enable" /></h1>
				<fieldset id="verticalForm">
					<br />
					<div id="publicLinkDialogToolbar" >
						<div id="publicLinkDialogMenu" class="fg-buttonset fg-buttonset-multi"
							style="float: right; clear :   both; width: 90%;">
							<button type="button" id="grantLinkButton"
								class="ui-state-default ui-corner-all" value="grant"
								onclick="grantPublicLink()")><g:message code="text.grant" /></button>
							<button type="button" id="cancelPublicLinkButton"
								class="ui-state-default ui-corner-all" value="cancelPublicLink"
								onclick="closePublicLinkDialog()")><g:message code="text.cancel" /></button>
						</div>
					</div>
					
					
					
					
					
				</fieldset>
			</g:else>
			
	</div>
</div>
