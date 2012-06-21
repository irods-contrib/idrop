<span id="loginInfo">
<b><g:message code="text.user" />:</b>${irodsAccount.userName}&nbsp;&nbsp;<b><g:message code="text.zone" />:</b>${irodsAccount.zone}&nbsp;&nbsp;<b><g:message code="text.resource" />:</b><g:select name="defaultStorageResource" id="defaultStorageResource" from="${resources}" value="${irodsAccount.defaultStorageResource}" onchange="topBarDefaultResourceChanged()"/>
</span>
<script type="text/javascript">
function topBarDefaultResourceChanged() {
	var resource = $("#defaultStorageResource").val();
	if (resource == null) {
		return false;
	}
	setDefaultStorageResource(resource);
}
</script>