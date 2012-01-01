<div style="height:100%;position:absolute;">
<ul>
<g:each in="${tagCloud}" var="tagVal">
	<li>${tagVal.irodsTagValue.tagData}</li>
</g:each>
</ul>
</div>