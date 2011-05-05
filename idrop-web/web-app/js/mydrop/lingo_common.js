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
var messageClass = "message";

/**
 * Prepare for a call (usually an ajax call) doing things like clearing the
 * message area
 */
function lcPrepareForCall() {
	lcClearMessage();
}

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
		setMessageInArea(messageAreaId,
				"Session expired or resource was not found");
		return false;
	}

	if (resultHtml.indexOf(uncaughtException) > -1) {

		setMessageInArea(messageAreaId, "An exception has occurred");
		return false;
	}

	if (resultHtml.indexOf(dataAccessFailure) > -1) {

		setMessageInArea(messageAreaId,
				"Unable to access, due to expired login or no authorization");
		return false;
	}

	if (resultHtml.indexOf(expiredSessionVal) > -1) {

		setMessageInArea(messageAreaId,
				"Unable to access, due to expired login or no authorization");
		return false;
	}

	if (resultHtml.indexOf(appExceptionVal) > -1) {

		exceptionStart = resultHtml.indexOf("_exception") + 12;
		exceptionEnd = resultHtml.indexOf("<", exceptionStart);
		errorFromApp = resultHtml.substring(exceptionStart, exceptionEnd);
		setMessageInArea(messageId, errorFromApp);
		return false;
	}

}

/**
 * Set the specified (by jquery selector) message area message to a given
 * string.
 * 
 * message: the text message to display
 */
function setMessageInArea(messageAreaId, message) {
	$(messageAreaId).html(message);
	$(messageAreaId).addClass(messageClass);

}
/**
 * Set the default message area message to a given string. The target will be a
 * message area denoted on the web page by the javascriptMessageArea div id.
 * 
 * message: the text message to display
 */
function setMessage(message) {
	if (message == null || message.length == 0) {
		$(javascriptMessageArea).html("");
		$(javascriptMessageArea).removeClass();
	} else {
		$(javascriptMessageArea).html(message);
		$(javascriptMessageArea).addClass(messageClass);
	}
}

/**
 * Clear global javascript message area
 */
function lcClearMessage() {
	setMessage();
}

/**
 * Given the result of an AJAX call, inspect the returned data for various types
 * of errors, set the message, and throw an appropriate exception.
 */
