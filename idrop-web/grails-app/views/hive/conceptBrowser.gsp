<div class="container-fluid">
	<div class="row-fluid">
		<div class="span6">
			<ul id="conceptBrowserVocabTabs" class="nav nav-tabs">
				<g:each in="${vocabs}" var="vocab">
					<li><a href="${'#' + vocab}">
							${vocab}
					</a></li>
				</g:each>
			</ul>
			<div class="tab-content">
			  <div class="tab-pane active" id="home">
			  	<g:each in="${vocabs}" var="vocab">
					<div class="tab-pane" id="${vocab}">
					</div>
				</g:each>
			</div>
		</div>
		<div class="span6">
			<!--selected item content-->
		</div>
	</div>
</div>
<script>


function loadFirstConceptBrowserVocabTab() {
	$('#conceptBrowserVocabTabs a:first').tab('show');
}

$(function() {
	$('#conceptBrowserVocabTabs a').click(function (e) {
		  e.preventDefault();
		  alert(this);
		  $(this).tab('show');
	});
		
	loadFirstConceptBrowserVocabTab();
});



</script>