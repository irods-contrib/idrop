/**

= * Javascript for search page, processing the search table
 * 
 * author: Mike Conway - DICE
 */

/**
 * Global var holds jquery ref to the search results table
 */

/**
 * Initialize the table by prosecuting a search based on the given parameters
 * 
 * @return
 */
function prosecuteSearch(searchTerm, searchType) {

	var url = "/search/search";

	var params = {
		searchTerm : searchTerm,
		searchType : searchType
	}

	lcSendValueAndBuildTable(url, params, "#searchTableDiv",
			"#searchResultTable", searchDetailsClick, ".search-detail-icon");

}

function searchDetailsClick(minMaxIcon) {
	var searchTable = $("#searchResultTable").dataTable();
	var nTr = minMaxIcon.parentNode.parentNode;
	if (minMaxIcon.parentNode.innerHTML.match('circle-minus')) {
		lcCloseTableNodes(searchTable);
	} else {
		try {
			searchDetailsFunction(minMaxIcon, nTr);
		} catch (err) {
			setErrorMessage("error in searchDetailsClick():" + err);
		}

	}
}

function searchDetailsFunction(clickedIcon, rowActionIsOn) {
	var searchTable = $("#searchResultTable").dataTable();
	/* Open this row */
	lcCloseTableNodes(searchTable);
	// nTr points to row and has absPath in id
	var absPath = $(rowActionIsOn).attr('id');
	var detailsId = "details_" + absPath;
	var detailsHtmlDiv = "details_html_" + absPath;
	var buildDetailsLayoutVal = buildSearchLayout(detailsId);
	clickedIcon.setAttribute("class", "ui-icon ui-icon-circle-minus");
	newRowNode = searchTable.fnOpen(rowActionIsOn,
			buildDetailsLayoutVal, 'details');
	newRowNode.setAttribute("id", detailsId);
	askForSearchDetailsPulldown(absPath, detailsId)
	
}

function buildSearchLayout(detailsId) {
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

function askForSearchDetailsPulldown(absPath, detailsId) {
	
	var url = "/browse/miniInfo";
	var params = {
			absPath:absPath
		}
		
	lcSendValueWithParamsAndPlugHtmlInDiv(url, params, ".details",
			null);
	
}


