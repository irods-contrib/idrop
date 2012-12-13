 <%@page import="org.irods.jargon.core.query.MetaDataAndDomainData" %>
<table class="table table-striped table-hover">
	<thead>
		<tr>
			<th></th>
			<th>Action</th>
			<th>Name</th>
			<th>Description</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${listing}" var="entry">
			<tr>
				<g:if test="${entry.metadataDomain == MetaDataAndDomainData.MetadataDomain.COLLECTION}">
					<td><g:img dir="images" file="folder_icon.png" width="20"
								height="20" /></td>
					<td><i class="icon-folder-open"></i><i class="icon-upload"></i></td>
					<td>${entry.domainUniqueName}</td> <td>${entry.description}</td>
				</g:if>
				<g:else>
					<td><g:img dir="images" file="file.png" width="20"
								height="20" /></td>
					<td><i class="icon-folder-open"></i><i class="icon-downloadload"></i></td>
					<td>${entry.domainUniqueName}</td> <td>${entry.description}</td>>
				</g:else>
			</tr>
		</g:each>
	</tbody>

</table>