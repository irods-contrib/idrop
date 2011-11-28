
<div id="userTopSection" class="box">
<div id="searchMenu" class="fg-buttonset fg-buttonset-multi"
			style="float: left, clear:both;margin:5px;">

			<label for=""><g:message code="heading.user.search" /></label>
			<g:select name="userSearchType" id="userSearchType"
				from="${['name']}" />
			<input id="userSearchTerm" type="text" name="userSearchTerm" /> 
			<button type="button" id="searchUser"
				class="ui-state-default ui-corner-all" value="searchUser"
				onclick="searchUser()")>
				<g:message code="text.search" />
			</button>
		</div>
	<div id="userToolbar" class="fg-toolbar ui-widget-header">
		
		<div id="userMenu" class="fg-buttonset fg-buttonset-multi"
			style="float: left, clear :   both;">
			<button type="button" id="reloadUserButton"
				class="ui-state-default ui-corner-all" value="reloadUser"
				onclick="refreshUsers()")>
				<g:message code="default.button.reload.label" />
			</button>
		</div>
	</div>

	<div id="userTableDiv" style="overflow: auto;">
		<!--  cart table -->
	</div>

	<script type="text/javascript">

	$(function() {
		//refreshCartFiles();
	});

	</script>