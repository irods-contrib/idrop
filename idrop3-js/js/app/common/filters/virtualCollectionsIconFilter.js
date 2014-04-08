/**
 * Filter to assign icons to virtual collections
 * Created by mikeconway on 4/7/14.
 */

angular.module('virtualCollectionFilter', []).filter('vcIcon', function () {
    /**
     * Given an input type which is a virtual collection i18n icon name, convert to an appropriate icon css class
     */

    return function (inputType) {
       if (!inputType) {
           return "glyphicon-folder-close";
       } else if (inputType == "virtual.collection.default.icon") {
           return "glyphicon-folder-close";
       } else if (inputType == "virtual.collection.icon.starred") {
           return "glyphicon-star";
       } else {
           return "glyphicon-folder-close";
       }

    };
});
