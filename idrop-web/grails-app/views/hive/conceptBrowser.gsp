<div class="container-fluid">
	<div class="row-fluid ">
		<div class="span2">
			<div class="btn-group pad-around">
				<button type="button" class="btn btn-primary"
					id="showSelectionListing" onclick="processShowSelectionListing()">
					<g:message code="text.choose.vocabularies" />
				</button>
			</div>
		</div>
		<div class="span10">
			<div class="btn-group pad-around" data-toggle="buttons-radio">
				<g:each in="${hiveState.selectedVocabularies}"
					var="selectedVocabulary">
					<button type="button" class="btn btn-primary"
						id="${selectedVocabulary}"
						onclick="processVocabularySelection('${selectedVocabulary}')">
						${selectedVocabulary}
					</button>
				</g:each>
			</div>
		</div>
		<div id="searchConceptForm">
			<g:form id="searchConceptForm" action="search" method="get">
            <g:textField name="searchConcept" value="${params.searchedConcept}"/>
            <button type="button" id="searchConcept" value="update" onclick="searchConcept()">
			</button>
        	</g:form>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span6">
			<div class="container-fluid">
				<g:if test="${!conceptProxy.topLevel}">
					<div class="row-fluid">
						<div class="span10 offset1 well">
							<div class="container-fluid">
								<div class="row-fluid">
									<div class="span3">
										<strong>Preferred label:</strong>
									</div>
									<div class="offset1 span8">
										<em>${conceptProxy.preLabel}</em>
									</div>
								</div>
								<div class="row-fluid">
									<div class="span3">
										<strong>Alternate labels</strong>
									</div>
								</div>
								<g:each in="${conceptProxy.altLabel}" var="altLabel">
									<div class="row-fluid">
										<div class="offset1 span8">
											<em>${altLabel}</em>
										</div>
									</div>

								</g:each>
							<div class="row-fluid">
									<div class="span3">
										<strong>URI:</strong>
									</div>
									<div class="offset1 span8">
										<em>${conceptProxy.URI}</em>
									</div>
								</div>
							</div>
						</div>
					</div>
				</g:if>
				<g:else>
					<!-- <div class="row-fluid">
						<div class="span10 offset1 well">at top level...</div>
					</div> -->
				</g:else>
				<div class="row-fluid" id="conceptBrowserNarrower">
					<div class="span10 offset1 well">
						<table cellspacing="0" cellpadding="0" border="0"
							id="hiveVocabTable" class="table table-striped table-hover">
							<thead>
								<tr>
									<th></th>
								</tr>
							</thead>
							<tbody>

								<g:each in="${conceptProxy.narrower.keySet()}" var="key">
									<tr id="${conceptProxy.narrower.get(key)}"
										onclick="processSelectOfTermAsCurrent('${conceptProxy.narrower.get(key)}')">
										<td>
											${key}
										</td>
									</tr>
								</g:each>
							</tbody>
						</table>
					</div>
				</div>
				<!-- <div class="row-fluid" id="conceptBrowserNarrowerLetters">
					<div class="span10 offset1 well">
						<g:render template="/hive/alphabetTable" />
					</div>
				</div> -->
			</div>
		</div>
		<div class="span6">
			<div class="container-fluid">
				<div class="row-fluid" id="conceptBrowserBroader">
					<div class="span10 offset1 well">
						<table cellspacing="0" cellpadding="0" border="0"
							id="hiveVocabBroaderTable" class="table table-striped table-hover">
							<thead>
								<tr>
									<th></th>
								</tr>
							</thead>
							<tbody>

								<g:each in="${conceptProxy.broader.keySet()}" var="key">
									<tr id="${conceptProxy.broader.get(key)}"
										onclick="processSelectOfTermAsCurrent('${conceptProxy.broader.get(key)}')">
										<td>
											${key}
										</td>
									</tr>
								</g:each>
							</tbody>
						</table>
					</div>
				</div>
				<div class="row-fluid" id="conceptBrowserRelated">
					<div class="span10 offset1 well">
						<table cellspacing="0" cellpadding="0" border="0"
							id="conceptBrowserRelatedTable" class="table table-striped table-hover">
							<thead>
								<tr>
									<th></th>
								</tr>
							</thead>
							<tbody>

								<g:each in="${conceptProxy.related.keySet()}" var="key">
									<tr id="${conceptProxy.related.get(key)}"
										onclick="processSelectOfTermAsCurrent('${conceptProxy.related.get(key)}')">
										<td>
											${key}
										</td>
									</tr>
								</g:each>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	/**
	 * page level action signals to select a new vocabulary
	 */
	function processVocabularySelection(vocabName) {
		if (vocabName == null || vocabName == "") {
			setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
			return false;
		}
		resetVocabulary(vocabName);
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

		browseToUri(termUri);
	}

	$(function() {

	});
</script>