function checkAjaxResultForError(resultHtml) {

	setMessage("");

	if (resultHtml.indexOf(resourceNotFound) > -1) {
		setMessage("Session expired or resource was not found");
		throw ("resourceNotFound");
	}

	if (resultHtml.indexOf(uncaughtException) > -1) {

		setMessage("An exception has occurred");
		throw ("uncaughtException");
	}

	if (resultHtml.indexOf(dataAccessFailure) > -1) {

		setMessage("Unable to access, due to expired login or no authorization");
		throw ("dataAccessError");
	}

	if (resultHtml.indexOf(expiredSessionVal) > -1) {

		setMessage("Unable to access, due to expired login or no authorization");
		throw ("dataAccessError");
	}

	if (resultHtml.indexOf(appExceptionVal) > -1) {

		exceptionStart = resultHtml.indexOf("_exception") + 12;
		exceptionEnd = resultHtml.indexOf("<", exceptionStart);
		errorFromApp = resultHtml.substring(exceptionStart, exceptionEnd);
		setMessage(errorFromApp);
		throw ("appException");
	}

	setMessage("An error occurred processing your request");
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
 */
function lcSendValueAndBuildTable(getUrl, params, tableDiv, newTableId,
		detailsFunction) {

	lcPrepareForCall();
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
				lcBuildTable(data, tableDiv, newTableId, detailsFunction);
			}
		}, "html");

	} catch (err) {
		// FIXME: console traces are not good for IE - mcc
		// console.trace();
		$(tableDiv).html(""); // FIXME: some sort of error icon?
		setMessage(err);
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

	if (detailsFunction != null) {
		$(dataIconSelector, dataTableCreated.fnGetNodes())
				.each(detailsFunction);
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
 */
function lcBuildTableInPlace(newTableId, detailsFunction, dataIconSelector) {
	var dataTableCreated = $(newTableId).dataTable({
		"bJQueryUI" : true
	});

	if (detailsFunction != null) {
		$(dataIconSelector, dataTableCreated.fnGetNodes()).each(function() {
			$(this).click(function() {
				detailsFunction(this);
			});
		});

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
	$(dataTable.fnGetNodes()).each(function() {
		if (this.innerHTML.match('circle-minus')) {
			var firstNode = $(this).children().first().children(".ui-icon");
			firstNode.removeClass("ui-icon-circle-minus");
			firstNode.addClass("ui-icon-circle-plus");
			dataTable.fnClose(this);
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

	lcPrepareForCall();
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

		}, "html").error(function() {
			// TODO: default message put into area
			resultDiv.html("");
			setMessage("unable to load data");
			// alert("error loading");
		});

	} catch (err) {
		// FIXME: console traces are not good for IE - mcc
		// console.trace();
		$(resultDiv).html(""); // FIXME: some sort of error icon?
		setMessage(err);
		// console.log("javascript error:" + err);
	}

}

/**
 * Send a query via ajax that results in html plugged into the correct div
 */
function lcSendValueWithParamsAndPlugHtmlInDiv(getUrl, params, resultDiv,
		postLoadFunction) {

	lcPrepareForCall();
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
		}, "html").error(function() {
			setMessage("Error in request");
		});

	} catch (err) {
		// FIXME: console traces are not good for IE - mcc
		// console.trace();
		$(resultDiv).html(""); // FIXME: some sort of error icon?
		setMessage(err);
		// console.log("javascript error:" + err);
	}

}

/**
 * Send a query via ajax GET request that results in html plugged into the
 * correct div
 */
function lcSendValueAndCallbackHtmlAfterErrorCheck(getUrl, divForAjaxError,
		divForLoadingGif, callbackFunction) {

	lcPrepareForCall();
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
		}, "html").error(function() {
			
			$(divForLoadingGif).html("");
			setMessage("unable to load data");
			// alert("error loading");
		});

	} catch (err) {
		$(divForLoadingGif).html(""); // FIXME: some sort of error icon?
		setMessage(err);
		// console.log("javascript error:" + err);
	}

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

	lcPrepareForCall();
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
		}, "html").error(function() {
			if (divForLoadingGif != null) {
				$(divForLoadingGif).html("");
			}
			if (divForAjaxError != null) {
				//alert("error in callback sending back for divForAjaxError");
				setMessageInArea(divForAjaxError, "An error occurred");
			} else {
				setMessage("An error occurred");
			}

			

		});

	} catch (err) {
		alert("error caught:" + err);
		$(divForLoadingGif).html(""); // FIXME: some sort of error icon?
		setMessage(err);
		// console.log("javascript error:" + err);
	}

}

/**
 * Send a query via ajax that results in json that will be returned to a
 * callback function
 */
function lcSendValueAndCallbackWithJsonAfterErrorCheck(getUrl, parms,
		divForAjaxError, callbackFunction) {

	lcPrepareForCall();
	if (getUrl.length == 0) {
		throw ("no get url for call");
	}

	try {

		$.get(context + getUrl, parms, function(data, status, xhr) {
			var continueReq = checkForSessionTimeout(data, xhr);
			if (continueReq) {
				callbackFunction(data);
			}
		}, "json").error(function() {
			if (divForAjaxError != null) {
				setMessageInArea(divForAjaxError, "An error occurred");
			} else {
				setMessage("An error occurred");
			}

			if (divForLoadingGif != null) {
				$(divForLoadingGif).html("");
			}

		});
	} catch (err) {
		setMessage(err);
		// console.log("javascript error:" + err);
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
