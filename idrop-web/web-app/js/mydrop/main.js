/**
 * Javascript for common elements in main layout template, such as menus, headers, and footers.
 * 
 * author: Mike Conway - DICE
 */

function search() {
		var searchTerm = $("#searchTerm").val();
		$('#tabs').tabs({ selected: 1 }); // activate search results tab
		prosecuteSearch(searchTerm, "file");
}

