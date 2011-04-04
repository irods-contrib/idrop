
	<div>
		<table cellspacing="0" cellpadding="0" border="0"
			id="browseDataDetailsTable"  style="width: 100%;">
			<thead>
				<tr>
					<th></th>
					<th>Name</th>
					<th>Type</th>
					<th>Modified date</th>
					<th>Length</th>
				</tr>
			</thead>
			<tbody>
				<g:each in="${collection}" var="entry">
					<tr id="${entry.formattedAbsolutePath}">
						<td><span class="ui-icon-circle-plus browse_detail_icon ui-icon"></span></td>
						<td>
							<g:if test="${entry.objectType.toString() == 'COLLECTION'}">
							${entry.nodeLabelDisplayValue}
							</g:if>
							<g:else>
							
							<g:link url="${'file/download' + entry.formattedAbsolutePath}">${entry.nodeLabelDisplayValue}</g:link>
							</g:else>
						</td>

						<td>
							${entry.objectType}
						</td>
						<td>
							${entry.modifiedAt}
						</td>
						<td>
							${entry.dataSize}
						</td>
					</tr>
				</g:each>

			</tbody>

			<tfoot>
				<tr>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</tfoot>
		</table>
	</div>
<script>

	var dataTable;

	$(function() {
	
	
		dataTable = lcBuildTableInPlace("#browseDataDetailsTable", browseDetailsClick, ".browse_detail_icon");
		
	});

	function browseDetailsClick(minMaxIcon) {
		var nTr = minMaxIcon.parentNode.parentNode;

		if (minMaxIcon.parentNode.innerHTML.match('circle-minus')) {
			lcCloseTableNodes(dataTable);
		} else {
			try {
				browseDataDetailsFunction(minMaxIcon, nTr);
			} catch (err) {
				console.log("error in detailsClick():" + err);
			}

		}
	}

	function browseDataDetailsFunction(clickedIcon, rowActionIsOn) {
		/* Open this row */
		prepareForCall();
		lcCloseTableNodes(dataTable);
		// nTr points to row and has absPath in id
		var absPath = $(rowActionIsOn).attr('id');
		//alert("absPath:" + absPath);
		var detailsId = "details_" + absPath;
		var detailsHtmlDiv = "details_html_" + absPath;

		clickedIcon.setAttribute("class", "ui-icon ui-icon-circle-minus");
		newRowNode = dataTable.fnOpen(rowActionIsOn,
				askForBrowseDetailsPulldown(absPath, detailsId), 'details');
		newRowNode.setAttribute("id", detailsId);
		
	}

	function buildDetailsLayout(detailsId) {
		var td = document.createElement("TD");
		td.setAttribute("colspan", "4");

		var detailsPulldownDiv = document.createElement("DIV");
		detailsPulldownDiv.setAttribute("id", detailsId);
		detailsPulldownDiv.setAttribute("class", "detailsPulldown");

		td.appendChild(detailsPulldownDiv);

		return $(td).html();
	}

	function askForBrowseDetailsPulldown(absPath, detailsId) {
		var url = "/browse/displayPulldownDataDetails";
		var params = {
				absPath:absPath
			}
			
		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, ".details",
				null);
		
	}
	
	</script>