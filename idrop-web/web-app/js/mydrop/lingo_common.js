/**
 * Common functions for iRODS web applications
 * 
 * Author: Mike Conway
 * 
 */
var appExceptionVal = "_exception";
var expiredSessionVal = "You have tried to access a protected area of this application";
var resourceNotFound = "resourceNotFound";
var uncaughtException = "uncaughtException";
var dataAccessFailure = "dataAccessFailure";
var timeoutHeaderValue = "apptimeout";
var javascriptMessageArea = "#javascript_message_area";
var context = "";
var scheme = "";
var host = "";
var port = "";
var messageClass = "message";


function checkForSessionTimeout(data, xhr) {
	var headers = xhr.getAllResponseHeaders();
	if (headers.indexOf(timeoutHeaderValue) > -1) {
		window.location = context;
		return false;
	} else {
		return true;
	}

}

/**
 * FIXME: remove..this is dumb check HTML coming back from an AJAX call for an
 * indication of an error, and if an error is found, then set the message in the
 * div using the given id. Then the appropriate exception will be thrown.
 * 
 * @param messageAreaId
 *            is a jquery selector that will give the div where the message
 *            should be displayed
 */
function checkAjaxResultForErrorAndDisplayInGivenArea(resultHtml, messageAreaId) {

	// if no message area specified, then do an exception throw
	if (messageAreaId == null) {
		checkAjaxREsultForError(resultHtml);
	}

	if (resultHtml.indexOf(resourceNotFound) > -1) {
		setMessage(
				"Session expired or resource was not found");
		return false;
	}

	if (resultHtml.indexOf(uncaughtException) > -1) {

		setErrorMessage("An exception has occurred");
		return false;
	}

	if (resultHtml.indexOf(dataAccessFailure) > -1) {

		setErrorMessage(
				"Unable to access, due to expired login or no authorization");
		return false;
	}

	if (resultHtml.indexOf(expiredSessionVal) > -1) {

		setErrorMessage(
				"Unable to access, due to expired login or no authorization");
		return false;
	}

	if (resultHtml.indexOf(appExceptionVal) > -1) {

		exceptionStart = resultHtml.indexOf("_exception") + 12;
		exceptionEnd = resultHtml.indexOf("<", exceptionStart);
		errorFromApp = resultHtml.substring(exceptionStart, exceptionEnd);
		setErrorMessage(errorFromApp);
		return false;
	}

}

/**
 * Set the specified (by jquery selector) message area message to a given
 * string.
 * 
 * message: the text message to display
 */
/*function setMessageInArea(messageAreaId, message) {
	try {
	$(messageAreaId).html(message);
	$(messageAreaId).addClass(messageClass);
	} catch(e) {
		
	}
}*/


/**
 * Set the default message area message to a given string. This will be a normal, non-sticky 
 * gritter message
 * 
 * message: the text message to display
 */
function setMessage(message) {
	if (message == null || message == "") {
		message = "An unknown error has occurred";
	}
	$.gritter.add({
		// (string | mandatory) the heading of the notification
		title: 'iDrop Message',
		// (string | mandatory) the text inside the notification
		text: message,
		// (string | optional) the image to display on the left
		//image: 'http://a0.twimg.com/profile_images/59268975/jquery_avatar_bigger.png',
		// (bool | optional) if you want it to fade out on its own or just sit there
		sticky: false,
		// (int | optional) the time you want it to be alive for before fading out
		time: '3000'
	});

}

/**
 * Set an error message, which is treated as sticky
 * @param message
 */
function setErrorMessage(message) {
	if (message == null || message == "") {
		message = "An unknown error has occurred";
	}
	$.gritter.add({
		// (string | mandatory) the heading of the notification
		title: 'iDrop Error Message',
		// (string | mandatory) the text inside the notification
		text: message,
		// (string | optional) the image to display on the left
		//image: 'http://a0.twimg.com/profile_images/59268975/jquery_avatar_bigger.png',
		// (bool | optional) if you want it to fade out on its own or just sit there
		sticky: true
		
	});

}

