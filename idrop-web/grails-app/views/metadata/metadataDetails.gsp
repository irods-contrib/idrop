<div id="detailsTopSection" class="box">
<div id="detailsToolbar" class="fg-toolbar ui-widget-header">
<div id="detailsMenu" class="fg-buttonset fg-buttonset-multi"
							style="float: left">
<button type="button" id="addMetadataButton" class="ui-state-default ui-corner-all"  value="addMetadata" onclick="prepareMetadataDialog()")>Add Metadata</button>
<button type="button" id="deleteMetadataButton" class="ui-state-default ui-corner-all" value="deleteMetadata" onclick="deleteMetadata()")>Delete Metadata</button>
</div>
</div>
<g:render template="/common/panelmessages"/>

<div id="metadataMessageArea">
	<!--  -->
</div>

<div id="metadataDialogArea">
<!--  area for generating dialogs --></div>


	<table cellspacing="0" cellpadding="0" border="0"
		id="metaDataDetailsTable" style="width: 100%;">
		<thead>
			<tr>
				<th></th>
				<th>Attribute</th>
				<th>Value</th>
				<th>Unit</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${metadata}" var="entry">
				<tr id="${entry.domainObjectUniqueName}">
					<td><g:checkBox name="selectedMetadata" />
					</td>
					<td class="editable avuAttribute">${entry.avuAttribute}</td>
					<td class="editable avuValue">${entry.avuValue}</td>
					<td class="editable avuUnit">${entry.avuUnit}</td>
				</tr>
			</g:each>

		</tbody>

		<tfoot>
			<tr>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
			</tr>
		</tfoot>
	</table>
</div>
<script type="text/javascript">

	var origData = "";
	
	$(function() {
	
		dataTable = lcBuildTableInPlace("#metaDataDetailsTable", null, null);	
		$('.editable').editable(function(content, settings) {
		
		     var avu = [];
		     var newAvu = [];

		     var currentNode = $(this);

		     if (currentNode.hasClass("avuAttribute")) {
			     avu['attribute'] = origData;
			     newAvu['attribute'] = content;
			 } else if (currentNode.hasClass("avuValue")) {
				 avu['value'] = origData;
			     newAvu['value'] = content;
			} else if (currentNode.hasClass("avuUnit")) {
				 avu['unit'] = origData;
			     newAvu['unit'] = content;
			}

			//var siblings = $(this).siblings();
			var siblings = currentNode.siblings();//parent().children();
			siblings.each(function(index) { 
				var sib = $(this);
				if (sib.hasClass("avuAttribute")) {
				     avu['attribute'] = sib.html();
				     newAvu['attribute'] =  sib.html();
				 } else if (sib.hasClass("avuValue")) {
					  avu['value'] =sib.html();
					     newAvu['value'] =  sib.html();
				} else if (sib.hasClass("avuUnit")) {
					  avu['unit'] = sib.html();
					     newAvu['unit'] =  sib.html();
				}
			});
			
			console.log("currentAVU:" + avu['attribute'] + "/" +  avu['value'] + "/" + avu['unit']);
			console.log("newAVU:" +  newAvu['attribute'] + "/" +  newAvu['value'] + "/" + newAvu['unit']);

			if (selectedPath == null) {
				throw "no collection or data object selected";
			}
			
			metadataUpdate(avu, newAvu, selectedPath);

		     
		     return(content);
		} , {type    : 'textarea',
		     submit  : 'OK',
		     cancel    : 'Cancel',
		     data: function(value, settings) {
		        origData = value;
		        return value;
		       }


		     });
	});

	
	</script>