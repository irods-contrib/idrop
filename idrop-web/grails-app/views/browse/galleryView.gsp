
<div id="gallery">

	<g:each in="${collection}" var="entry">
		<g:galleryEntryIfImage entry="${entry}">
			<img
				src="${createLink(absolute:true,controller:'image',action:'generateThumbnail',params:[absPath:entry.formattedAbsolutePath])}"
				alt="${entry.pathOrName}" abspath="entry.formattedAbsolutePath" />

		</g:galleryEntryIfImage>
	</g:each>
</div>

<script type="text/javascript">
	jQuery(document).ready(function($) {
		   Galleria.loadTheme('js/themes/classic/galleria.classic.min.js');
		   $("#gallery").galleria({
		        width: 800,
		        height: 500
		    });
	});
</script>