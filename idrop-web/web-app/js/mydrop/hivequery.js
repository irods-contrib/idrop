

function hiveQueryByTerm(searchTerm) {
	if (!searchTerm) {
		setErrorMessage(jQuery.i18n.prop('msg_search_missing'));
		return false;
	}
	
	var params = {
			uri:searchTerm
		}
	
	var getUrl = "/sparqlQuery/searchByTerm";
	
	$.get(context + getUrl, params, function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}
		
		processHiveQueryResults(data);
		unblockPanel();
		$('#searchTabs a[href="#resultsTab"]').tab('show');
	}, "html").error(function(xhr, status, error) {
		$(resultDiv).html("");
		setErrorMessage(xhr.responseText);
		unblockPanel();
	});
	
	
	
}

function processHiveQueryResults(data) {
	
	if (data == null || data == '') {
		$("#resultsTabInner").html("");
		setErrorMessage(jQuery.i18n.prop('msg_search_unsuccessful'))
	}
	
	var myObject = $.parseJSON(data);
	
	// build first part of result table...
	
	var html = "<table id=\"searchResultTable\" class=\"table table-striped table-hover\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><thead><tr><th></th><th>iRODS Path</th><th>VocabularyInfo</th></tr></thead>";
	
	html += "<tbody>";
	
	if (myObject.results != null && myObject.results.bindings != null) {
	
	$.each(myObject.results.bindings, function(index, value) {

		html += "<tr><td>";
		
		if (value.infoLink != null && value.infoLink.value != null) {
			//html += "<a href='" + value.infoLink.value + "'><i class=\"icon-folder-open\"></i></a>";
			html += "<a href='" + context + "/browse/index?mode=path&absPath=" + encodeURIComponent(value.absPath.value) +"'><i class=\"icon-folder-open\"></i></a>";
		}
		
		if (value.weblink != null && value.weblink.value != null) {
			html += "<a href='" + value.weblink.value + "'><i class=\"icon-download\"></i></a>";
		}
		
		
		html += "</td><td>"; 
			
		html += value.absPath.value;
		html += "</td><td>";
		
		if (value.y != null && value.y.value != null) {
			html += value.y.value;
		}
		
		html += "</td>";
		html += "</tr>";
	
	}
			
	); 
}
	
	html += "</tbody><tfoot><tr><td></td><td></td><td></td></tr></tfoot></table>";
	
	$("#resultsTabInner").html(html);


}

/*
<div>

	
	<tbody>
		<g:each in="${results}" var="entry">

			<tr id="${entry.formattedAbsolutePath}">

				<td><span
					class="ui-icon-circle-plus search-detail-icon  ui-icon"></span></td>
				<td><g:checkBox name="selectDetail"
						value="${entry.formattedAbsolutePath}" checked="false" /> <span
					class="setPaddingLeftAndRight"><g:link target="_blank"
							controller="browse" action="index"
							params="[mode: 'path', absPath: entry.formattedAbsolutePath]">
							<i class="icon-folder-open "></i>
						</g:link></span></td>
				<td>
					${entry.nodeLabelDisplayValue}
				</td>
				<td>
					${entry.objectType}
				</td>
				<td>
					${entry.displayDataSize}
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
			<td></td>
		</tr>
	</tfoot>
</table>
</div>
*/
