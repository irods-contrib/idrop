<table class="table table-striped table-hover" cellspacing="0"
	cellpadding="0" border="0">
	<tr>
		<th></th>
		<th>

			<div class="btn-group">
				<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">Action<span
					class="caret"></span></a>
				<ul class="dropdown-menu">
					<li id="menuAddToCartDetails"><a href="#addAllToCartDetails"
						onclick="addSelectedToCart()"><g:message
								code="text.add.all.to.cart" /></a></li>
					<li id="menuDeleteDetails"><a href="#deleteAllDetails"
						onclick="deleteSelected()"><g:message code="text.delete.all" /></a></li>
					<!-- dropdown menu links -->
				</ul>
			</div>

		</th>
		<th><g:message code="text.name" /></th>
		<th><g:message code="text.type" /></th>
		<th><g:message code="text.modified" /></th>
		<th><g:message code="text.length" /></th>
	</tr>
	</thead>
	<tbody>
		<g:each in="${results}" var="entry">

			<tr id="${entry.formattedAbsolutePath}">

				<td><span
					class="ui-icon-circle-plus search-detail-icon  ui-icon"></span></td>
				<td><g:checkBox name="selectDetail"
						value="${entry.formattedAbsolutePath}" checked="false" /> <span
					class="setPaddingLeftAndRight"><g:link target="_blank"
							controller="browse" action="index"
							params="[mode: 'path', absPath: entry.formattedAbsolutePath]">
							<i class="icon-folder-open "></i>
						</g:link></span></td>
				<td>
					${entry.nodeLabelDisplayValue}
				</td>
				<td>
					${entry.objectType}
				</td>
				<td>
					${entry.modifiedAt}
				</td>
				<td>
					${entry.displayDataSize}
				</td>
			</tr>
		</g:each>

	</tbody>

	<tfoot>
		<tr>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
	</tfoot>
</table>

<script type="text/javascript">
	function clickOnPathInSearchResult(data) {

	}

	function infoHere(path) {
		setDefaultView("info");
		selectTreePathFromIrodsPath(path);
	}
</script>