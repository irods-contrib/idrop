<div id="profileTabContent"  class="clearfix" style="display:block;width:100%;height:98%;">
	<h1><g:message code="text.profile.header"/></h1>
	<div id="profileToolbar" style="display:block;">
		<g:render template="/profile/profileToolbar" />
	</div> <!--  profileToolbar -->
	<div id="profileDialogArea"><!--  div for optional profile dialogs -->
	</div>
	<div id="profileDataArea">
	<!-- area for profile data -->
	</div>
</div>
<script>

$(function() {
	loadProfileData();
});


</script>