 <form id="userTableForm" name="userTableForm">
    <table cellspacing="0" cellpadding="0" border="0" id="userListTable">
   
      <thead>
        <tr>
          <th></th>
          <th>User Name</th>
        </tr>     
      </thead> 
      <tbody>
      <g:each in="${users}" var="entry">
        <tr >
        <td><g:checkBox name="selectuser"
                            value="${entry.nameWithZone}" checked="false" /></td>
          <td  id="${entry.nameWithZone}" class="userDetailRow">${entry.nameWithZone}</td>
        </tr>
      </g:each>
      </tbody>

      <tfoot> 
        <tr>
        <td></td>
        <td></td>
        </tr>
      </tfoot>
    </table>
 
</form>
<script>
/*
        var userListTable;

        $(function() {

        	$('.userDetailRow').click(function(event) {
        		//requestUserPopup(event);
        		});

          
               userListTable = $("#userListTable").dataTable({
            	 	"bFilter": false,
            		"bScrollCollapse": true,
            		 "bLengthChange": false,
            		 "bPaginate": true,
            		"iDisplayLength": 15
            	});

				userListTable = $("#userListTable").dataTable({});
              	userListTable.fnAdjustColumnSizing();
               
        });
        */

</script>