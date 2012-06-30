

<div id="container" class="roundedContainer" style="height:100%;width:100%;">
		
				<div >
					<div style="width:20%;"><label><g:message code="text.id" /></label></div>
					<div>${auditedAction.objectId}</div>
				</div>
				<div>
					<div><label><label><g:message code="text.user" /></label></div>
					<div><div>${auditedAction.userName}</div></div>
				</div>
				<div>
					<div><label><label><g:message code="text.audit.code" /></label></div>
					<div><div>${auditedAction.auditActionEnum.auditCode} - ${auditedAction.auditActionEnum.textValue}</div></div>
				</div>
				<div>
					<div><label><label><g:message code="text.description" /></label></div>
					<div><div>${auditedAction.auditActionEnum.meaning}</div></div>
				</div>
				<div>
					<div><label>${auditedAction.auditActionEnum.commentContent}</label></div>
					<div>${auditedAction.comment}</div>
				</div>
				<div>
					<div><label><label><g:message code="text.timestamp" /></label></div>
					<div><div>${auditedAction.createdAt}</div></div>
				</div>
				
				
</div>
		
