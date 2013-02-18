<g:form name="aclDetailsForm" action="" id="aclDetailsForm">
	<table cellspacing="0" cellpadding="0" border="0" id="aclDetailsTable"
		style="width: 100%;" class="table table-striped table-hover" >
		<thead>
			<tr>
				<th></th>
				<th><g:message code="text.user" /></th>
				<th><g:message code="text.share.type" /></th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${acls}" var="acl">
				<tr id="${acl.nameWithZone}">
					<td><g:checkBox name="selectedAcl" value="${acl.nameWithZone}" checked="false"/>
					</td>
					<td>
						${acl.nameWithZone}
					</td>
					<td class="forSharePermission" id="${acl.nameWithZone}">
						${acl.filePermissionEnum}
					</td>

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
	</g:form>
</div>