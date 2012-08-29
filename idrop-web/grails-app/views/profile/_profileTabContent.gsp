<div id="profileTabContent"  class="clearfix" style="display:block;width:100%;height:98%;">
<h1>Profile</h1>
<div id="profileToolbar" style="display:block;">
	<g:render template="/profile/profileToolbar" />
</div> <!--  profileToolbar -->
<div id="profileDialogArea"><!--  div for optional profile dialogs -->
</div>
<div id="profileDataArea">
<!-- area for profile data -->
</div>
<script>

$(function() {
	loadProfileData();
});


function loadProfileData() {
	alert("loadProfileData");
	var targetDiv = "#profileDataArea";
	lcSendValueAndCallbackHtmlAfterErrorCheckPreserveMessage(
			"/profile/index",
			targetDiv, targetDiv, null);
}


</script>