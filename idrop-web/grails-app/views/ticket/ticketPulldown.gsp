<div id="ticketPulldownDiv">
	<g:hiddenField name='irodsAbsolutePath' id='ticketDetailsAbsPath' value='${ticket.irodsAbsolutePath}'/>
	
			<div id="container" style="height:100%;width:100%;">
					
					<div>
						<div style="width:20%;"><label><g:message code="text.ticket.user" />:</label></div>
						<div>${ticket.ownerName}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.owner.zone" />:</label></div>
						<div>${ticket.ownerZone}</div>
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
					<div>
						<div><label><g:message code="text.ticket.url" />:</label></div>
						<div><a href="${ticketDistribution.ticketURL}">${ticketDistribution.ticketURL}</a></div>
					</div>
				</div>		
</div>
<script>
</script>
