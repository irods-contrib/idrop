/**
 * Javascript for file shopping cart
 * 
 * author: Mike Conway - DICE
 */
var addToCartUrl = '/browse/addFileToCart';
var listCartUrl = '/browse/listCart';

/**
 * The add to shopping cart button has been selected from an info view
 */
function addToCartViaToolbar() {
	var infoAbsPath = $("#infoAbsPath").val();
	addToCartGivenPath(infoAbsPath);
}

/**
 * The add to cart button was selected from the browseDetailsView
 */
function addToCartViaBrowseDetailsToolbar() {
	var path = $("#browseDetailsAbsPath").val();
	addToCartGivenPath(path);
}

/**
 * Add a file to the file shopping cart at the given path
 * @param absPath absolute path to file to add to the cart
 */
function addToCartGivenPath(absPath) {
	lcPrepareForCall();

	var params = {
		absPath : absPath
	}
	
	var jqxhr = $.post(context + addToCartUrl, params, "html").success(function(returnedData, status, xhr) {
		setMessage("file added to cart:" + xhr.responseText);
	}).error(function(xhr, status, error) {
		setMessage(xhr.responseText);
	});
}

/**
 * Display the tab data that shows the files in the shopping cart
 */
function refreshCartFiles() {
	alert("refresh cart files");
	lcSendValueAndCallbackHtmlAfterErrorCheck(listCartUrl, "#cartFileDetails", "#cartFileDetails",
			null);
}