/**
 * Clear global javascript message area
 */
/*function lcClearMessage() {
	setMessage();
}*/

/**
 * Given the result of an AJAX call, inspect the returned data for various types
 * of errors, set the message, and throw an appropriate exception.
 */
function checkAjaxResultForError(resultHtml) {

	
	if (resultHtml.indexOf(resourceNotFound) > -1) {
		setErrorMessage("Session expired or resource was not found");
		throw ("resourceNotFound");
	}

	if (resultHtml.indexOf(uncaughtException) > -1) {

		setErrorMessage("An exception has occurred");
		throw ("uncaughtException");
	}

	if (resultHtml.indexOf(dataAccessFailure) > -1) {

		setErrorMessage("Unable to access, due to expired login or no authorization");
		throw ("dataAccessError");
	}

	if (resultHtml.indexOf(expiredSessionVal) > -1) {

		setErrorMessage("Unable to access, due to expired login or no authorization");
		throw ("dataAccessError");
	}

	if (resultHtml.indexOf(appExceptionVal) > -1) {

		exceptionStart = resultHtml.indexOf("_exception") + 12;
		exceptionEnd = resultHtml.indexOf("<", exceptionStart);
		errorFromApp = resultHtml.substring(exceptionStart, exceptionEnd);
		setErrorMessage(errorFromApp);
		throw ("appException");
	}

	setErrorMessage("An error occurred processing your request");
}

/**
 * Send a query via ajax that results in an HTML table to be displayed as a
 * JQuery data table
 * 
 * @param getUrl -
 *            url for ajax call as GET, context will be pre-pended, supply the
 *            leading '/'
 * 
 * @param params -
 *            map of parameters to add to the get
 * 
 * @param tableDiv -
 *            selector for the div where the table HTML response will be placed
 * 
 * @param newTableId -
 *            id for the new table
 * 
 * @param detailsFunction -
 *            function pointer for click event handler to be attached to each
 *            table node
 *            
 *  @param detailsIconSelector
 *  		jquery selector for detail icon, or null
 */
function lcSendValueAndBuildTable(getUrl, params, tableDiv, newTableId,
		detailsFunction, detailsIconSelector) {

	if (getUrl.length == 0) {
		throw ("no get url for call");
	}

	var img = document.createElement('IMG');
	img.setAttribute("src", context + "/images/ajax-loader.gif");

	$(tableDiv).html(img);

	try {

		$.get(context + getUrl, params, function(data, status, xhr) {
			var continueReq = checkForSessionTimeout(data, xhr);
			if (continueReq) {
				lcBuildTable(data, tableDiv, newTableId, detailsFunction, detailsIconSelector);
			}
		}, "html");

	} catch (err) {
		// FIXME: console traces are not good for IE - mcc
		// console.trace();
		$(tableDiv).html(""); // FIXME: some sort of error icon?
		setErrorMessage(err);
		// console.log("javascript error:" + err);
	}

}

/**
 * Function called by ajax action as response handler. Builds the data table
 * 
 * @param data -
 *            results from ajax call in | delimited format for parsing
 * @param tableDiv -
 *            id for div that will hold the table results after the ajax call
 * @param newTableId -
 *            id to be given to the table, used to set it to a data table
 * @param detailsFunction -
 *            function to be processed against each node of the table if detail
 *            icons are to be setup
 * @return - DataTable that was created
 */
function lcBuildTable(data, tableDiv, newTableId, detailsFunction,
		dataIconSelector) {
	$(tableDiv).html(data);
	var dataTableCreated = $(newTableId).dataTable({
		"bJQueryUI" : true
	});
	
	dataTableCreated.fnAdjustColumnSizing();
	
	
	if (detailsFunction != null) {
		$(dataIconSelector, dataTableCreated.fnGetNodes()).each(function() {
			$(this).click(function() {
				detailsFunction(this);
			});
		});

	}

}

