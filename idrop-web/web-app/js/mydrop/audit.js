/**
 * Javascript for audit functions
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var
 */
var auditLoadUrl = '/audit/auditTable';
var auditTable;


/**
 * Reload the audit details as a JQuery data table using an AJAX call to get
 * the data
 * 
 * @returns
 */
function reloadAuditTable(absPath, usePageSize, useOffset) {
	
	if (absPath == null) {
		absPath = selectedPath;
	}
	
	if (usePageSize == null) {
		usePageSize = 1000;
	}
	
	if (useOffset == null) {
		useOffset = 0;
	}

	$("#auditTableDiv").empty();
	lcShowBusyIconInDiv("#auditTableDiv");
	
	var params = {
		absPath : absPath,
		pageSize : usePageSize,
		offset : useOffset
	}

	var jqxhr = $.get(context + auditLoadUrl, params,
			function(data, status, xhr) {
				$('#auditTableDiv').html(data);
			}, "html").error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	}).success(function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}
		auditTable = buildAuditTableInPlace();
	});
}

/**
 * Given that the audit details table data has been retrieved (as html
 * table), make it a dataTable
 * 
 * @returns
 */
function buildAuditTableInPlace() {

	  tableParams = {"bJQueryUI" : true,
          	"bLengthChange": false,
          	"bFilter": false,
          	"iDisplayLength" : 500

          }
	var auditDataTable = lcBuildTableInPlace("#auditDetailsTable", auditDetailsClick, ".browse_detail_icon", tableParams);
	 
		
		
	return auditDataTable;
}

/* click twistie to open details table info */
function auditDetailsClick(minMaxIcon) {

   		var dataTable = $("#auditDetailsTable").dataTable();
        var nTr = minMaxIcon.parentNode.parentNode;

        if (minMaxIcon.parentNode.innerHTML.match('circle-minus')) {
                lcCloseTableNodes(dataTable);
        } else {
                try {
                        browseAuditDetailsFunction(minMaxIcon, nTr);
                } catch (err) {
                        setErrorMessage("error in auditDetailsClick():" + err);
                }

        }
}

/** called by auditDetailsClick() when it is decided that the details table row should be opened, go 
to server and get the details.
*/
function browseAuditDetailsFunction(clickedIcon, rowActionIsOn) {
		var dataTable = $("#auditDetailsTable").dataTable();
        /* Open this row */
        lcCloseTableNodes(dataTable);
        // nTr points to row and has absPath in id
        var objId = $(rowActionIsOn).attr('id');
        //alert("absPath:" + absPath);
        var detailsId = "details_" + objId;
        var detailsHtmlDiv = "details_html_" + objId;
        var buildDetailsLayoutVal = buildAuditDetailsLayout(detailsId);
        clickedIcon.setAttribute("class", "ui-icon ui-icon-circle-minus");
        newRowNode = dataTable.fnOpen(rowActionIsOn,
                        buildDetailsLayoutVal, 'details');
        newRowNode.setAttribute("id", detailsId);
        var absPath = $("#auditDetailsAbsPath").val();
        
        // get the audit action and timestamp from the hidden fields
        var auditActionSelector = '#audit_' + objId + '_code';
        var auditAction = $(auditActionSelector).val();
        
        var timestampSelector = '#audit_' + objId + '_timestamp';
        var timestamp = $(timestampSelector).val();
        
        askForAuditDetailsPulldown(absPath, auditAction, timestamp);

}

/** The table row is being opened, and the query has returned from the server with the data, fill in the table row
*/
function buildAuditDetailsLayout(detailsId) {
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

function askForAuditDetailsPulldown(absPath, auditAction, timestamp) {

        var url = "/audit/auditInfo";
        absPath = absPath;
        var params = {
                        absPath:absPath,
                        actionCode:auditAction,
                        timeStamp:timestamp
                }
	
        lcSendValueWithParamsAndPlugHtmlInDiv(url, params, ".details",
                        null);

}
