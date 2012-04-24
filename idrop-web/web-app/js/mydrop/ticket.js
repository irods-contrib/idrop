/**
 * Javascript for ticket functions
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var
 */

var ticketTableUrl = '/ticket/listTickets';
var ticketUpdateUrl = '/ticket/update';

function reloadTicketTable(absPath) {
	if (absPath == null) {
		throw "null absPath";
	}
	
	//alert("clearing div");
	
	lcClearDivAndDivClass("#ticketTableDiv");
	//$("#ticketTableDiv").empty();
	lcShowBusyIconInDiv("#ticketTableDiv");
	
	//alert("showing busy icon and making call");
	
	var params = {
		absPath : absPath
	}

	var jqxhr = $.get(context + ticketTableUrl, params,
			function(data, status, xhr) {
				$('#ticketTableDiv').html(data);
			}, "html").error(function(xhr, status, error) {
		//alert("error");
		setErrorMessage(xhr.responseText);
	}).success(function(data, status, xhr) {
		//alert("success");
		var continueReq = checkForSessionTimeout(data, xhr);
		if (!continueReq) {
			return false;
		}
		buildTicketTableInPlace();
	});
	
}

/**
 * Given that the ticket details table data has been retrieved (as html table),
 * make it a dataTable
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
	// $("#infoDiv").resize();

	return ticketTable;
}


/**
 * Reload the tickets table
 */

function reloadTickets() {
	var absPath = $("#ticketDetailsAbsPath").val();
	reloadTicketTable(absPath);
}

/**
 * Show ticket details dailog area
 * 
 * @param create -
 *            is this a create or edit
 */

function prepareTicketDetailsDialog(ticketString) {
	var absPath = $("#ticketDetailsAbsPath").val();
	var url = "/ticket/ticketDetailsDialog";
	
	var create = false;
	
	if (!ticketString) {
		create = true;
		ticketString = "";
	}
	
	var params = {
		ticketString : ticketString,
		create : create
	}

	lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "", function(data) {
		showTicketDetailsDialog(data);
	});

}

/**
 * Show the dialog with the provided data
 * 
 * @param data -
 *            html data
 */
function showTicketDetailsDialog(data) {
	$("#ticketDialogArea").html(data).fadeIn('slow');
}

/**
 * Cause the add ticket dialog to be closed
 */
function closeTicketDialog() {
	$("#ticketDialogArea").fadeOut('slow', new function() {
		$("#ticketDialogArea").empty();
	});
}


/**
 * Handle form submit to add a ticket
 */
function submitTicketDialog() {

	
		var selectedPath = $("#ticketDetailsAbsPath").val();
	
		if (selectedPath == null) {
			setErrorMessage("no collection or data object selected");
			return false;
		}
		
		// see if there is form data (users in a pick list) that are selected
		var formData = $("#ticketDialogForm").serializeArray();
	
		if (formData == null) {
			setErrorMessage(jQuery.i18n.prop('msg_no_ticket_data'));
			return false;
		}
	
		//alert("sending request");
		var jqxhr = $.post(context + ticketUpdateUrl, formData,
				function(data, status, xhr) {
				}, "html").success(function(data, status, xhr) {
					//alert("success...");
					var continueReq = checkForSessionTimeout(data, xhr);
					if (!continueReq) {
						return false;
					} 
					
					//alert("closing dialog");
					closeTicketDialog();
					//alert("setting success message");
					setMessage(jQuery.i18n.prop('msg_ticket_update_successful'));
					//alert("reloading ticket table");
					reloadTicketTable(selectedPath);
					

		}).error(function(xhr, status, error) {
			closeTicketDialog();
			reloadTicketTable(selectedPath);
			setErrorMessage(xhr.responseText);
		});
		
}



