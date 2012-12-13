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
					<td></td>
					<td><span class="setPaddingLeftAndRight"><i class="icon-folder-open "></i></span><span class="setPaddingLeftAndRight"><i class="icon-upload "></i></span></td>
					<td>${entry.domainUniqueName}</td> <td>${entry.description}</td>
				</g:if>
				<g:else>
					<td></td>
					<td><span class="setPaddingLeftAndRight"><i class="icon-folder-open "></i></span><span class="setPaddingLeftAndRight"><i class="icon-download "></i></span></td>
					<td>${entry.domainUniqueName}</td> <td>${entry.description}</td>
				</g:else>
			</tr>
		</g:each>
	</tbody>

</table>