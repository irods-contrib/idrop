<div id="ticketDialogDiv" class="roundedContainer">
		<form id="ticketDialogForm" name="ticketDialogForm">
	
			<g:hiddenField name='create' id='create' value='${ticket.create}'/>
			<g:hiddenField name='irodsAbsolutePath' id='ticketDetailsAbsPath' value='${ticket.irodsAbsolutePath}'/>
	
			<div id="container" style="height:100%;width:100%;">
					<div>
						<div style="width:20%;"><label><g:message code="text.ticket.string" />:</label></div>
						<div>
						<g:if test="${ticket.create}">
						<div><g:textField id="ticketString" name="ticketString"
						value="${ticket.ticketString}" /></div>
						</g:if>
						<g:else>
						<g:hiddenField name='ticketString' id='ticketString' value='${ticket.ticketString}'/>
						${ticket.ticketString}
						</g:else>
						</div>
					</div>
					<g:if test="${ticket.create==false}">
					<div>
						<div><label><g:message code="text.ticket.object.type" />:</label></div>
						<div>${ticket.objectType}</div>
					</div>
					
					<div>
						<div><label><g:message code="text.ticket.user" />:</label></div>
						<div>${ticket.ownerName}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.owner.zone" />:</label></div>
						<div>${ticket.ownerZone}</div>
					</div>
					</g:if>
					<div>
						<div><label><g:message code="text.ticket.type" />:</label></div>
						<div><g:select name="type" id="type" from="${['READ', 'WRITE']}" value="${ticket.type}" /></div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.expire.time" />:</label></div>
						<div>${ticket.expireTime}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.uses.count" />:</label></div>
						<div>${ticket.usesCount}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.uses.limit" />:</label></div>
						<div><g:textField id="usesLimit" name="usesLimit" value="${ticket.usesLimit}" /></div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.byte.count" />:</label></div>
						<div>${ticket.writeByteCount}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.byte.limit" />:</label></div>
						<div><g:textField id="writeByteLimit" name="writeByteLimit" value="${ticket.writeByteLimit}" /></div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.file.count" />:</label></div>
						<div>${ticket.writeFileCount}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.file.limit" />:</label></div>
						<div><g:textField id="writeFileLimit" name="writeFileLimit"
						value="${ticket.writeFileLimit}" /></div>
					</div>
				</div>	
			</form>
			<div id="detailsDialogMenu" class="fg-buttonset fg-buttonset-multi"
						style="float: left, clear :   both; width: 90%;">
						<button type="button" id="updateTicketDetailButton"
							class="ui-state-default ui-corner-all" value="update  Ticket"
							onclick="submitTicketDialog()")><g:message code="default.button.save.label" /></button>
						<button type="button" id="cancelAddTicketButton"
							class="ui-state-default ui-corner-all" value="cancelAdd"
							onclick="closeTicketDialog()")><g:message code="default.button.cancel.label" /></button>
			</div>
	</div>