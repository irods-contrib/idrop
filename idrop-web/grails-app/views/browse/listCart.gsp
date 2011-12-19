<div id="cartTopSection" class="box" style="width:100%;height:100%;">

<div id="cartToggleDiv" style="width:100%;height:100%;">
<div id="cartToolbar" class="fg-toolbar ui-widget-header" style="height:10%;">
	<div id="cartMenu" class="fg-buttonset fg-buttonset-multi"
		style="float: left, clear: both;">
		<button type="button" id="clearCartButton"
			class="ui-state-default ui-corner-all" value="clearCart"
			onclick="clearCart()")><g:message code="default.button.clear.label" /></button>
		<button type="button" id="deleteFromCartButton"
			class="ui-state-default ui-corner-all" value="deleteFromCart"
			onclick="deleteFromCart()")><g:message code="default.button.delete.label" /></button>
			<button type="button" id="reloadCartButton"
			class="ui-state-default ui-corner-all" value="reloadCart"
			onclick="refreshCartFiles()")><g:message code="default.button.reload.label" /></button>
					<button type="button" id="checkout"
				class="ui-state-default ui-corner-all" value="checkout"
				onclick="checkOut()")><g:message code="text.check.out" /></button>
	</div>
</div>
<div id="cartTableDiv" style="overflow:auto;height:90%;">
<!--  cart table -->
</div>
</div>
<div id="cartAppletDiv">
<!--  empty div --></div>
</div>
<script type="text/javascript">

	$(function() {
		refreshCartFiles();
	});

	</script>