/**
 * Given a table structure in an existing DOM, build a table based on a JQuery
 * selector, assigning it a function that can be called when a given selector is
 * clicked. This is useful for cases where tables are expanded based on click
 * 
 * @param newTableId
 * @param detailsFunction
 * @param dataIconSelector
 * @param tableParams optional parameters for JTable
 */
function lcBuildTableInPlace(newTableId, detailsFunction, dataIconSelector, tableParams) {
	
	if (tableParams == null) {
		tableParams = {"bJQueryUI" : true}
	}
	
	
	var dataTableCreated = $(newTableId).dataTable(tableParams);
	

	if (detailsFunction != null) {
		$(dataIconSelector, dataTableCreated.fnGetNodes()).each(function() {
			$(this).click(function() {
				detailsFunction(this);
			});
		});

	}
	
	if (dataTableCreated != null) {
		dataTableCreated.fnAdjustColumnSizing();
	}
	return dataTableCreated;

}

/**
 * Close table nodes when using +/- details icon
 * 
 * @param dataTable -
 *            reference to jquery dataTable (not a selector, the table)
 */
function lcCloseTableNodes(dataTable) {
	$(dataTable.fnGetNodes()).each(function(index, value) {
		if (value.innerHTML.match('circle-minus')) {
			var firstNode = $(value).children().first().children(".ui-icon");
			firstNode.removeClass("ui-icon-circle-minus");
			firstNode.addClass("ui-icon-circle-plus");
			dataTable.fnClose(value);
		}

	});
}

/**
 * close an individual details node on a data table
 * 
 * @param -
 *            minMaxIcon - icon as styled by the jquery ui css class
 * @param -
 *            rowActionIsOn - selected node to close
 * @dataTable - reference to JQuery dataTable
 */
function lcCloseDetails(minMaxIcon, rowActionIsOn, dataTable) {
	/* This row is already open - close it */
	minMaxIcon.setAttribute("class", "ui-icon ui-icon-circle-plus");
	dataTable.fnClose(rowActionIsOn);
}

/**
 * Send a query via ajax that results in html plugged into the correct div
 */
function lcSendValueAndPlugHtmlInDiv(getUrl, resultDiv, context,
		postLoadFunction) {

	if (getUrl.length == 0) {
		throw ("no get url for call");
	}

	var img = document.createElement('IMG');
	img.setAttribute("src", +context + "/images/ajax-loader.gif");

	$(resultDiv).html(img);

	try {

		$.get(getUrl, function(data, status, xhr) {
			var continueReq = checkForSessionTimeout(data, xhr);
			if (continueReq) {
				lcFillInDivWithHtml(data, resultDiv, postLoadFunction);
			}

		}, "html").error(function(xhr, status, error) {
			resultDiv.html("");
			setErrorMessage(xhr.responseText);
		});

	} catch (err) {
		
		try {
			$(resultDiv).html(""); // FIXME: some sort of error icon?
		} catch(err) {
			// ignore
		}
		setErrorMessage(err);
	}

}

/**
 * Send a query via ajax that results in html plugged into the correct div
 */
function lcSendValueWithParamsAndPlugHtmlInDiv(getUrl, params, resultDiv,
		postLoadFunction) {

	if (getUrl.length == 0) {
		throw ("no get url for call");
	}

	var img = document.createElement('IMG');
	img.setAttribute("src", context + "/images/ajax-loader.gif");

	$(resultDiv).html(img);

	try {

		$.get(context + getUrl, params, function(data, status, xhr) {
			var continueReq = checkForSessionTimeout(data, xhr);
			if (continueReq) {
				lcFillInDivWithHtml(data, resultDiv, postLoadFunction);
			}
		}, "html").error(function(xhr, status, error) {
			$(resultDiv).html("");
			setErrorMessage(xhr.responseText);
		});

	} catch (err) {
		// FIXME: console traces are not good for IE - mcc
		// console.trace();
		try {
			$(resultDiv).html(""); // FIXME: some sort of error icon?
		} catch(err) {
			// ignore
		}
		setErrorMessage(err);
		// console.log("javascript error:" + err);
	}

}

