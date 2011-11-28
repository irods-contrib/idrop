<script>
	$(function() {
		$("#secondaryTabs").tabs();
		refreshTagCloud();
		displayCartTab();
		showUserPanel();
	});

</script>
<div id="secondaryTabs">
<ul>
	<li><a href="#tabs-1">Tags</a></li>
	<li><a href="#tabs-2">People</a></li>
	<li><a href="#tabs-3">iDrop Desktop</a></li>
	<li><a href="#tabs-4">File Cart</a></li>
	
</ul>
<div id="tabs-1">
<div id="tagCloudDiv" >

<!--  tag cloud div is ajax loaded -->

</div>
</div>
<div id="tabs-2">
<div id="userDiv"><!--  user information --></div>

<div id="tabs-3">
 <h1>Launch iDrop Desktop Client</h1>
      <script src="http://www.java.com/js/deployJava.js"></script>
    <script>
        // using JavaScript to get location of JNLP file relative to HTML page
        //var url = "http://irendb.renci.org:8080/idrop/launchIDrop.html";
        //deployJava.createWebStartLaunchButton(url, '1.6.0');
    </script><a onmouseover="window.status=''; return true;" href="javascript:if (!deployJava.isWebStartInstalled(&quot;1.6.0&quot;)) {if (deployJava.installLatestJRE()) {if (deployJava.launch(&quot;http://iren-web.renci.org:8080/idrop/idrop.jnlp&quot;)) {}}} else {if (deployJava.launch(&quot;http://iren-web.renci.org:8080/idrop/idrop.jnlp&quot;)) {}}"><img border="0" src="http://java.sun.com/products/jfc/tsc/articles/swing2d/webstart.png"></a>

</div>


<div id="tabs-4">
 <div id="cartFileDetails"><!--  cart file details div --></div>
    
</div>

</div>