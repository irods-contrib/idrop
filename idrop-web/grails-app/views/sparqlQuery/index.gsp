<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/search" />
<g:javascript library="mydrop/hivequery" />
</head>
<div id="hiveSearch">
	<ul class="nav nav-tabs" id="searchTabs">
		<li><a href="#hiveQueryTab">HIVE Query</a></li>
		<li><a href="#resultsTab">Search Results</a></li>
	</ul>
	
	<div class="tab-content">
		<div class="tab-pane active" id="hiveQueryTab">
			<div id="hiveQueryForm">
				<g:render template="/sparqlQuery/hiveQuery" />
			</div>
			<div id="conceptBrowserWindows"></div>
		</div>
		
		<div class="tab-pane" id="resultsTab">
			<div id="resultsTabInner">Results Here...
			</div>
		</div>
	
	</div>
</div>

<script>
	$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});
		$("#topbarSearch").addClass("active");

		$('#searchTabs a').click(function(e) {

			e.preventDefault();
			$(this).tab('show');
			var state = {};
			var tabId = this.hash
			state["tab"] = tabId;
			$.bbq.pushState(state);
		});

		$(window).bind('hashchange', function(e) {
			//processTagSearchStateChange( $.bbq.getState());
		});

		$(window).trigger('hashchange');

	});

	function showConceptBrowser(index) {
		console.log("showConceptBrowser(); index: " + index);
		if(index == null || index == "") {
			//setErrorMessage("index is missing"); // FIXME:i18n
			console.log("index is missing");
		}
		navConceptBrowser(index);
	}

	function changeTextColor(id) {
		var element = document.getElementById(id);
		element.style.color = "#FF8000";
	}

	function changeTextColorBack(id) {
		var element = document.getElementById(id);
		element.style.color = "#333333";
	}

	/**
	 * Pivot the concept browser to the new term
	 */
	function processSelectOfTermAsCurrent(termUri, index) {
		console.log("Select as current, index: " + index);
		if (termUri == null || termUri == "") {
			setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
			return false;
		}

		if(index == null) {
			//setErrorMessage("index is missing"); // FIXME:i18n
			console.log("index is missing");
		}

		browseToUri(termUri, index);
	}

	/**
	 * page level action signals to select a new vocabulary
	 */
	function processVocabularySelection(vocabName, index) {

		if (vocabName == null || vocabName == "") {
			vocabName = null;
			console.log("vocabName is null");
		} 

		if(index == null || index == "") {
			console.log("index is null or empty");
		}
		
		if (vocabName != null) {
		 	$(".vocab-btn").removeClass("btn btn-mini btn-warning vocab-btn").addClass("btn btn-mini btn-inverse vocab-btn");
		 	console.log("change class");
			
			var element = document.getElementById(vocabName);
			element.className = "btn btn-mini btn-warning vocab-btn";
		}

		resetVocabulary(vocabName, index);
	}

	/**
	 * page level action to show the vocabulary selection form for reselection
	 */
	function processShowSelectionListing() {
		<%--alert("show vocabulary selection listing to rechoose");--%>
		$('#myModal').modal({
			backdrop: false,
			show: false
			});
	 }

	 function processSelectHiveTerm(vocabName, preLabel, termUri, index) {
		console.log("processSelectHiveTerm(), vocabName: " + vocabName + ", preLabel: " + preLabel + ", termUri: " + termUri + ", index: " + index);
		if (termUri == null || termUri == "") {
			setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
			return false;
		}
		if (vocabName == null || vocabName == "") {
			setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
			<%--return false;--%>
		}

		if (preLabel == null || preLabel == "") {
			setErrorMessage(jQuery.i18n.prop('msg_preLabel_missing'));
			return false;
		}


		if(index == null) {
			//setErrorMessage("index is missing"); // FIXME:i18n
			console.log("index is missing");
		}

		
		selectHiveTerm(vocabName, preLabel, termUri, index);

	}
		
</script>