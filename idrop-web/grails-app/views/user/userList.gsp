<div id="userTableWrappingDiv" style="height:93%;overflow:hidden;">

<form id="userTableForm" name="userTableForm" style="height:100%; overflow:auto;">
 
    <table cellspacing="0" cellpadding="0" border="0" id="userListTable" style="width:100%;">
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
</div>  
<script>

        var userListTable;

        $(function() {

        	$('.userDetailRow').click(function(event) {
        		requestUserPopup(event);
        		});

            
               //userListTable = $("#userListTable").dataTable({});
            	//	"sScrollX": "100%",
            	//	"sScrollXInner": "110%",
            	 //	"bFilter": false,
            		//"sScrollY": "100%",
            		//"sScrollYInner": "110%",
            	//	"bScrollCollapse": true,
            	//	 "bLengthChange": false,
            		 
            	//	"iDisplayLength": 15
            		/*"
            		 "bLengthChange": false,
            		 "bPaginate": true,
            		 "sPaginationType": "full_numbers"*/
            	//});
                //$('.draggableUser').draggable({ revert: true, containment: 'document', stack: '#bd', scroll:false, zindex:-1 });
                //var dd1 = new YAHOO.util.DD("draggableUser");
                // $('.draggableUser').draggable({ revert:true, appendTo:'body',containment:'document', zindex:9999, stack:'#tabs' });
        });

</script>