/**
 * Send a query via ajax GET request that results in html plugged into the
 * correct div
 */
function lcSendValueAndCallbackHtmlAfterErrorCheck(getUrl, divForAjaxError,
		divForLoadingGif, callbackFunction) {

	if (getUrl.length == 0) {
		throw ("no get url for call");
		return;
	}

	var img = document.createElement('IMG');
	img.setAttribute("src", context + "/images/ajax-loader.gif");

	$(divForLoadingGif).html(img);

	try {

		$.get(context + getUrl, function(data, status, xhr) {
			var continueReq = checkForSessionTimeout(data, xhr);
			if (continueReq) {
				$(divForLoadingGif).html("");
				if (callbackFunction != null) {
					var myHtml = data;
					callbackFunction(myHtml);
				} else {
					$(divForLoadingGif).html(data);
				
				}
			}
		}, "html").error(function(xhr, status, error) {
			
			$(divForLoadingGif).html("");
			setErrorMessage(xhr.responseText);
		});

	} catch (err) {
		try {
			$(resultDiv).html(""); // FIXME: some sort of error icon?
		} catch(err) {
			// ignore
		}
		setErrorMessage(err);
		// console.log("javascript error:" + err);
	}

}

/**
* Send a query via ajax GET request that results in html plugged into the
* correct div.  Do not overwrite message if no error occurs
*/
function lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(getUrl, divForAjaxError,
		divForLoadingGif, callbackFunction) {

	if (getUrl.length == 0) {
		throw ("no get url for call");
		return;
	}

	var img = document.createElement('IMG');
	img.setAttribute("src", context + "/images/ajax-loader.gif");

	$(divForLoadingGif).html(img);

	try {

		$.get(context + getUrl, function(data, status, xhr) {
			var continueReq = checkForSessionTimeout(data, xhr);
			if (continueReq) {
				$(divForLoadingGif).html("");
				if (callbackFunction != null) {
					var myHtml = data;
					callbackFunction(myHtml);
				} else {
					$(divForLoadingGif).html(data);
				
				}
			}
		}, "html").error(function(xhr, status, error) {
			
			try {
				$(resultDiv).html(""); // FIXME: some sort of error icon?
			} catch(err) {
				// ignore
			}
			setErrorMessage(xhr.responseText);
		});

	} catch (err) {
		$(divForLoadingGif).html(""); 
		
		if (err.indexOf("Runtime") > -1) {
			err = "Unable to view data, this may be a permissions issue";
			setMessage(err);
		} else {
			setErrorMessage(err);
		}
	}

}

/**
* Send a query via ajax GET request that results in html plugged into the
* correct div.  If an error occurs then do a throw
*/
function lcSendValueAndCallbackHtmlAfterErrorCheckThrowsException(getUrl, 
		divForLoadingGif, callbackFunction, errorHandlingFunction) {

	if (getUrl.length == 0) {
		throw ("no get url for call");
		return;
	}

	var img = document.createElement('IMG');
	img.setAttribute("src", context + "/images/ajax-loader.gif");

	$(divForLoadingGif).html(img);

		$.get(context + getUrl, function(data, status, xhr) {
			var continueReq = checkForSessionTimeout(data, xhr);
			if (continueReq) {
				$(divForLoadingGif).html("");
				if (callbackFunction != null) {
					var myHtml = data;
					callbackFunction(myHtml);
				} else {
					$(divForLoadingGif).html(data);
				
				}
			}
		}, "html").error(function(xhr, status, error) {
			$(divForLoadingGif).html("");
			if (errorHandlingFunction == null) {
				throw "error loading:" + error;
			} else {
				errorHandlingFunction();
			}
			
		});



}

/**
 * Send a query via ajax POST request that results in html plugged into the
 * correct div
 * 
 * @param postURL
 *            url String (sans web context, which is automatically appended) for
 *            the POST action
 * @param params
 *            map with name/value params for the post data
 * @param divForAjaxError
 *            JQuery selector for a DIV to display any Ajax error
 * @param divForLoadingGif
 *            JQuery selector for a DIV in which to display a loading GIF, and
 *            then any response data
 * @param callbackFunction
 *            optional function reference that will receive a callback
 */
