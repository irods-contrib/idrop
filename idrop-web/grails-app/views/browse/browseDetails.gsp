

<div id="browseDetailsMessageArea"></div>
<div id="infoDialogArea" style="height:0px; "></div>

<div id=browseDetailsDialogArea" style-"height:0px;></div>
<g:hiddenField id="browseDetailsAbsPath" name="absolutePath"
					value="${parent.collectionName}" />

<div style="overflow:visible; position:relative;">
  <div id="idropLiteArea">
    <!--  area to show idrop lite applet -->
  </div>
  <div id="toggleHtmlArea">
    <div id="infoDialogArea">
      <!--  no empty divs -->
    </div>
    <div id="detailsTopSection" >
      <form id="browseDetailsForm" name="browseDetailsForm">
        <table cellspacing="0" cellpadding="0" border="0"
               id="browseDataDetailsTable">
          <thead>
            <tr>
              <th></th>
              <th></th>
              <th>Name</th>
              <th>Type</th>
              <th>Modified date</th>
              <th>Length</th>
            </tr>
          </thead>
          <tbody>
          <g:each in="${collection}" var="entry">
            <tr id="${entry.formattedAbsolutePath}" class="draggableFile">
            
              <td><span
                  class="ui-icon-circle-plus browse_detail_icon ui-icon"></span>
              </td>
              <td><g:checkBox name="selectDetail"
                            value="${entry.formattedAbsolutePath}" checked="false" />
            </td>
            <td class="draggableFile"><g:if
              test="${entry.objectType.toString() == 'COLLECTION'}">
              <a href="#" id="${entry.formattedAbsolutePath}" onclick="clickOnPathInBrowseDetails(this.id)">${entry.nodeLabelDisplayValue}</a>

            </g:if> <g:else>
            	
            
              <g:link url="${context + '/file/download' + entry.formattedAbsolutePath}">
${entry.nodeLabelDisplayValue}
              </g:link>
            </g:else></td>
            <td>
${entry.objectType}
            </td>
            <td>
${entry.modifiedAt}
            </td>
            <td>
${entry.displayDataSize}
            </td>
            </tr>
          </g:each>

          </tbody>

          <tfoot>
            <tr>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
            </tr>
          </tfoot>
        </table>
      </form>
    </div>
  </div>
</div>
<script>

        var dataTable;

        tableParams = {"bJQueryUI" : true,
                	"bLengthChange": false,
                	"bFilter": false,
                	"iDisplayLength" : 500,
                	"fnInitComplete": function() {
                		//this.fnAdjustColumnSizing(true);
                	}
                	

                }

        $(function() {
        		//alert("building table ");
                dataTable = lcBuildTableInPlace("#browseDataDetailsTable", browseDetailsClick, ".browse_detail_icon", tableParams);
                $("#infoDiv").resize();
                $("#menuDownload").hide();
        });

      
       
	
</script>