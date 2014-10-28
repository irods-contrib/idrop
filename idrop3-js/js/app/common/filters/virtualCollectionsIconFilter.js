/**
 * Filter to assign icons to virtual collections
 * Created by mikeconway on 4/7/14.
 */

angular.module('virtualCollectionFilter', []).filter('vcIcon', function ($log) {
    /**
     * Given an input type which is a virtual collection i18n icon name, convert to an appropriate icon css class
     */

    return function (inputType) {
        if (!inputType) {
            $log.info("no type, use default icon");
            return "glyphicon-folder-close";
        } else if (inputType == "virtual.collection.default.icon") {
            $log.info("use default");
            return "glyphicon-folder-close";
        } else if (inputType == "virtual.collection.icon.starred") {
            $log.info("use star icon");
            return "glyphicon-star";
        } else {
            $log.info("unknown type, use default");
            return "glyphicon-folder-close";
        }

    };
})

/**
 * Filter that will create an absolute path for a breadcrumb by chaining together parent paths
 */
    .filter('breadcrumbUrl', function ($log) {
        /**
         * Given an absolute path, make a space-saving abbreviation by redacting parts of the full string
         */

        return function (index, paths) {
            if (!index) {
                return "";
            }

            if (!paths) {
                return "";
            }

            // i know it's an array?

            if (!paths instanceof Array) {
               return "";
            }

            var totalPath = "";

            for (var i = 0; i <+ index; i ++) {
                totalPath = totalPath + "/" + paths[i];
            }

            return totalPath;

        };
    })

    .filter('fileIcon', function ($log) {
        /**
         * Given a listing entry, return the appropriate file icon
         */

        return function (listingEntry) {
            if (!listingEntry) {
                return "";
            }


         /*
         right now it's a
          */
         if (listingEntry.objectType.name == "COLLECTION") {
            return "glyphicon-folder-close";
         } else {
             return "glyphicon-file";
         }


        }
    })
    .filter('hideSizeForCollection', function ($log) {
        /**
         * Given an absolute path, make a space-saving abbreviation by redacting parts of the full string
         */

        return function (collectionListingEntry) {
            if (!collectionListingEntry) {
                return "";
            }


            if (collectionListingEntry.objectType.name == "COLLECTION") {
                return "";
            } else {
                return collectionListingEntry.displayDataSize;
            }

        };
    })
    .filter('starIcon', function ($log) {
        /**
         * Given an absolute path, make a space-saving abbreviation by redacting parts of the full string
         */

        return function (dataProfile) {

            if (!dataProfile) {
                return "glyphicon-star-empty";
            }

            if(dataProfile.data.starred) {
                return "glyphicon-star";
            } else {
                return "glyphicon-star-empty";

            }

        };
    })
    .filter('abbreviateFileName', function ($log) {
        /**
         * Given an absolute path, make a space-saving abbreviation by redacting parts of the full string
         */

        return function (fullPath) {
            if (!fullPath) {
                return "";
            }

            // check length

            var newPath = "";

            if (fullPath.length > 100) {
                newPath = fullPath.substring(0, 50);
                newPath = newPath + "...";
                newPath = newPath + fullPath.substring(fullPath.length - 50);

            } else {
                newPath = fullPath;
            }

            return newPath;
        };
    });
