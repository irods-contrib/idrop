<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/home" />
</head>
<div class="row-fluid">
   <div class="span12">
    	<center><h1>iDrop Dashboard</h1></center>
   </div>
<div class="row-fluid">
	<div class="span1 offset4" ><g:img dir="images" file="upload.png" style="margin-top:40px;" width="100" height="100"/></div>   
	<div class="span4 well"><h2>Quick upload</h2> - Use this option to quickly upload files to iRODS, automatically organized based on rules on the current grid.</div>  		
</div>
<div class="row-fluid">
 	<div class="span12">
    	<center><h1>iDrop Quick Folders</h1></center>
	</div>
</div>
<g:render template="/common/browseLegend" />

<div>
<h2>stuff</h2>
<a href="#" id="tooltip" rel="tooltop" 	data-original-title="Testing 123!!!"> Sample Text</a>

 <script type="text/javascript">
    $(document).ready(function () {
        $("#tooltip").tooltip({ selector: "a" });
      });
</script>


</div>


<div class="row-fluid">
	<div class="span8 offset2">
	<table class="table table-striped table-hover">
  <thead>
    <tr>
      <th></th>
      <th>Action</th>
      <th>Name</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><g:img dir="images" file="folder_icon.png" width="20" height="20"/></td>
      <td><i class="icon-folder-open"></i><i class="icon-upload"></i></td>
      <td>Some Name</td>
      <td>Description text</td>
    </tr>
     <tr>
      <td><g:img dir="images" file="folder_icon.png" width="20" height="20"/></td>
        <td><i class="icon-folder-open"></i><i class="icon-upload"></i></td>
      <td>Some Name</td>
      <td>Description text</td>
    </tr>
     <tr>
      <td><g:img dir="images" file="folder_icon.png" width="20" height="20"/></td>
        <td><i class="icon-folder-open"></i><i class="icon-upload"></i></td>
      <td>Some Name</td>
      <td>Description text</td>
    </tr>
     <tr>
      <td><g:img dir="images" file="folder_icon.png" width="20" height="20"/></td>
        <td><i class="icon-folder-open"></i><i class="icon-upload"></i></td>
      <td>Some Name</td>
      <td>Description text</td>
    </tr>
     <tr>
      <td><g:img dir="images" file="folder_icon.png" width="20" height="20"/></td>
        <td><i class="icon-folder-open"></i><i class="icon-upload"></i></td>
      <td>Some Name</td>
      <td>Description text</td>
    </tr>
     <tr>
      <td><g:img dir="images" file="folder_icon.png" width="20" height="20"/></td>
        <td><i class="icon-folder-open"></i><i class="icon-upload"></i></td>
      <td>Some Name</td>
      <td>Description text</td>
    </tr>
  </tbody>
 
	</table>
	</div>
</div>
<script type="text/javascript">
	
	$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});
		$("#topbarHome").addClass("active");

	});
</script>
