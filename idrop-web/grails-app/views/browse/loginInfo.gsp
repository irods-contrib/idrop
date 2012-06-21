<span id="loginInfo"><g:message code="text.user" />:${irodsAccount.userName}&nbsp;&nbsp;<g:message code="text.zone" />:${irodsAccount.zone}&nbsp;&nbsp;<g:message code="text.resource" />:<g:select name="defaultStorageResource" id="defaultStorageResource" from="${resources}" value="${irodsAccount.defaultStorageResource}" onchange="topBarDefaultResourceChanged()"/>
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