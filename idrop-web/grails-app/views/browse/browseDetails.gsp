<div id="browseDetailsMessageArea"></div>
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
        <table class="table table-striped table-hover" cellspacing="0" cellpadding="0" border="0"
               id="browseDataDetailsTable">
          <thead>
            <tr>
              <th></th>
              <th>
             
              <div class="btn-group">
  				<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">Action<span class="caret"></span></a>
 					 <ul class="dropdown-menu">
 					 	<li id="menuAddToCartDetails"><a href="#addAllToCartDetails" onclick="addSelectedToCart()"><g:message code="text.add.all.to.cart" /></a></li>
						<li id="menuDeleteDetails"><a href="#deleteAllDetails" onclick="deleteSelected()"><g:message code="text.delete.all" /></a></li>
    						<!-- dropdown menu links -->
 					 </ul>
				</div>
              
              </th>
              <th><g:message code="text.name" /></th>
              <th><g:message code="text.type" /></th>
              <th><g:message code="text.modified" /></th>
              <th><g:message code="text.length" /></th>
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
                            <g:if test="${entry.objectType.toString() == 'COLLECTION'}"><img class="icon-info-sign" onclick="infoHere('${entry.formattedAbsolutePath}')"/></g:if> <g:else><img class="icon-info-sign" onclick="infoHere('${entry.formattedAbsolutePath}')"/></g:else>
            </td>
            <td><g:if
              test="${entry.objectType.toString() == 'COLLECTION'}">
              <a href="#" id="${entry.formattedAbsolutePath}" onclick="clickOnPathInBrowseDetails(this.id)">${entry.nodeLabelDisplayValue}</a>

            </g:if> <g:else>
            	${entry.nodeLabelDisplayValue}  
            
             <!--  <g:link url="${'file/download' + entry.formattedAbsolutePath}">
${entry.nodeLabelDisplayValue} --> 
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
                	 "sDom": "<'row'<'span10'l><'span8'f>r>t<'row'<'span10'i><'span10'p>>",
                	 "aoColumns" : [
                	                {'sWidth': '20px', 'bSortable':false},
                	                {'sWidth': '20px', 'bSortable':false},
                	                { 'sWidth': '120px' },
                	                { 'sWidth': '30px' },
                	                { 'sWidth': '40px' },
                	                { 'sWidth': '40px' }
                	                
                	            ],
                	
                	"fnInitComplete": function() {
                		this.fnAdjustColumnSizing(true);
                	}
                	
                }

        $(function() {
        		//alert("building table ");
                dataTable = lcBuildTableInPlace("#browseDataDetailsTable", browseDetailsClick, ".browse_detail_icon", tableParams);
                $("#infoDiv").resize();
                $.extend( $.fn.dataTableExt.oStdClasses, {
                    "sSortAsc": "header headerSortDown",
                    "sSortDesc": "header headerSortUp",
                    "sSortable": "header"
                } );
        });

       function showLegend() {
			$("#legend").show("slow");
       }

       function hideLegend() {
			$("#legend").hide("slow");
      }


	function infoHere(path) {
		setDefaultView("info");
		selectTreePathFromIrodsPath(path);
	}
        

      
       
	
</script>