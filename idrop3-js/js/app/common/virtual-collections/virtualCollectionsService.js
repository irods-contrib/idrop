/**
 * Service providing access to virtual collections
 */

angular.module('virtualCollectionsModule', [])

    .factory('virtualCollectionsService', ['$http', '$log', '$q', function ($http, $log, $q) {

        return {
            listUserVirtualCollections: function () {
                $log.info("doing get of virtual collections");
                var promise = $http({method: 'GET', url: 'virtualCollection'});
                return promise;
            }
        }


    }]);
