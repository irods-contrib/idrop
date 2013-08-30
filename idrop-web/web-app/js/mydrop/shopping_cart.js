/**
 * Javascript for file shopping cart
 * 
 * author: Mike Conway - DICE
 */
var addToCartUrl = '/shoppingCart/addFileToCart';
var showCartUrl = '/shoppingCart/showCartTab';
var listCartUrl = '/shoppingCart/listCart';
var clearCartUrl = '/shoppingCart/clearCart';
var deleteCartUrl = '/shoppingCart/deleteFromCart';
var addToCartBulkActionUrl = '/shoppingCart/addToCartBulkAction';
var checkOutCartUrl = '/idropLite/shoppingCartAppletLoader';
var idropLiteShoppingCartSelector = "#cartAppletDiv";

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
	//var path = $("#browseDetailsAbsPath").val();
	addToCartGivenPath(selectedPath);
}

/**
 * Add a file to the file shopping cart at the given path
 * 
 * @param absPath
 *            absolute path to file to add to the cart
 */
function addToCartGivenPath(absPath) {

	var params = {
		absPath : absPath
	}
	
	showBlockingPanel();

	var jqxhr = $.post(context + addToCartUrl, params, "html").success(
			function(returnedData, status, xhr) {
				var continueReq = checkForSessionTimeout(returnedData, xhr);
				if (!continueReq) {
					return false;
				}
				setMessage("file added to cart:" + xhr.responseText);
				unblockPanel();

			})
			
			.error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
		unblockPanel();

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
				var continueReq = checkForSessionTimeout(returnedData, xhr);
				if (!continueReq) {
					return false;
				}
				refreshCartFiles();
			}).error(function(xhr, status, error) {
		setErrorMessage(xhr.responseText);
	});

}

/**
 * Delete files from the cart based on inputs in the cart details tab
 */
function deleteFromCart() {
	var answer = confirm("Delete selected files from cart?"); // FIXME: i18n

	var filesToDelete = new Array();
	var i = 0;
	if (answer) {
		var formData = $("#cartTableForm").serializeArray();
		var jqxhr = $.post(context + deleteCartUrl, formData, "html").success(
				function(returnedData, status, xhr) {
					var continueReq = checkForSessionTimeout(returnedData, xhr);
					if (!continueReq) {
						return false;
					}
					refreshCartFiles();
				}).error(function(xhr, status, error) {
			setErrorMessage(xhr.responseText);
		});

	}
}

/**
 * Add to the file cart based on inputs in the browse details table.  Note that confirmation has already
 * been provided.
 */
function addToCartBulkAction() {
	
		var formData = $("#browseDetailsForm").serializeArray();
		var jqxhr = $.post(context + addToCartBulkActionUrl, formData, "html").success(
				function(returnedData, status, xhr) {
					var continueReq = checkForSessionTimeout(returnedData, xhr);
					if (!continueReq) {
						return false;
					}
					setMessage("Selected files added to cart");  // FIXME: i18n
					refreshCartFiles();
				}).error(function(xhr, status, error) {
			setErrorMessage(xhr.responseText);
		});

}

function closeShoppingCartApplet() {
	
	$(idropLiteShoppingCartSelector).animate({
		height : 'hide'
	}, 'slow');
	$("#cartToggleDiv").show('slow');
	$("#cartToggleDiv").height = "100%";
	$("#cartToggleDiv").width = "100%";
	$(idropLiteShoppingCartSelector).empty();
	
}

/**
 * Check out the shopping cart as the logged in user, this will launch iDrop lite in shopping cart mode
 */
function checkOut() {
	
	// first hide cart details table
	$("#cartToggleDiv").hide('slow');
	$("#cartToggleDiv").width = "0%";
	$("#cartToggleDiv").height = "0%";
	$(idropLiteShoppingCartSelector).animate({ height: '100%',
		 opacity: '100%' }, 'slow');
	$(idropLiteShoppingCartSelector).show('slow');
	$(idropLiteShoppingCartSelector).width = "100%";
	$(idropLiteShoppingCartSelector).height = "100%";


	lcShowBusyIconInDiv(idropLiteShoppingCartSelector);
	setMessage("This will launch the iDrop Lite applet, it may take a minute for the applet to load, please be patient");
	var jqxhr = $
			.post(context + checkOutCartUrl, null, function(data, status, xhr) {
				var continueReq = checkForSessionTimeout(data, xhr);
				if (!continueReq) {
					return false;
				}
				lcClearDivAndDivClass(idropLiteShoppingCartSelector);
			}, "html")
			.error(function(xhr, status, error) {

				setErrorMessage(xhr.responseText);
				
				$("#cartToggleDiv").show('slow');
				$("#cartToggleDiv").width = "100%";
				$("#cartToggleDiv").height = "100%";
				$(idropLiteShoppingCartSelector).hide('slow');

			})
			.success(
					function(data) {

						var dataJSON = jQuery.parseJSON(data);
						var appletDiv = $(idropLiteShoppingCartSelector);
						$(appletDiv)
								.append(
										"<div id='appletMenu' class='fg-buttonset fg-buttonset-single' style='float:none'><button type='button' id='toggleCartClosed' class='ui-state-default ui-corner-all' value='toggleCartClosed' onclick='closeShoppingCartApplet()')>Close Cart</button></div>")
						var appletTagDiv = document.createElement('div');
						appletTagDiv.setAttribute('id', 'appletTagDiv');
						var a = document.createElement('applet');
						appletTagDiv.appendChild(a);
						a.setAttribute('code', dataJSON.appletCode);
						a.setAttribute('codebase', dataJSON.appletUrl);
						a.setAttribute('archive', dataJSON.archive);
						a.setAttribute('width', 800);
						a.setAttribute('height', 600);
						var p = document.createElement('param');
						p.setAttribute('name', 'mode');
						p.setAttribute('value', dataJSON.mode);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'host');
						p.setAttribute('value', dataJSON.host);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'port');
						p.setAttribute('value', dataJSON.port);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'zone');
						p.setAttribute('value', dataJSON.zone);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'user');
						p.setAttribute('value', dataJSON.user);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'password');
						p.setAttribute('value', dataJSON.password);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'absPath');
						p.setAttribute('value', dataJSON.absolutePath);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'uploadDest');
						p.setAttribute('value', dataJSON.absolutePath);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'defaultStorageResource');
						p
								.setAttribute('value',
										dataJSON.defaultStorageResource);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'key');
						p
								.setAttribute('value',
										dataJSON.key);
						a.appendChild(p);
						p = document.createElement('param');
						p.setAttribute('name', 'displayMode');
						p.setAttribute('value', 3);
						a.appendChild(p);
						appletDiv.append(appletTagDiv);

						$(idropLiteShoppingCartSelector).removeAttr('style');

					
					});
}


