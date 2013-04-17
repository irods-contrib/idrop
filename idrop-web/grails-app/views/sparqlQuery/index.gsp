<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/search" />
<g:javascript library="mydrop/hivequery" />
</head>

<ul class="nav nav-tabs" id="searchTabs">
	<li><a href="#hiveQueryTab">HIVE Query</a></li>
	<li><a href="#resultsTab">Search Results</a></li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="hiveQueryTab">
		<g:render template="/sparqlQuery/hiveQueryForm" />
	</div>
	<div class="tab-pane" id="resultsTab">
		<div id="resultsTabInner">Results Here...
		</div>

	</div>

</div>

<script>
	$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});
		$("#topbarSearch").addClass("active");

		$('#searchTabs a').click(function(e) {

			e.preventDefault();
			$(this).tab('show');
			var state = {};
			var tabId = this.hash
			state["tab"] = tabId;
			$.bbq.pushState(state);
		});

		$(window).bind('hashchange', function(e) {
			//processTagSearchStateChange( $.bbq.getState());
		});

		$(window).trigger('hashchange');

	});
</script>