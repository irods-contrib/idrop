<div id="ticketDialogDiv" class="roundedContainer">
ticket dialog here...
	
	
		<form id="ticketDialogForm" name="ticketDialogForm">
	
			<g:hiddenField name='create' id='create' value='${create}'/>
	
			<div id="container" style="height:100%;width:100%;">
					<div>
						<div style="width:20%;"><label><g:message code="text.ticket.string" />:</label></div>
						<div>
						<g:if test="${create}">
						<div><g:textField id="ticketString" name="ticketString"
						value="${ticket.ticketString}" /></div>
						</g:if>
						<g:else>
						<g:hiddenField name='ticketString' id='ticketString' value='${ticket.ticketString}'/>
						${ticket.ticketString}
						</g:else>
						</div>
					</div>
					<g:if test="${create==false}">
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
						<div>${ticket.type}</div>
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
						<div>${ticket.usesLimit}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.byte.count" />:</label></div>
						<div>${ticket.writeByteCount}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.byte.limit" />:</label></div>
						<div>${ticket.writeByteLimit}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.file.count" />:</label></div>
						<div>${ticket.writeFileCount}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.file.limit" />:</label></div>
						<div>${ticket.writeFileLimit}</div>
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