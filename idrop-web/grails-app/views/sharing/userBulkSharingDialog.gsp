<div id="userBulkSharingDialog">
	<div id="userBulkSharingDialogMessageArea" style="width:90%;">
		<!--  no empty divs -->
	</div>
	 <div id="userBulkSharingDialogMenu" class="fg-buttonset fg-buttonset-multi"
       style="float: left, clear:both; margin: 5px;">

	 <label for=""><g:message code="heading.user.search" /></label>

    <select name="userBulkSharingSearchType" id="userBulkSharingSearchType">
      <option value="userBulkSharingName">User Name</option>				
    </select>

    <input id="userBulkSharingSearchTerm" type="text" name="userBulkSharingSearchTerm" />
    <button type="button" id="userBulkSharingSearchUser"
            class="ui-state-default ui-corner-all" value="searchUser"
            onclick="searchUsersBulkSharingDialog()")>
            <g:message code="text.search" />
    </button>   
  </div>
  </div>     

  <div id="userBulkSharingTableDiv" style="height:90%;">
    <!--  user table -->
  </div>
	
	
<script>

	$(function() {
		$("#userBulkSharingDialog").dialog({width:500, height:300,title:"Share with selected users", modal:true});
	});


	

</script>
