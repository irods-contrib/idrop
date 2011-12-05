<form id="userTableForm" name="userTableForm">
  <div style="position:relative;overflow:visible;">
    <table cellspacing="0" cellpadding="0" border="0"
           id="userTable" style="width: 100%;overflow:visible;">
      <thead>
        <tr>
          <th></th>
          <th>User Name</th>

        </tr>     
      </thead> 
      <tbody>
      <g:each in='${users}' var='entry'>
        <tr id="${entry.name}" style="overflow:visible;">
           <td><g:checkBox name="selectUser"
                            value="${entry.name}" checked="false" />
            </td>
          <td  class="draggableUser">${entry.name}</td>
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
  </div> 
</div>
</div>
</form>  
<script>

        var userTable;

        $(function() {
                 //userTable = $("#userTable").dataTable();
                // $('.draggableUser').draggable({ revert: true, containment: 'document', stack: '#bd', scroll:false, zindex:-1 });
                //var dd1 = new YAHOO.util.DD("draggableUser");
                 $('.draggableUser').draggable({ revert:true, appendTo:'body',containment:'document', zindex:9999, stack:'#tabs' });
        });

</script>