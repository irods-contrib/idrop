<div class="span10 offset1 well">
	<table cellspacing="0" cellpadding="0" border="0" id="hiveVocabTable"
		class="table table-striped table-hover">
		<thead>
			<tr>
				<th></th>
			</tr>
		</thead>
		<tbody>

			<g:each in="${conceptProxy.narrower.keySet()}" var="key">
				<tr id="${conceptProxy.narrower.get(key)}"
					onclick="processSelectOfTermAsCurrent('${conceptProxy.narrower.get(key)}')"
					onmouseover="changeTextColor('${conceptProxy.narrower.get(key)}')">
					<td>
						${key}
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>