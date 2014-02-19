/**
 * Service providing access to virtual collections
 */

angular.module('virtualCollectionsModule', [])

    .factory('virtualCollectionsService', function () {

    return {

        listUserVirtualCollections: function () {
            var msg = "hello";
            return msg;
        }
    }


});