function lcSendValueViaPostAndCallbackHtmlAfterErrorCheck(postUrl, params,
		divForAjaxError, divForLoadingGif, callbackFunction) {

	if (postUrl.length == 0) {
		throw ("no post url for call");
	}

	var img = document.createElement('IMG');
	img.setAttribute("src", context + "/images/ajax-loader.gif");

	$(divForLoadingGif).html(img);

	try {

		$.post(context + postUrl, params, function(data, status, xhr) {
			var continueReq = checkForSessionTimeout(data, xhr);
			if (continueReq) {
				$(divForLoadingGif).html("");
				if (callbackFunction != null) {
					var myHtml = data;
					callbackFunction(myHtml);
				} else {
					$(divForLoadingGif).html(data);
				}
			}
		}, "html").error(function(xhr, status, error) {
			if (divForLoadingGif != null) {
				$(divForLoadingGif).html("");
			}
			setErrorMessage(xhr.responseText);

		});

	} catch (err) {
		try {
			$(divForLoadingGif).html(""); 
		} catch(err) {
			// ignore
		}
		setErrorMessage(err);
	}

}

/**
 * Send a query via ajax that results in json that will be returned to a
 * callback function
 */
function lcSendValueAndCallbackWithJsonAfterErrorCheck(getUrl, parms,
		divForAjaxError, callbackFunction) {

	if (getUrl.length == 0) {
		throw ("no get url for call");
	}

	try {

		$.get(context + getUrl, parms, function(data, status, xhr) {
			var continueReq = checkForSessionTimeout(data, xhr);
			if (continueReq) {
				callbackFunction(data);
			}
		}, "json").error(function(xhr, status, error) {
			setErrorMessage(xhr.responseText);
			if (divForLoadingGif != null) {
				$(divForLoadingGif).html("");
			}

		});
	} catch (err) {
		setErrorMessage(err);
	}

}

function lcFillInDivWithHtml(data, resultDiv, postLoadFunction) {
	$(resultDiv).html(data);
	if (postLoadFunction != null) {
		postLoadFunction(data);
	}

}

/**
 * Handy method to show a loading icon in the div specified by the given JQuery
 * selector
 * 
 * @param divSelector
 */
function lcShowBusyIconInDiv(divSelector) {
	lcClearDivAndDivClass(divSelector);
	var img = document.createElement('IMG');
	img.setAttribute("src", context + "/images/ajax-loader.gif");

	$(divSelector).html(img);
}

/**
 * Handy method to clear a div specified by the given JQuery selector, including
 * removing any class set
 * 
 * @param divSelector
 */
function lcClearDivAndDivClass(divSelector) {
	$(divSelector).removeClass();
	$(divSelector).html("");
}

/**
 * Handy method to set a message of a message class in the div specified by the
 * given JQuery selector
 * 
 * @param divSelector
 */
function lcSetMessageWithMessageClass(divSelector, message) {
	lcClearDivAndDivClass(divSelector);
	$(divSelector).html(message);
	$(divSelector).addClass(messageClass);
}

/**
 * Handy method to show a loading icon in the div specified by the given JQuery
 * selector
 * 
 * @param divSelector
 */
function lcShowLoaderBarIconInDiv(divSelector) {
	var img = document.createElement('IMG');
	img.setAttribute("src", context + "/images/ajax-loader-bar.gif");

	$(divSelector).html(img);
}

/**
 * Show a modal blocking overlay with a message using blockUI
 * @param message
 */
function showBlockingPanel(message) {
	
	if (message == null || message == "") {
		message = "please wait...";
	}
	
	var messageHtml = "<h1>";
	messageHtml += message;
	messageHtml += "</h1>";
	
	$.blockUI({ message: messageHtml });
	
} 


function unblockPanel() {
	$.unblockUI();
}
