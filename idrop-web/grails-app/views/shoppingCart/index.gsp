<head>
	<meta name="layout" content="mainNoSidebar" />
	<g:javascript library="mydrop/home" />
	<g:javascript library="mydrop/shoppingCart" />
</head>
<div id="cartTopSection">
	<div id="cartToggleDiv">
		<div id="cartToggleDiv" class="row">
			<div class="span12">
				<div id="cartToolbar" class="btn-toolbar" >
					
						<button type="button" id="clearCartButton"
							onclick="clearCart()")><g:message code="default.button.clear.label" /></button>
						<button type="button" id="deleteFromCartButton"
							 value="deleteFromCart"
							onclick="deleteFromCart()")><g:message code="default.button.delete.label" /></button>
							<button type="button" id="reloadCartButton"
							 value="reloadCart"
							onclick="refreshCartFiles()")><g:message code="default.button.reload.label" /></button>
									<button type="button" id="checkout"
								 value="checkout"
								onclick="checkOut()")><g:message code="text.check.out" /></button>
					
				</div>
			</div>
		</div>

	<div  class="row">
		<div id="cartTableDiv" class="span12">
	<!--  cart table -->
		</div>
	</div>
</div>

<div id="cartAppletDiv">
<!--  empty div --></div>
</div>
<script type="text/javascript">

	$(function() {
		$("#topbarShoppingCart").addClass("active");
		refreshCartFiles();
	});

	</script>