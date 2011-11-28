/**
 * Javascript for file shopping cart
 * 
 * author: Mike Conway - DICE
 */
var addToCartUrl = '/browse/addFileToCart';
var showCartUrl = '/browse/showCartTab';
var listCartUrl = '/browse/listCart';
var clearCartUrl = '/browse/clearCart';
var deleteCartUrl = '/browse/deleteFromCart';


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
 * 
 * @param absPath
 *            absolute path to file to add to the cart
 */
function addToCartGivenPath(absPath) {
	lcPrepareForCall();

	var params = {
		absPath : absPath
	}

	var jqxhr = $.post(context + addToCartUrl, params, "html").success(
			function(returnedData, status, xhr) {
				setMessage("file added to cart:" + xhr.responseText);
				refreshCartFiles();
			}).error(function(xhr, status, error) {
		setMessage(xhr.responseText);
	});
}

/**
 * Show the cart tab info this will subsequently call 'refreshCartFiles()' when
 * the tab info is displayed
 */
function displayCartTab() {
	lcSendValueAndCallbackHtmlAfterErrorCheck(showCartUrl, "#cartFileDetails",
			"#cartFileDetails", null);
}

/**
 * Display the tab data that shows the files in the shopping cart
 */
function refreshCartFiles() {
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(listCartUrl, "#cartTableDiv",
			"#cartTableDiv", null);
}

/**
 * Clear the files in the shopping cart
 */
function clearCart() {
	var jqxhr = $.post(context + clearCartUrl, null, "html").success(
			function(returnedData, status, xhr) {
				refreshCartFiles();
			}).error(function(xhr, status, error) {
		setMessage(xhr.responseText);
	});

}

function deleteFromCart() {
	var answer = confirm("Delete selected files from cart?"); // FIXME: i18n

	var filesToDelete = new Array();
	var i = 0;
	if (answer) {
		var formData = $("#cartTableForm").serializeArray();
		var jqxhr = $.post(context + deleteCartUrl, formData, "html").success(
				function(returnedData, status, xhr) {
					refreshCartFiles();
				}).error(function(xhr, status, error) {
			setMessage(xhr.responseText);
		});

	}
}