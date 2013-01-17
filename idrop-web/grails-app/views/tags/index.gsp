<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/tag" />
</head>
<button type="button" id="refreshTags" name="refreshTags"
	onclick="refreshTagCloudButtonClicked()">
	<g:message code="text.refresh" />
</button>

<ul class="nav nav-tabs" id="searchTabs">
	<li><a href="#tagCloudTab">Tags</a></li>
	<li><a href="#resultsTab">Search Results</a></li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="tagCloudTab">
		<div style="padding: 10px;overflow:auto;">
			<div id="tagCloudDiv" style="height: 700px;width:auto;">
				<!--  tag cloud div is ajax loaded -->
			</div>
		</div>
	</div>
	<div class="tab-pane"  id="resultsTab">
	<div id="resultsTabInner">
		Search Results here
	</div>
		
	</div>

</div>

<script>
	$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});
		$("#topbarSearch").addClass("active");

		$('#searchTabs a').click(function (e) {

			  e.preventDefault();
			  $(this).tab('show');
			  var state = {};
			  var tabId = this.hash
			  state["tab"] = tabId;
			  $.bbq.pushState(state);
		});

		 $(window).bind( 'hashchange', function(e) {
             processTagSearchStateChange( $.bbq.getState());
		});

		 refreshTagCloud();

		  $(window).trigger( 'hashchange' );
	
	});

	function refreshTagCloudButtonClicked() {

		refreshTagCloud();
	}
</script>