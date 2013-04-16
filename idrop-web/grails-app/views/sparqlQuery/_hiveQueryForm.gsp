<div class="container-fluid">
	<div class="row-fluid">
		<form>
			<fieldset>
				<legend>Query by Term</legend>
				<label>Vocabulary URI</label> <input type="text"
					placeholder="enter a URI for a vocabulary term…"> <span class="help-block">Enter a URI that is a HIVE vocabulary term and search for it.</span> <label class="checkbox"> <input
					type="checkbox" id="searchRelated" name="searchRelated"> Search for items related to this term
				</label>
				<button type="button" class="btn" id="searchForTerm" onclick="callSearchForHIVETerm()"><g:message code="text.search" /></button>
			</fieldset>
		</form>
	</div>
</div>
<script>
function callSearchForHIVETerm() {
	alert('search for hive term!');
}

</script>