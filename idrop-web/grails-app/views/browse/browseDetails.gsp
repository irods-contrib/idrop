
<g:render template="/common/panelmessages" />

<div id="browseDetailsMessageArea">
  <!--  -->
</div>
<div id="infoDialogArea"><!--  no empty divs --></div>

<div id=browseDetailsDialogArea">
  <!--  area for generating dialogs -->
</div>
<g:hiddenField id="browseDetailsAbsPath" name="absolutePath"
					value="${parent.collectionName}" />

<div style="overflow:visible; position:relative;">
  <div id="idropLiteArea">
    <!--  area to show idrop lite applet -->
  </div>
  <div id="toggleHtmlArea">
    <g:render template="/common/browseDetailsBrowseToolbar" />
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
              <g:link url="${'file/download' + entry.formattedAbsolutePath}">
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
${entry.dataSize}
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
                	"iDisplayLength" : 500

                }

        $(function() {
                dataTable = lcBuildTableInPlace("#browseDataDetailsTable", browseDetailsClick, ".browse_detail_icon", tableParams);
                $("#infoDiv").resize();
                $("#menuDownload").hide();
                                      // $(".draggableFile").draggable({ revert: true, containment: 'document', stack: '#bd', scroll:false, zindex:-1 });
        });

        /* click twistie to open details table info */
        function browseDetailsClick(minMaxIcon) {
                var nTr = minMaxIcon.parentNode.parentNode;

                if (minMaxIcon.parentNode.innerHTML.match('circle-minus')) {
                        lcCloseTableNodes(dataTable);
                } else {
                        try {
                                browseDataDetailsFunction(minMaxIcon, nTr);
                        } catch (err) {
                                console.log("error in detailsClick():" + err);
                        }

                }
        }

        /** called by browseDetailsClick() when it is decided that the details table row should be opened, go 
        to server and get the details.
        */
        function browseDataDetailsFunction(clickedIcon, rowActionIsOn) {
                /* Open this row */
                lcCloseTableNodes(dataTable);
                // nTr points to row and has absPath in id
                var absPath = $(rowActionIsOn).attr('id');
                //alert("absPath:" + absPath);
                var detailsId = "details_" + absPath;
                var detailsHtmlDiv = "details_html_" + absPath;
                var buildDetailsLayoutVal = buildDetailsLayout(detailsId);
                clickedIcon.setAttribute("class", "ui-icon ui-icon-circle-minus");
                newRowNode = dataTable.fnOpen(rowActionIsOn,
                                buildDetailsLayoutVal, 'details');
                newRowNode.setAttribute("id", detailsId);
                askForBrowseDetailsPulldown(absPath, detailsId)
		
        }

        /** The table row is being opened, and the query has returned from the server with the data, fill in the table row
        */
        function buildDetailsLayout(detailsId) {
                var td = document.createElement("TD");
                td.setAttribute("colspan", "4");

                var detailsPulldownDiv = document.createElement("DIV");
                detailsPulldownDiv.setAttribute("id", detailsId);
                detailsPulldownDiv.setAttribute("class", "detailsPulldown");
                var img = document.createElement('IMG');
                img.setAttribute("src", context + "/images/ajax-loader.gif");
                detailsPulldownDiv.appendChild(img);
                td.appendChild(detailsPulldownDiv);
                return $(td).html();
        }

        function askForBrowseDetailsPulldown(absPath, detailsId) {
		
                var url = "/browse/fileInfo";
                absPath = absPath;
                var params = {
                                absPath:absPath
                        }
			
                lcSendValueWithParamsAndPlugHtmlInDiv(url, params, ".details",
                                null);
		
        }

        function clickOnPathInBrowseDetails(data) {
        	if (data == null) {
        		throw new Exception("no absolute path provided");
        	}
        	// show main browse tab
        	 
        	  splitPathAndPerformOperationAtGivenTreePath(data, null,
        				null, function(path, dataTree, currentNode){

        		  $.jstree._reference(dataTree).open_node(currentNode);
        		  $.jstree._reference(dataTree).select_node(currentNode, true);

        			});
        }
                

       
	
</script>