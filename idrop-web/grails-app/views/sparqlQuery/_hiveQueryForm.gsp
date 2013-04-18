<div class="container-fluid">
	<div class="row-fluid">
		<form>
			<fieldset>
				<legend>Query by Term</legend>
				<label>Vocabulary URI</label> <input type="text" name="hiveSearchTerm" id="hiveSearchTerm"
					placeholder="enter a URI for a vocabulary term…"> <span class="help-block">Enter a URI that is a HIVE vocabulary term and search for it.</span> <label class="checkbox"> <input
					type="checkbox" id="searchRelated" name="searchRelated"> Search for items related to this term
				</label>
				<button type="button" class="btn" id="searchForTerm" onclick="callSearchForHIVETerm()"><g:message code="text.search" /></button>
			</fieldset>
		</form>
	</div>
	<div class="row-fluid">
		<div class="span6 offset2 alert">Sample queries<ul><li> <a href="#" onclick="setQuery1()">Query1 Search By Term</a></li><li> <a href="#" onclick="setQuery2()">Query2 Search by Related Term</a></li></ul></div>
	</div>
	<div class="row-fluid">
		<form>
			<fieldset>
				<legend>Query by SPARQL (returns JSON Data)</legend>
				<label>queryString</label><g:textArea id="sparqlQuery" name="sparqlQuery"  rows="5" cols="40"/><span class="help-block">Enter a complete SPARQL query and get results as JSON.</span>
				<button type="button" class="btn" id="runSparqlQuery" onclick="callSparqlQuery()">Run SPARQL</button>
			</fieldset>
		</form>
	</div>
	
</div>
<script>
function callSearchForHIVETerm() {
	var hiveTerm = $("#hiveSearchTerm").val();
	if (hiveTerm == null || hiveTerm == "") {
		setErrorMessage(jQuery.i18n.prop('msg_search_missing'));
		return false;
	}

	var related = $("#searchRelated").is(':checked');

	if (related) {
		hiveQueryByRelatedTerm(hiveTerm);
	} else {
		hiveQueryByTerm(hiveTerm);
	}
}

function callSparqlQuery() {
	var query = $("#sparqlQuery").val();
	if (query == null || query == "") {
		setErrorMessage(jQuery.i18n.prop('msg_search_missing'));
		return false;
	}

	hiveQuerySparqlReturnNewWindow(query);
}

function setQuery1() {
	$("#hiveSearchTerm").val("http://www.fao.org/aos/agrovoc#c_3208");
	$("#searchRelated").prop('checked', false);
}

function setQuery2() {
	$("#hiveSearchTerm").val("http://www.fao.org/aos/agrovoc#c_3208");
	$("#searchRelated").prop('checked', true);
}

</script>