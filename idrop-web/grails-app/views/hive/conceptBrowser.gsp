<div>
	<table cellspacing="0" cellpadding="0" border="0"
		id="vocabFirstConceptTable" class="table table-striped table-hover">
		<thead>
			<tr>
				<th></th>
				<th>Vocabulary Tree</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${listOfPreferedLabels}" var="preferedLabels">
				<tr>
					<td><g:checkBox name="selectedLabel" value="preferedLabels" checked="false" />
					</td>
					<td>
						${preferedLabels}
					</td>
				</tr>
			</g:each>
		</tbody>
		<tfoot>
			<tr>
				<td></td>
				<td></td>
			</tr>
		</tfoot>
	</table>
</div>