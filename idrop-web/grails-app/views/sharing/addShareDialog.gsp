<div class="alert alert-info">
	<g:message code="heading.add.share" />
</div>
<div id="addShareDialogDiv">

	<form class="form-horizontal" id="addShareForm" name="addShareForm"
		action="">
		<g:hiddenField name="formAction" id="formAction" value="${formAction}"/>
		<g:if test="${flash.error}">
			<script type="text/javascript">
                $(function() { setErrorMessage("${flash.error}"); });
                </script>
		</g:if>

		<g:if test="${flash.message}">
			<script type="text/javascript">
                $(function() { setMessage("${flash.message}");});
                </script>
		</g:if>

		<div class="control-group">
			<label class="control-label" for="shareName"><g:message
					code="text.share.name" /></label>
			<div class="controls">
				<input type="text" id="shareName" value="${shareName}">
			</div>
		</div>
		<div class="control-group">
			<div class="controls">
				<button type="button" class="btn" id="btnUpdateNamedShare" onclick="updateNamedShare()">
					<g:message code="text.update" />
				</button>
				<button type="button" class="btn" onclick="closeNamedShareDialog()">
					<g:message code="text.cancel" />
				</button>
			</div>
		</div>
	</form>

</div>