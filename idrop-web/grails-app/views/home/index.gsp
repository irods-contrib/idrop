<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/home" />
</head>
<div>

	<div class="row-fluid">
		<div class="span12">
			<center>
				<h1>iDrop Dashboard</h1>
			</center>
		</div>
	</div>
	<div class="row-fluid well">
		<div class="span1 offset4">
			<g:img dir="images" file="upload.png" style="margin-top:5px;"
				width="80" height="80" />
		</div>

		<div class="span2">
			<button type="button" id="quickUpload" style="margin-top: 40px;">Quick
				Upload</button>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12">
			<center>
				<h3>iDrop Quick Folders</h3>
			</center>
		</div>
	</div>
	<!-- <g:render template="/common/browseLegend" /> -->


	<div id="uploadDialog">
		<!--  target div for creating upload dialogs -->
	</div>
	<div class="row-fluid">
		<div class=span3>
			<!--  nav for table -->
			<ul class="nav nav-list">
				<li class="nav-header">Starred</li>
				<li id="quickStarredFiles" class="quicknav"><a href="#"  onclick="quickViewShowStarredFiles()">Starred Files</a></li>
				<li id="quickStarredFolders" class="quicknav"><a href="#" onclick="quickViewShowStarredFolders()">Starred Folders</a></li>
			</ul>


		</div>
		<div id="quickViewListContainer" class="span8"></div>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});
		$("#topbarHome").addClass("active");
		quickViewShowStarredFiles();

	});


	function quickViewShowStarredFolders() {
		resetQuicknav();
		$("#quickStarredFolders").addClass("active");
		var url = "/home/starredCollections";
		var params = {
				
			}
		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#quickViewListContainer", null);
	}


	function quickViewShowStarredFiles() {
		resetQuicknav();
		$("#quickStarredFiles").addClass("active");
		var url = "/home/starredDataObjects";
		var params = {
				
			}
		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#quickViewListContainer", null);
	}

	function resetQuicknav() {
		$(".quicknav").removeClass("active");
	}

	
</script>