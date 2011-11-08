<div id="detailsToolbar" class="fg-toolbar ui-widget-header">
	<div id="detailsMenu" class="fg-buttonset fg-buttonset-multi"
		style="float: left">
		<button type="button" id="upload"
			class="ui-state-default ui-corner-all" value="upload"
			onclick="showUploadDialog()")>
			<g:message code="text.upload" />
		</button>

		<g:if test="${showLite}">
			<button type="button" id="idroplite"
				class="ui-state-default ui-corner-all" value="uploadWithIdropLite"
				onclick="showIdropLite()")>
				<g:message code="text.idrop.lite" />
			</button>
		</g:if>

		<button type="button" id="delete"
			class="ui-state-default ui-corner-all" value="delete"
			onclick="deleteViaToolbar()")>
			<g:message code="default.button.delete.label" />
		</button>

		<button type="button" id="newFolder"
			class="ui-state-default ui-corner-all" value="newFolder"
			onclick="newFolderViaToolbar()")>
			<g:message code="text.new.folder" />
		</button>

		<button type="button" id="rename"
			class="ui-state-default ui-corner-all" value="rename"
			onclick="renameViaToolbar()")>
			<g:message code="text.rename" />
		</button>
		
		<button type="button" id="addToCart"
			class="ui-state-default ui-corner-all" value="addToCart"
			onclick="addToCartViaToolbar()")>
			<g:message code="text.add.to.cart" />
		</button>

	</div>
</div>