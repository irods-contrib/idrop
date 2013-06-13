<div id="conceptBrowserMain" class="container-fluid">
	<div class="row-fluid " id="conceptBrowserVocabularyToolbar">
		<div class="span2">
			<div class="btn-group pad-around">
				<button type="button" class="btn btn-primary"
					id="showSelectionListing" onclick="processShowSelectionListing()">
					<g:message code="text.choose.vocabularies" />
				</button>
				<button type="button" class="btn btn-primary" id="backToTop"
					onclick="processVocabularySelection()">
					<g:message code="text.back.to.top" />
				</button>
			</div>
		</div>
		<div class="offset1 span9">
			<div class="btn-group pad-around" data-toggle="buttons-radio">
				<g:each in="${hiveState.selectedVocabularies}"
					var="selectedVocabulary">
					<button type="button" class="btn  btn-inverse"
						id="${selectedVocabulary}"
						onclick="processVocabularySelection('${selectedVocabulary}')">
						${selectedVocabulary}
					</button>
				</g:each>
			</div>
		</div>
	</div>
	<div class="row-fluid " id="conceptBrowserSearchForm">
		<div class="span5 offset1" id="searchConceptForm">
			<g:form id="searchConceptForm" action="search" method="get">
				<g:textField name="searchConcept" id="searchConceptTerm"
					value="${params.searchedConcept}" onkeydown="if (event.keyCode == 13) processSearchHiveConcept()"/>
				<button type="button" class="btn" id="searchConcept" value="update"
					onclick="processSearchHiveConcept()">
					<g:message code="text.search" />
				</button>
			</g:form>
		</div>
	</div>
	<div class="container-fluid">
		<div class="row-fluid ">
			<div class="span12" id="searchConceptResults"></div>
		</div>
	</div>
	<div id="conceptBrowserPivotContainer">
		<g:render template="/hive/conceptBrowserPivotView" />
	</div>
</div>

<div id="conceptBrowserDialog"></div>

<script type="text/javascript">
	/**
	 * page level action signals to select a new vocabulary
	 */
	function processVocabularySelection(vocabName) {
		if (vocabName == null || vocabName == "") {
			vocabName = null;
		}

		var absPath = $("#infoAbsPath").val();

		if (absPath == null) {
			setErrorMessage(jQuery.i18n.prop('msg_path_missing'));
			return false;
		}

		resetVocabulary(vocabName, absPath);
	}

	/**
	 * page level action to show the vocabulary selection form for reselection
	 */
	function processShowSelectionListing() {
		alert("show vocabulary selection listing to rechoose");
	}

	/**
	 * Pivot the concept browser to the new term
	 */
	function processSelectOfTermAsCurrent(termUri) {
		if (termUri == null || termUri == "") {
			setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
			return false;
		}

		var absPath = $("#infoAbsPath").val();

		if (absPath == null) {
			setErrorMessage(jQuery.i18n.prop('msg_path_missing'));
			return false;
		}

		browseToUri(termUri, absPath);
	}

	/**
	 * handle pressing the view in skos button
	 */
	function processViewInSKOS(vocabulary, termUri) {
		if (termUri == null || termUri == "") {
			setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
			return false;
		}
		if (vocabulary == null || vocabulary == "") {
			setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
			return false;
		}
		alert("to be implemented, view as SKOS term:" + termUri
				+ " from vocabulary:" + vocabulary );
	}

	/**
	 * handle pressing the apply term button
	 */
	function processApplyHiveTerm(vocabulary, termUri) {
		if (termUri == null || termUri == "") {
			setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
			return false;
		}
		if (vocabulary == null || vocabulary == "") {
			setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
			return false;
		}

		var absPath = $("#infoAbsPath").val();

		if (absPath == null) {
			setErrorMessage(jQuery.i18n.prop('msg_path_missing'));
			return false;
		}

		applyHiveTerm(absPath, vocabulary, termUri);

	}

	function processSearchHiveConcept() {
		var searchedConcept = $("#searchConceptTerm").val();
		if (!searchedConcept) {
			setMessage("No search term entered");
			return false;
		}
		searchConcept(searchedConcept);
	}

	$(function() {

	});

	function changeTextColor(id) {
		var element = document.getElementById(id);
		element.style.color = "#005580";
	}
</script>