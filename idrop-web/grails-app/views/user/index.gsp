<div id="userTopSection" class="box" style="height:8%;">
  <div id="searchMenu" class="fg-buttonset fg-buttonset-multi"
       style="float: left, clear:both; margin: 5px;">

    <label for=""><g:message code="heading.user.search" /></label>

    <select name="userSearchType" id="userSearchType">
      <option value="name">User Name</option>				
    </select>

    <input id="userSearchTerm" type="text" name="userSearchTerm" />
    <button type="button" id="searchUser"
            class="ui-state-default ui-corner-all" value="searchUser"
            onclick="searchUsers()")>
            <g:message code="text.search" />
    </button>   
  </div>
  </div>     

  <div id="userTableDiv" style="height:90%;">
    <!--  user table -->
  </div>

