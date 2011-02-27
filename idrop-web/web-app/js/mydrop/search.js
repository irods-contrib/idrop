/**
 * Javascript for search page, processing the search table
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var holds jquery ref to the search results table
 */
var dataTable;

/**
 * Initialize the table by prosecuting a search based on the given parameters
 * 
 * @return
 */
function prosecuteSearch(searchTerm, searchType) {
	if (dataTable == null) {
		var url = "/search/search";
		
		var params = {
				searchTerm : searchTerm,
				searchType : searchType
			}
		
		lcSendValueAndBuildTable(url, params,
				"#searchTableDiv", "#searchResultTable", null);
	} else {
		
	}
}


function searchTableClickAction() {
	
}

/*
function detailsClick(minMaxIcon) {
	var nTr = minMaxIcon.parentNode.parentNode;

	if (minMaxIcon.parentNode.innerHTML.match('circle-minus')) {
		closeDetails(minMaxIcon, nTr);
	} else {
		try {
			openDetails(minMaxIcon, nTr);
		} catch (err) {
			console.log("error in getMetaData():" + err);
		}

	}
}

function closeDetails(minMaxIcon, rowActionIsOn) {
	// This row is already open - close it 
	minMaxIcon.setAttribute("class", "ui-icon ui-icon-circle-plus");
	dataTable.fnClose(rowActionIsOn);
}

function openDetails(minMaxIcon, rowActionIsOn) {
	prepareForCall();
	closeTableNodes();
	// nTr points to row and has id user_id in id
	var userId = $(rowActionIsOn).attr('id');
	var detailsId = "details_" + userId;
	var detailsHtmlDiv = "details_html_" + userId;
	var userDetailsId = "userDetailsForm_" + userId;

	// close other rows
	minMaxIcon.setAttribute("class", "ui-icon ui-icon-circle-minus");
	newRowNode = dataTable.fnOpen(rowActionIsOn,
			buildDetailsLayout(userDetailsId), 'details');
	// newRowNode.setAttribute("class", "details");
	newRowNode.setAttribute("id", detailsId);
	askForUserDetailsForm(userDetailsId, userId);

}
	*/
