<div class="container-fluid">
	<div class="row-fluid ">
		<!-- <div class="span1">
			<h4><g:message code="text.vocabulary" /></h4>
		</div> -->
		<div class="span10">
			<div class="btn-group pad-around" data-toggle="buttons-radio">
					<g:each in="${hiveState.selectedVocabularies}" var="selectedVocabulary">
						<button type="button" class="btn btn-primary" id="${selectedVocabulary}" onclick="processVocabularySelection('${selectedVocabulary}')">${selectedVocabulary}</button>
					</g:each>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span6">
			<div class="container-fluid">
				<div class="row-fluid">
					<div class="span10 offset1 well">current</div>
				</div>
				<div class="row-fluid" id="conceptBrowserNarrower">
					<div class="span10 offset1 well">children</div>
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

			$(function() {
				
			});
		</script>