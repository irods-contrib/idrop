<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/home" />
</head>
<div>

	<div id="uploadDialog">
		<!--  target div for creating upload dialogs -->
	</div>
	<div class="row-fluid">
		<div class="span2">
			<!--  nav for table -->
			<ul class="nav nav-list">
				<li class="nav-header">Tools</li>
				<li id="quickUpload" class="quicknav"><a href="#"  onclick="quickUploadFromHome()">Quick Upload</a></li>
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

	function quickUploadFromHome() {
		
		showQuickUploadDialog();

		
	}

	
</script>