<div class="container-fluid">
	<div class="row-fluid">
		<div class="span6">
			<ul id="conceptBrowserVocabTabs" class="nav nav-tabs">
				<g:each in="${vocabs}" var="vocab">
					<li><a href="${'#' + vocab}" id="${vocab}">${vocab}</a></li>
				</g:each>
			</ul>
			<div class="tab-content">
					<g:each in="${vocabs}" var="vocab">
						<div class="tab-pane" id="tab_${vocab}"></div>
					</g:each>
			</div>
			<div class="span6">
				<!--selected item content-->
			</div>
		</div>
	</div>
</div>
<script>
	function loadFirstConceptBrowserVocabTab() {
		$('#conceptBrowserVocabTabs a:first').tab('show');
	}

	$(function() {
		$('#conceptBrowserVocabTabs a').click(function(e) {
			e.preventDefault();
			alert(this);
			$(this).tab('show');
		});

		$('#conceptBrowserVocabTabs a').on('shown', function (e) {
			e.preventDefault();
			alert(this);
			
		});

		loadFirstConceptBrowserVocabTab();
	});
</script>