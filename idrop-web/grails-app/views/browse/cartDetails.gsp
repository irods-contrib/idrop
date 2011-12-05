<form id="User" name="cartTableForm">
<div>
	
			<table cellspacing="0" cellpadding="0" border="0"
				id="cartTable" style="width: 100%;">
				<thead>
					<tr>
						<th></th>
						<th>File Name</th>
					</tr>
				</thead>
				<tbody>
					<g:each in="${cart}" var="entry">
						<tr id="${entry}">
							<td><g:checkBox name="selectCart" 
									value="${entry}" checked="false" /></td>
							<td>${entry}</td>
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
</div>
</form>
<script>

	var cartTable;

	$(function() {
		cartTable = $("#cartTable").dataTable();
	});


	
	</script>