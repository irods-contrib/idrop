
<div id="gallery">

	<g:each in="${collection}" var="entry">
		<g:galleryEntryIfImage entry="${entry}">
			<img
				src="${createLink(controller:'image',action:'generateThumbnail',params:[absPath:entry.formattedAbsolutePath])}"
				alt="${entry.pathOrName}" abspath="${entry.formattedAbsolutePath}" />
				<div><h2>${entry.formattedAbsolutePath}</h2>
				<br/>
				<h2>Size: ${entry.dataSize}</h2>
				<br/>
				<h2>Click <a href="${resource(absolute:true,dir:'file/download',file:entry.formattedAbsolutePath)}"> here </a> to download full size image</h2>
				</div>

		</g:galleryEntryIfImage>
	</g:each>
</div>

<script type="text/javascript">
	jQuery(document).ready(function($) {
		   Galleria.loadTheme(context + '/js/themes/classic/galleria.classic.min.js');
		   $("#gallery").galleria({
		        width: 800,
		        height: 750,
		        dataConfig: function(img) {
		            return {
		                description: $(img).next('div').html()
		            }
		        }
		    });
	});
	
	function lightboxImageLoaded(data) {
		alert("loaded:" + data);
	}

	
</script>