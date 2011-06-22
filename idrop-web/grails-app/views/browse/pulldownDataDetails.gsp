<div id="dataDetailsAccordion" style="width: 100%; height: 100%;">

	<h3>Data details</h3>
	<div id="pulldownTabs">
		<ul>
			<li><a href="#summary">Info</a></li>
			<li><a href="#sharing">Sharing</a></li>
			<li><a href="#metadata">Metadata</a></li>
		</ul>
		<div id="pulldownInfo"></div>
		<div id="sharing"></div>
		<div id="metadata"></div>

	</div>
</div>


<script type="text/javascript">
$(document).ready(function() {
	$( "#pulldownTabs" ).tabs();
	//retrieveBrowserFirstView();
});