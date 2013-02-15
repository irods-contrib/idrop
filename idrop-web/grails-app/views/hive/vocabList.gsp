<div class="well ">
<div class="pull-left">
<g:img dir="images" file="Hive_Logo.png" width="80" height="40"/>
</div>
<div >
	<g:message code="heading.hive" />
</div>
</div>

<div >
	<div >
		<table cellspacing="0" cellpadding="0" border="0" id="hiveVocabTable"
			class="table table-striped table-hover">
			<thead>
				<tr>
					<th></th>
					<th>Vocabulary</th>
				</tr>
			</thead>
			<tbody>
				<g:each in="${vocabs}" var="vocab">
					<tr id="${vocab}">
						<td><g:checkBox name="selectedVocab" value="${vocab}"
								checked="false" /></td>
						<td>
							${vocab}
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
</div>
