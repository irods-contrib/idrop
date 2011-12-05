<head>
  <meta name="layout" content="main" />
<g:javascript library="mydrop/home" />
<g:javascript library="mydrop/search" />
<g:javascript library="mydrop/metadata" />
</head>
<div id="tabs" class="wrapper" style="height: 820px;position:relative;">
  <ul>
    <li><a href="#quickView"><g:message code="text.home" />
      </a>
    </li>
    <li><a href="#search"><g:message code="text.search" />
      </a>
    </li>
    <li><a href="#browse"><g:message code="text.browse" />
      </a>
    </li>
  </ul>
  <div id="quickView" class="wrapper">
    <h1>Home View Here...</h1>
  </div>
  <div id="search" class="wrapper">
    <div id="searchDivOuter"
         style="display: block; width: 95%; height: 90%; position: relative; overflow:hidden;"
         class="ui-layout-center">
      <!--  this will be filled in with the search results table -->
      <div id="searchTableDiv" style="width:100%;height:100%;overflow:auto;">
        <!--  search table display di -->
      </div>
    </div>
  </div>
  <div id="browse">
    <div id="browser" class="wrapper">
      <div id="browseToolbar"
           style="height: 35px; position: relative; display: block; width: auto;">
        <div id="browseToolbarSubBox" class="ui-widget-header fg-toolbar"
             style="height: 35px;">
          <div id="browseMenu" class="fg-buttonset fg-buttonset-multi"
               style="float: left">

            <button type="button" class="ui-state-default ui-corner-all"
                    id="refreshTree" value="refreshTree" onclick="refreshTree()")>
                    <g:message code="text.refresh" />
            </button>
            <g:message code="text.display.option" />
            :
            <g:select name="browseDisplayOption" id="browseDisplayOption"
                      from="${['info', 'sharing', 'metadata']}"
                      noSelection="${['details':'details']}" onChange="setBrowseMode()" />
          </div>
        </div>
      </div>
      <g:render template="/common/panelmessages" />

      <div id="dataTreeView" style="width: 100%; height: 700px; overflow:visible;">
        <!--  no empty divs -->
        <div id="dataTreeDiv" class="ui-layout-west"
             style="width: 25%; height: 700px; overflow: visible;">
          <!--  no empty divs -->
        </div>
        <div id="infoDivOuter"
             style="display: block; width: 75%; height:100%; position:relative; overflow:visible;"
             class="ui-layout-center">
          <div id="infoDiv" style="overflow:visible; position:relative;">
            <h2>
              <g:message code="browse.page.prompt" />
            </h2>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
var dataLayout;
var tabs;
var globalMessageArea = "#javascript_message_area";
$(document).ready(function() {

         $.ajaxSetup({ cache: false }); 
		
        dataLayout = $("#dataTreeView").layout({ 
                applyDefaultStyles: false,
                size: "auto",
                west__minSize: 200,
                west__resizable: true		
                });

        retrieveBrowserFirstView();
          //  $("#infoDivOuter").droppable();
        tabs = $( "#tabs" ).tabs({		
        } );
});

</script>