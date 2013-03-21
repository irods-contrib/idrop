<div id="conceptBrowserMain" class="container-fluid">
	<div class="row-fluid ">
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
		<div class="offset 1 span9">
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
	<div class="row-fluid">
		<div class="span6">
			<div class="container-fluid">
				<g:if test="${!conceptProxy.topLevel}">
					<div class="row-fluid">
						<div class="span10 offset2">
							<h5>
								<g:message code="text.current.term" />
							</h5>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span10 offset1 well">
							<div class="container-fluid">
								<div class="row-fluid">
									<div class="span3">
										<h6>
											<g:message code="text.preferred.label" />
											:
										</h6>
									</div>
								</div>

								<div class="row-fluid">
									<div class="offset1 span8">
										<em> ${conceptProxy.preLabel}
										</em>
									</div>
								</div>
								<div class="row-fluid">
									<div class="span3">
										<h6>
											<g:message code="text.alternate.labels" />
											:
										</h6>
									</div>
								</div>
								<g:each in="${conceptProxy.altLabel}" var="altLabel">
									<div class="row-fluid">
										<div class="offset1 span8">
											<em> ${altLabel}
											</em>
										</div>
									</div>
								</g:each>
								<div class="row-fluid">
									<div class="span3">
										<h6>
											<g:message code="text.uri" />
											:
										</h6>
									</div>
								</div>
								<div class="row-fluid">
									<div class="offset1 span8">
										<em> ${conceptProxy.URI}
										</em>
									</div>
								</div>
								<div class="row-fluid">
									<div class="offset1 span11">
										<div class="btn-group pad-around">
											<button type="button" class="btn"
												id="btnViewInSKOS"
												onclick="processViewInSKOS('${conceptProxy.origin}','${conceptProxy.URI}')">
												<g:message code="text.view.in.skos" />
											</button>

											<g:if test="${conceptProxy.selected}">
											<button type="button" class="btn"
													id="btnEditTerm"
													onclick="processEditHiveTerm('${conceptProxy.origin}','${conceptProxy.URI}')">
													<g:message code="text.edit" />
												</button>
												<button type="button" class="btn"
													id="btnRemoveTerm"
													onclick="processRemoveHiveTerm('${conceptProxy.origin}','${conceptProxy.URI}')">
													<g:message code="text.delete" />
												</button>
											</g:if>
											<g:else>
												<button type="button" class="btn"
													id="btnApplyTerm"
													onclick="processApplyHiveTerm('${conceptProxy.origin}','${conceptProxy.URI}')">
													<g:message code="text.apply.hive.term" />
												</button>
											</g:else>
										</div>
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
				<div class="row-fluid">
					<div class="span10 offset2">
						<h5>
							<g:message code="text.narrower.terms" />
						</h5>
					</div>
				</div>
				<div class="row-fluid" id="conceptBrowserNarrowerLetters">
					<div class="span10 offset1 well">
						<g:render template="/hive/alphabetTable" />
					</div>
				</div>
				<div class="row-fluid" id="conceptBrowserNarrower">
					<g:render template="/hive/narrowerTable" />
				</div>
				
			</div>
		</div>
		<div class="span6">
			<div class="container-fluid">
				<div class="row-fluid">
					<div class="span10 offset2">
						<h5>
							<g:message code="text.broader.terms" />
						</h5>
					</div>
				</div>
				<div class="row-fluid" id="conceptBrowserBroader">
					<div class="span10 offset1 well">
						<table cellspacing="0" cellpadding="0" border="0"
							id="hiveVocabBroaderTable"
							class="table table-striped table-hover">
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
				<div class="row-fluid">
					<div class="span10 offset2">
						<h5>
							<g:message code="text.related.terms" />
						</h5>
					</div>
				</div>
				<div class="row-fluid" id="conceptBrowserRelated">
					<div class="span10 offset1 well">
						<table cellspacing="0" cellpadding="0" border="0"
							id="conceptBrowserRelatedTable"
							class="table table-striped table-hover">
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
<div id="conceptBrowserDialog">
</div>
<script>
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
				+ " from vocabulary:" + vocabulary);
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

	$(function() {

	});
</script>