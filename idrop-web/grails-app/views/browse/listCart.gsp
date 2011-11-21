
<div id="cartTopSection" class="box">
<div id="cartToolbar" class="fg-toolbar ui-widget-header">
	<div id="cartMenu" class="fg-buttonset fg-buttonset-multi"
		style="float: left, clear: both;">
		<button type="button" id="clearCartButton"
			class="ui-state-default ui-corner-all" value="clearCart"
			onclick="clearCart()")><g:message code="default.button.clear.label" /></button>
		<button type="button" id="deleteFromCartButton"
			class="ui-state-default ui-corner-all" value="deleteFromCart"
			onclick="deleteFromCartAcl()")><g:message code="default.button.delete.label" /></button>
			<button type="button" id="reloadCartButton"
			class="ui-state-default ui-corner-all" value="reloadCart"
			onclick="reloadCart()")><g:message code="default.button.reload.label" /></button>
					<button type="button" id="checkout"
				class="ui-state-default ui-corner-all" value="checkout"
				onclick="checkOut()")><g:message code="text.check.out" /></button>
			
	</div>
</div>
<div id="cartTableDiv">
</div>
</div>
<script type="text/javascript">

	$(function() {
		reloadCart();
	});

	</script>