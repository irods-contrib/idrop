<div class="container-fluid">
	<div class="row-fluid ">
		<!-- <div class="span1">
			<h4><g:message code="text.vocabulary" /></h4>
		</div> -->
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
	</div>
	<div class="row-fluid">
		<div class="span6">
			<div class="container-fluid">
				<g:if test="${!conceptProxy.topLevel}">
					<div class="row-fluid">
						<div class="span10 offset1 well">current</div>
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
									<tr id="${conceptProxy.narrower.get(key)}" onclick="processSelectOfTermAsCurrent('${conceptProxy.narrower.get(key)}')">
										<td>
											${key}
										</td>
									</tr>
								</g:each>
							</tbody>
						</table>
					</div>
				</div>
				<div class="row-fluid" id="conceptBrowserNarrowerLetters">
					<div class="span10 offset1 well">
						<g:render template="/hive/alphabetTable" />
					</div>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="container-fluid">
				<div class="row-fluid">
					<div class="span10 offset1 well">parent</div>
				</div>
				<div class="row-fluid">
					<div class="span10 offset1 well">related</div>
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
				//TODO: call to hive.js to set the new concept browser on this vocabulary
				alert("selected new vocab:" + vocabName);
			}

			/**
			 * page level action to show the vocabulary selection form for reselection
			 */
			function processShowSelectionListing() {
				alert("show vocabulary selection listing to rechoose");
			}

			function processSelectOfTermAsCurrent(termUri) {
				if (termUri == null || termUri == "") {
					setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
					return false;
				}

				alert("new uri is:" + termUri);
			}

			$(function() {

			});
		</script>