<div class="container-fluid">
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
					<div class="span10 offset1 well"><g:render template="/hive/alphabetTable" />
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
			function loadFirstConceptBrowserVocabTab() {
				$('#conceptBrowserVocabTabs a:first').tab('show');
			}

			$(function() {
				$('#conceptBrowserVocabTabs a').click(function(e) {
					e.preventDefault();
					alert(this);
					$(this).tab('show');
				});

				$('#conceptBrowserVocabTabs a').on('shown', function(e) {
					e.preventDefault();
					alert(this);

				});

				loadFirstConceptBrowserVocabTab();
			});
		</script>