<script>
$(document).ready(function() {
	
	//sectabs.tabs('select', '#tabs-1');
		refreshTagCloud();
		displayCartTab();
		var sectabs = $("#secondaryTabs").tabs();
		$(sectabs).tabs('select', '#tabs-1');
		//showUserPanel();
	});

</script>
<script src="http://www.java.com/js/deployJava.js"></script>
<div id="secondaryTabs" style="position:relative;height:820px;overflow:auto;">
<ul>
	<li><a href="#tabs-1">Tags</a></li>
	<li><a href="#tabs-3">iDrop Desktop</a></li>
	<li><a href="#tabs-4">File Cart</a></li>
</ul>
<div id="tabs-1" style="height:100%;">
	<div id="tagCloudDiv" style="height:100%;">
	<!--  tag cloud div is ajax loaded -->
	</div>
</div>
<div id="tabs-3">
 <h1>Launch iDrop Desktop Client</h1>
 <p>
 iDrop Desktop is a GUI that will run in your 'System Tray'.  iDrop Desktop is great when you:
 <ul>
 <li>Have lots of big, long running transfers</li>
 <li>Transfer files often</li>
 <li>Want to set up synchronization tasks</li>
 </ul>
 </p>
 <p>
 iDrop Desktop is a Java application that will be installed locally by clicking the 'Launch' button.  Also note that a 
 Bulk Upload and File Cart are available for occasional large transfers within the browser.
 </p>
     
    <script>
        // using JavaScript to get location of JNLP file relative to HTML page
        //var url = "http://irendb.renci.org:8080/idrop/launchIDrop.html";
        //deployJava.createWebStartLaunchButton(url, '1.6.0');
    </script><a onmouseover="window.status=''; return true;" href="javascript:if (!deployJava.isWebStartInstalled(&quot;1.6.0&quot;)) {if (deployJava.installLatestJRE()) {if (deployJava.launch(&quot;${grailsApplication.config.idrop.config.idrop.jnlp}&quot;)) {}}} else {if (deployJava.launch(&quot;${grailsApplication.config.idrop.config.idrop.jnlp}&quot;)) {}}"><img border="0" src="http://java.sun.com/products/jfc/tsc/articles/swing2d/webstart.png"></a>
</div>
<div id="tabs-4">
 	<div id="cartFileDetails"><!--  cart file details div --></div>
</div>
</div>