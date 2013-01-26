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
				<li id="quickUpload" class="quicknav"><a href="#"  onclick="quickUploadFromHome()">Quick upload</a></li>
				<li class="nav-header">Starred</li>
				<li id="quickStarredFiles" class="quicknav"><a href="#"  onclick="quickViewShowStarredFiles()">Starred files</a></li>
				<li id="quickStarredFolders" class="quicknav"><a href="#" onclick="quickViewShowStarredFolders()">Starred folders</a></li>
				<li class="nav-header">Shared</li>
				<li id="quickSharedByMeFolders" class="quicknav"><a href="#" onclick="quickViewShowFoldersSharedByMe()">Folders shared by me</a></li>
				<li id="quickSharedWithMeFolders" class="quicknav"><a href="#" onclick="quickViewShowFoldersSharedWithMe()">Folders shared with me</a></li>
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

	function quickViewShowFoldersSharedByMe() {
		resetQuicknav();
		$("#quickSharedByMeFolders").addClass("active");
		var url = "/home/sharedCollectionsByMe";
		var params = {
				
			}
		lcSendValueWithParamsAndPlugHtmlInDiv(url, params, "#quickViewListContainer", null);
	}

	function quickViewShowFoldersSharedWithMe() {
		resetQuicknav();
		$("#quickSharedWithMeFolders").addClass("active");
		var url = "/home/sharedCollectionsWithMe";
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