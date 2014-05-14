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
