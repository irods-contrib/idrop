
<g:render template="/common/panelmessages" />

<div id="browseDetailsMessageArea">
	<!--  -->
</div>

<div id=browseDetailsDialogArea">
	<!--  area for generating dialogs -->
</div>

<div>
	<div id="idropLiteArea">
		<!--  area to show idrop lite applet -->
	</div>
	<div id="toggleHtmlArea">
		<g:render template="/common/browseDetailsBrowseToolbar" />
		<fieldset id="verticalForm">
			<label>Collection Name:</label>
			${parent.collectionName}
			<g:hiddenField id="browseDetailsAbsPath" name="browseDetailsAbsPath"
				value="${parent.collectionName}" />
		</fieldset>
		<div id="infoDialogArea">
			<!--  no empty divs -->
		</div>
		<div id="detailsTopSection" class="box">
			<div id="detailsToolbar" class="fg-toolbar ui-widget-header">
				<div id="detailsMenu" class="fg-buttonset fg-buttonset-multi"
					style="float: left">

					<!-- FIXME: i18n -->

					<button type="button" class="ui-state-default ui-corner-all"
						id="doBulkAction" value="doBulkAction" onclick="bulkAction()")>
						<g:message code="text.bulk.action" />
					</button>

					<g:select name="bulkAction" id="bulkAction"
						from="${['Add to cart', 'Delete']}" />

					<button type="button" id="upload"
						class="ui-state-default ui-corner-all" value="upload"
						onclick="showUploadDialog()")>
						<g:message code="text.upload" />
					</button>

					<g:if test="${showLite}">
						<button type="button" id="idroplite"
							class="ui-state-default ui-corner-all"
							value="uploadWithIdropLite" onclick="showIdropLite()")>
							<g:message code="text.idrop.lite" />
						</button>
					</g:if>

				</div>
			</div>
			<table cellspacing="0" cellpadding="0" border="0"
				id="browseDataDetailsTable" style="width: 100%;">
				<thead>
					<tr>
						<th></th>
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
							<td><span
								class="ui-icon-circle-plus browse_detail_icon ui-icon"></span>
							</td>
							<td><g:checkBox name="selectDetail"
									value="${entry.formattedAbsolutePath}" checked="false" /></td>
							<td><g:if
									test="${entry.objectType.toString() == 'COLLECTION'}">
									${entry.nodeLabelDisplayValue}
								</g:if> <g:else>
									<g:link url="${'file/download' + entry.formattedAbsolutePath}">
										${entry.nodeLabelDisplayValue}
									</g:link>
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
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
</div>
<script>

	var dataTable;

	$(function() {
		dataTable = lcBuildTableInPlace("#browseDataDetailsTable", browseDetailsClick, ".browse_detail_icon");
		$("#infoDiv").resize();
	});

	/* click twistie to open details table info */
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

	/** called by browseDetailsClick() when it is decided that the details table row should be opened, go 
	to server and get the details.
	*/
	function browseDataDetailsFunction(clickedIcon, rowActionIsOn) {
		/* Open this row */
		lcPrepareForCall();
		lcCloseTableNodes(dataTable);
		// nTr points to row and has absPath in id
		var absPath = $(rowActionIsOn).attr('id');
		//alert("absPath:" + absPath);
		var detailsId = "details_" + absPath;
		var detailsHtmlDiv = "details_html_" + absPath;
		var buildDetailsLayoutVal = buildDetailsLayout(detailsId);
		clickedIcon.setAttribute("class", "ui-icon ui-icon-circle-minus");
		newRowNode = dataTable.fnOpen(rowActionIsOn,
				buildDetailsLayoutVal, 'details');
		newRowNode.setAttribute("id", detailsId);
		askForBrowseDetailsPulldown(absPath, detailsId)
		
	}

	/** The table row is being opened, and the query has returned from the server with the data, fill in the table row
	*/
	function buildDetailsLayout(detailsId) {
		var td = document.createElement("TD");
		td.setAttribute("colspan", "4");

		var detailsPulldownDiv = document.createElement("DIV");
		detailsPulldownDiv.setAttribute("id", detailsId);
		detailsPulldownDiv.setAttribute("class", "detailsPulldown");
		var img = document.createElement('IMG');
		img.setAttribute("src", context + "/images/ajax-loader.gif");
		detailsPulldownDiv.appendChild(img);
		td.appendChild(detailsPulldownDiv);
		return $(td).html();
	}

	function askForBrowseDetailsPulldown(absPath, detailsId) {
		
		var url = "/browse/fileInfo";
		absPath = absPath;
		var params = {
				absPath:absPath
			}
			
		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, ".details",
				null);
		
	}
	
	</script>