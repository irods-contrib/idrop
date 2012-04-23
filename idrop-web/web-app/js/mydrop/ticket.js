/**
 * Javascript for ticket functions
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var
 */

var ticketTableUrl = '/ticket/listTickets';

function reloadTicketTable(absPath) {
	if (absPath == null) {
		throw "null absPath";
	}
	

	lcClearDivAndDivClass(metadataMessageAreaSelector);
	$("#ticketTableDiv").empty();
	lcShowBusyIconInDiv("#ticketTableDiv");
	
	var params = {
		absPath : absPath
	}

	var jqxhr = $.get(context + ticketTableUrl, params,
			function(data, status, xhr) {
				$('#ticketTableDiv').html(data);
			}, "html").error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	}).success(function(data, status, xhr) {
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}
		buildTicketTableInPlace();
	});
	
}

/**
 * Given that the ticket details table data has been retrieved (as html
 * table), make it a dataTable
 * 
 * @returns
 */
function buildTicketTableInPlace() {

	  tableParams = {"bJQueryUI" : true,
          	"bLengthChange": false,
          	"bFilter": false,
          	"iDisplayLength" : 500

          }
	var ticketTable = lcBuildTableInPlace("#ticketDetailsTable", null, null, tableParams);
	//$("#infoDiv").resize();

	return ticketTable;
}
