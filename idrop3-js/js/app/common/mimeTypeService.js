/**
 * Created by Mike Conway on 10/23/2014.
 * Service to translate mime types to css classes for icon display and other mime-based purposes
 */

angular.module('mimeTypeServiceModule', [])

    .service('mimeTypeService', ['$log', function ($log) {

        /**
         * Get css class values based on the passed in mime type
         * @returns css classes that can be used to set icons for file type
         */
        this.iconClassFromMimeTypeFullSize = function (mimeType) {

            if (!mimeType) {
                return "glyphext2x glyphext-file-2x";
            }

            return  "glyphext2x glyphext-file-2x";

        };



    }]);
