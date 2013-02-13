	<div id="browseDialogArea"><!--  general area to spawn jquery dialogs --></div>
	<div id="browseToolbar" style="display:block; width:100%;position:relative;">
				
		<div id="infoDivPathArea"
			style="overflow: hidden; display:block; margin: 3px; font-size: 120%;position:relative;">
			<!-- area for the path crumb-trails -->
		</div>
		
	</div> <!--  browseToolbar -->
	<div id="browseMenuDiv" style="display:block; width:100%;position:relative;">
          <g:render template="/common/topToolbar" />
	</div> 
	
	<div id="browseDialogArea"><!--  general area to spawn jquery dialogs --></div>
	
		
	<div id="browser" class="wrapper" style="height:85%;width:100%;clear:both;overflow:hidden;">
		<div id="dataTreeView" style="width: 100%; height: 700px; overflow: hidden;">
						
			<div id="dataTreeDivWrapper"  class="ui-layout-west" style="width: 25%; height: 100%; overflow:hidden;">
				<div id="dataTreeToolbar" style="width:100%; height:4%;display:block; margin:5px;" class="fg-toolbar">
						
						<div id="dataTreeMenu">
							<button type="button" id="refreshTreeButton"
								 value="refreshTreeButton"
								onclick="refreshTree()")>
								<img class="icon-refresh"/>
							</button>
							<button type="button" id="homeTreeButton"
								
								onclick="setTreeToUserHome()")>
								<img class="icon-home"/>
							</button>
								<button type="button" id="rootTreeButton"
								 value="rootTreeButton"
								onclick="setTreeToRoot()")>
								<img class="icon-arrow-up"/>
							</button>
						</div> <!--  dataTreeMenu -->
						
				</div> <!--  dataTreeToolbar -->
						
				<div id="dataTreeDiv" class="clearfix" style="height:95%; width:100%; overflow:auto;"><!-- no empty div --></div>	
			</div> <!-- dataTreeDivWrapper -->
							
			<div id="infoDivOuter" style="display: block; width: 75%; height: 100%; position: relative; overflow: auto;"
							class="ui-layout-center">
		
				<div id="infoDiv" style=""><h2><g:message code="browse.page.prompt" /></h2>
				</div> <!--  infoDiv -->
			
			</div> <!--  infoDivOuter -->
		</div> <!--  data tree view -->
	</div> <!--  browser -->
