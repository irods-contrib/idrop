<div id="profileToolbar"
	style="height: 100%; overflow: visible; margin-left: auto; margin-right: auto;">

	<ul id="profileToolbarMenu" class="sf-menu">
		
		<li id="menuProfileData" class="detailsToolbarMenuItem"><a
			href="#view"><g:message code="text.view" /></a>
			<ul>
				<li id="menuRefresh"><a href="#refresh" onclick="loadProfileData()"><g:message
					code="text.refresh" /></a></li>
			</ul>
		</li>
		<li id="menuPassword" class="detailsToolbarMenuItem"><a href="#password"><g:message
					code="text.password" /></a>
			<ul>
				<li id="menuChangePassword"><a href="#changePassword" onclick="showChangePasswordDialog()"><g:message
					code="text.changePassword" /></a></li>
			
			</ul>
		</li>
	</ul>
</div>

<script type="text/javascript">

	$(function() {
		$("ul.sf-menu").superfish();
	});

</script>