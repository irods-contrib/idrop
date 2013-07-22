<div id="hiveDetailsDialog" class="container-fluid">

	<div class="row-fluid">
		<div class="span12 alert alert-info">
			<g:message code="heading.vocabulary" />
		</div>
	</div>
	<div class="row-fluid">
		<div class="span2">
			<h6>
				<g:message code="text.path" />
			</h6>
		</div>
		<div class="span10">
			<em>
				${absPath}
			</em>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span2">
			<h6>
				<g:message code="text.comment" />
			</h6>
		</div>
		<div class="span10">
			<g:form id="applyVocabularyForm" name="applyVocabularyForm">
				<g:textArea class="text" id="comment" name="comment" value="${comment}"
					style='width: 500px; height: 200px;' />
			</g:form>
		</div>
	</div>
	<div class="row-fluid" style="margin-left:25px">
		<div class="span2 offset5">
			<button type="button" class="btn  btn-primary" style="margin-left:60px"
				onclick="processUpdateVocabulary('${absPath}', '${conceptProxy.origin}', '${conceptProxy.URI}')">
				<g:message code="text.update" />
			</button>
		</div>
		<div class="span2 offset0" style="margin-left:15px">
			<button type="button" class="btn  btn-inverse" 
				onclick="processCancelUpdateVocabulary()">
				<g:message code="text.cancel" />
			</button>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span2">
			<h6>
				<g:message code="text.vocabulary" />
			</h6>
		</div>
		<div class="span10">
			<em>
				${conceptProxy.origin}
			</em>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span2">
			<h6>
				<g:message code="text.preferred.label" />
			</h6>
		</div>
		<div class="span10">
			<em>
				${conceptProxy.preLabel}
			</em>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span2">
			<h6>
				<g:message code="text.uri" />
			</h6>
		</div>
		<div class="span10">
			<em> <em> ${conceptProxy.URI}</em>
		</div>
	</div>

</div>
<script>

function processUpdateVocabulary(absPath, vocabulary, uri) {
	if (uri == null || uri == "") {
		setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
		return false;
	}
	
	if (vocabulary == null || vocabulary == "") {
		setErrorMessage(jQuery.i18n.prop('msg_no_form_data'));
		return false;
	}

	if (absPath == null || absPath == "") {
		setErrorMessage(jQuery.i18n.prop('msg_path_missing'));
		return false;
	}

	var comment = $("#comment").val();
	updateHiveTerm(absPath, vocabulary, uri, comment);

}

</script>
