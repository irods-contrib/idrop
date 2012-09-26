<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/home" />
<g:javascript library="mydrop/profile" />
</head>
<div id="profileTabContent" >
		<h1><g:message code="text.profile.header"/></h1>
		<div id="profileToolbar" style="display:block;">
			<g:render template="/profile/profileToolbar" />
		</div> <!--  profileToolbar -->
		<div id="profileDialogArea"><!--  div for optional profile dialogs -->
		</div>
		<div id="profileDataArea" style="clear:both;">
		<!-- area for profile data -->
		</div>
	</div>
</div>
<script>

$(function() {
	loadProfileData();
	hideAllInactive();
	$("#topbarPreferences").addClass("active");

});


</script>