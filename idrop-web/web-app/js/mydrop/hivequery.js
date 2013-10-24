function hiveQueryByTerm(searchTerm) {
	if (!searchTerm) {
		setErrorMessage(jQuery.i18n.prop('msg_search_missing'));
		return false;
	}
	
	var params = {
			uri:searchTerm
		}
	
	var getUrl = "/sparqlQuery/searchByTerm";
	 showBlockingPanel();
	
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

function hiveQueryByRelatedTerm(searchTerm) {
	if (!searchTerm) {
		setErrorMessage(jQuery.i18n.prop('msg_search_missing'));
		return false;
	}
	
	var params = {
			uri:searchTerm
		}
	
	var getUrl = "/sparqlQuery/searchByRelatedTerm";
	 showBlockingPanel();
	
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
	
	 showBlockingPanel();
	
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
	unblockPanel();


}

function hiveQuerySparqlReturnNewWindow(searchTerm) {
	if (!searchTerm) {
		setErrorMessage(jQuery.i18n.prop('msg_search_missing'));
		return false;
	}
	
	var params = {
			query:searchTerm
		}
	
	var getUrl = "/sparqlQuery/searchSparql";
	$("#resultsTabInner").html("");
	 showBlockingPanel();
	
	$.post(context + getUrl, params, function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}
		
		$("#resultsTabInner").html(data);
		unblockPanel();
		$('#searchTabs a[href="#resultsTab"]').tab('show');
	}, "html").error(function(xhr, status, error) {
		$(resultDiv).html("");
		setErrorMessage(xhr.responseText);
		unblockPanel();
	});
	
}

/**
 *  render a new page show the concept browser
 */
function navConceptBrowser(index) {
	console.log("navConceptBrowser()");
	
	var params = {
			index:index
			}
	
	lcSendValueWithParamsAndPlugHtmlInDiv("/sparqlQuery/showConceptBrowser", params, "", function(data) {
		showConceptBrowserWindow(data);
		} );		
}

/**
 * Hide the hive query form view and show the concept browser view
 * @param data
 */
function showConceptBrowserWindow(data) {
	console.log("showConcpetBrowserWindow()");
	$("#hiveQueryForm").hide("slow");
	$("#conceptBrowserWindows").html(data).show("slow");
}

function selectVocabularies() {
	var formData = $("#hiveQueryVocabularyForm").serializeArray();
	if (formData == null) {
		setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
		return false;
	}

	lcShowBusyIconInDiv("#conceptBrowserWindows");

	var jqxhr = $.post(context + "/sparqlQuery/selectVocabularies", formData,
			function(data, status, xhr) {
			}, "html").success(function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}

		$("#conceptBrowserWindows").html(data);

	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	});

}

function setLetter (indexLetter, index) {
	
	var params = {
		indexLetter:indexLetter,
		index: index
		
	}
	console.log("setLetter()");
	
	lcSendValueWithParamsAndPlugHtmlInDiv("/sparqlQuery/conceptBrowserPivotView", params, "#conceptBrowserPivotContainer", null);
	
}

/**
 * Set the concept browser view to the given uri
 * 
 * @param uri
 * @returns
 */
function browseToUri(uri, index) {

	if (uri == null) {
		setErrorMessage(jQuery.i18n.prop('msg_no_uri'));
		return false;
	}
	
	if(index == null) {
		//setErrorMessage("index is missing"); // FIXME:i18n
		console.log("index is missing");
	}

	try {
		var params = {
			targetURI : uri,
			index: index
		}

		lcSendValueWithParamsAndPlugHtmlInDiv("/sparqlQuery/conceptBrowserPivotView", params,
				"#conceptBrowserPivotContainer", null);

	} catch (err) {
		setErrorMessage(err);
		setHiveNoData();
	}

}

/**
 * Set the vocabulary to the top level of the provided vocabulary name. If no
 * vocabulary is specified, the controller will pick the first of the selected
 * vocabularies to display
 *
 * @param vocabulary
 *            HIVE vocabulary name
 */
function resetVocabulary(vocabulary, index) {

	try {
		var params;
		if (vocabulary == null) {
			params = {
				//absPath : absPath
				index: index
			}
		} else {
			params = {
				vocabulary : vocabulary,
				index: index
			}
		}
		lcSendValueWithParamsAndPlugHtmlInDiv("/sparqlQuery/resetConceptBrowser",
				params, "#conceptBrowserPivotContainer", function(data) {
					$("#conceptBrowserPivotContainer").html(data);
				}, function() {
					setHiveNoData();
				});
	} catch (err) {
		setErrorMessage(err);
		setHiveNoData();
	}

}

function selectHiveTerm(vocabName, preLabel, termUri, index) {
	if (vocabName == null) {
		setErrorMessage(jQuery.i18.prop('msg_no_vocabulary'));
		return false;
	}
	
	if(preLabel == null) {
		setErrorMessage(jQuery.i18.prop('msg_no_preLabel???'));
		return false;
	}
	
	if(termUri == null) {
		setErrorMessage(jQuery.i18n.prop('msg_uri_missing'));
		return false;
	}
	
	if(index == null || index == "") {
		//setErrorMessage("index is missing"); // FIXME:i18n
		console.log("index is missing");
	}
	

	showBlockingPanel();
	
	var params = {
			vocabulary: vocabName,
			preLabel: preLabel,
			uri: termUri,
			index: index
	}
	
	var jqxhr = $.post(context + "/sparqlQuery/pickupHiveTerm", params,
			function(data, status, xhr) {
			}, "html").success(function(data, status, xhr) {
				var continueReq = checkForSessionTimeout(data, xhr);
				if (!continueReq) {
					return false;
				} 
				$("#conceptBrowserWindows").html("").hide("slow");
				$("#hiveQueryView").html(data);
				//$("#hiveQueryForm").html(data);
				//$("#hiveSearch").html(data)
				$("#hiveQueryForm").show("slow");
				setMessage(jQuery.i18n.prop('msg_update_successful'));
				unblockPanel();
				
	}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
        unblockPanel();
	});
}

	function addNewRow(div_id) {	
		console.log("addNewRow: " + div_id);
		params = {
				div_id: div_id
		}
	
		
		lcSendValueWithParamsAndPlugHtmlInDiv("/sparqlQuery/addMoreRows", params, "#"+div_id, null);
	}
	

	function deleteSelectedSearchTerm(index) {

		if(index == null || index == "") {
			setErrorMessage("index is missing"); // FIXME:i18n
		}
		
		params = {
				index: index
		}
		
		lcSendValueWithParamsAndPlugHtmlInDiv("/sparqlQuery/deleteSearchTerm", params,
				"#hiveQueryForm", null);
		
	}
	
	
	function showEditPanel(index) {
		
		if(index == null || index == "") {
			setErrorMessage("index is missing"); // FIXME:i18n
		}
		
		params = {
			index: index
		}
		
		console.log(params);
		lcSendValueWithParamsAndPlugHtmlInDiv("/sparqlQuery/showEditablePanel", params, "#"+index, null);
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
