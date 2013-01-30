 <%@page import="org.irods.jargon.core.query.MetaDataAndDomainData" %>
 <%@page import="org.irods.jargon.usertagging.domain.IRODSSharedFileOrCollection" %>
<table class="table table-striped table-hover">
	<thead>
		<tr>
			<th></th>
			<th><g:message code="text.actions" /></th>
			<th><g:message code="text.name" /></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${listing}" var="entry">
			<tr>
				<g:if test="${entry.metadataDomain == MetaDataAndDomainData.MetadataDomain.COLLECTION}">
					<td></td>
					<td><span class="setPaddingLeftAndRight"><g:link controller="browse" action="index" params="[mode: 'path', absPath: entry.domainUniqueName]"><i class="icon-folder-open "></i></g:link></span>
					<span class="setPaddingLeftAndRight"><i class="icon-upload " onclick="quickviewUpload('${entry.domainUniqueName}')"></i></span></td>
					<td>${entry.shareName}</td>
				</g:if>
				<g:else>
					<td></td>
					<td><span class="setPaddingLeftAndRight"><g:link controller="browse" action="index" params="[mode: 'path', absPath: entry.domainUniqueName]"><i class="icon-folder-open "></i></g:link></span><span class="setPaddingLeftAndRight"><g:link url="${'file/download' + entry.domainUniqueName}"><i class="icon-download "></i></g:link></span></td>
					<td>${entry.shareName}</td>
				</g:else>
			</tr>
		</g:each>
	</tbody>

</table>
<script>

/**
* Show the uplaod dialog using the hidden path in the info view
*/
function quickviewUpload(path) {
	if (path == null) {
		showErrorMessage(jQuery.i18n.prop('msg.path.missing'));
		return false;
	}

	showUploadDialogUsingPath(path);

	
}
</script>