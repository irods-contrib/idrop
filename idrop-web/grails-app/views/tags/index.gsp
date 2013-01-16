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

			//alert("click!");
			  //e.preventDefault();
			  $(this).tab('show');
		});

		
		refreshTagCloud();

	});

	function refreshTagCloudButtonClicked() {

		refreshTagCloud();
	}
</script>