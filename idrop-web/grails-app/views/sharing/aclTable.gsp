<g:form name="aclDetailsForm" action="" id="aclDetailsForm">
	<table cellspacing="0" cellpadding="0" border="0" id="aclDetailsTable"
		style="width: 100%;">
		<thead>
			<tr>
				<th></th>
				<th>Person</th>
				<th>Access</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${acls}" var="acl">
				<tr id="${acl.userName}">
					<td><g:checkBox name="selectedAcl" value="${acl.userName}" checked="false"/>
					</td>
					<td>
						${acl.userName}
					</td>
					<td class="forSharePermission" id="${acl.userName}">
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