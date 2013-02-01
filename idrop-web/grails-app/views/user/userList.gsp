 
    <table class="table table-striped table-hover" cellspacing="0" cellpadding="0" border="0" id="userListTable">
      <thead>
        <tr>
          <th ></th>
          <th >User Name</th>
        </tr>     
      </thead> 
      <tbody>
      <g:each in="${users}" var="entry">
        <tr>
        <td style="width:10px;"><g:checkBox name="selectuser" style="width:10px;" value="${entry.nameWithZone}" checked="false" /></td>
          <td id="${entry.nameWithZone}">${entry.nameWithZone}</td>
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