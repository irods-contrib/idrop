
	<table cellspacing="0" cellpadding="0" border="0"
		id="auditDetailsTable" style="width: 100%;">
		<thead>
			<tr>
				 <th></th>
				<th><g:message code="text.action" /></th>
				<th><g:message code="text.user" /></th>
				<th><g:message code="text.timestamp" /></th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${auditedActions}" var="entry">
				<tr id="${entry.count}">
					  <td><span
                  class="ui-icon-circle-plus browse_detail_icon ui-icon"></span></td>
					<td>${entry.auditActionEnum.textValue}</td>
					<td>${entry.userName}</td>
					<td>${entry.createdAt}</td>
					<g:hiddenField id="${ 'audit_' + entry.count + '_id'}" name="${ 'audit_' + entry.count + '_id'}"  value="${entry.count}"/>
					<g:hiddenField id="${ 'audit_' + entry.count + '_code'}" name="${ 'audit_' + entry.count + '_code'}" value="${entry.auditActionEnum.auditCode}"/>
					<g:hiddenField id="${ 'audit_' + entry.count + '_timestamp'}" name="${ 'audit_' + entry.count + '_timestamp'}" value="${entry.timeStampInIRODSFormat}"/>
				</tr>
			</g:each>
		</tbody>
		<tfoot>
			<tr>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</tfoot>
	</table>
	<script>
	$(function() {
		
		 pageableForward = "${pageableForward}";
		 pageableBackwards = "${pageableBackwards}";

		 firstCount = "${firstCount}";
		 lastCount = "${lastCount}";
		 
		 if (pageableForward == true) {
				$('#forwardAuditButton').show();
			} else {
				$('#forwardAuditButton').hide();
			}
		
			if (pageableBackwards == true) {
				$('#backwardAuditButton').show();
			} else {
				$('#backwardAuditButton').hide();
			}
		
	});
	
	</script>
	