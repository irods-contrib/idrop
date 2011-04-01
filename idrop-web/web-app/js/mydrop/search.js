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

	var url = "/search/search";

	var params = {
		searchTerm : searchTerm,
		searchType : searchType
	}

	lcSendValueAndBuildTable(url, params, "#searchTableDiv",
			"#searchResultTable", null);

}

function searchTableClickAction() {

}

