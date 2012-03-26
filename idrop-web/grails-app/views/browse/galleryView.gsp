
<div id="gallery" class="ad-gallery">
	<div class="ad-image-wrapper"></div>
	<div class="ad-controls"></div>
	<div class="ad-nav">
		<div class="ad-thumbs">
			<ul class="ad-thumb-list">

				<g:each in="${collection}" var="entry">
					<g:galleryEntryIfImage entry="${entry}">

						<li><a name="${entry.formattedAbsolutePath}"
							href="${resource(absolute:true,controller:'file',dir:'/file/download',file:entry.formattedAbsolutePath)}"
							title="${entry.pathOrName}"> <img
								src="${createLink(absolute:true,controller:'image',action:'generateThumbnail',params:[absPath:entry.formattedAbsolutePath])}"
								alt="${entry.pathOrName}" abspath="entry.formattedAbsolutePath" />
						</a></li>
					</g:galleryEntryIfImage>
				</g:each>

			</ul>
		</div>
	</div>
</div>
 <div id="descriptions">

    </div>
<script type="text/javascript">
	jQuery(document).ready(function($) {
		var galleries = $('.ad-gallery').adGallery();
		$('#switch-effect').change(function() {
			galleries[0].settings.effect = $(this).val();
			return false;
		});
		$('#toggle-slideshow').click(function() {
			galleries[0].slideshow.toggle();
			return false;
		});
		$('#toggle-description').click(function() {
			if (!galleries[0].settings.description_wrapper) {
				galleries[0].settings.description_wrapper = $('#descriptions');
			} else {
				galleries[0].settings.description_wrapper = false;
			}
			return false;
		});
		$('.ad-image').click(function(){
			alert("click!");
		});
	});
</script>