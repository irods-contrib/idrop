/**
 * Service providing access to virtual collections
 */

angular.module('virtualCollectionsModule', [])

    .factory('virtualCollectionsService', ['$http', '$log', function ($http, $log) {

        var virtualCollectionsService = {

            virtualCollections: [],
            virtualCollectionContents: [],
            selectedVirtualCollection : {},

            listUserVirtualCollections: function () {
                $log.info("doing get of virtual collections");

                return $http({method: 'GET', url: 'virtualCollection'}).success(function (data) {
                    virtualCollections = data;
                }).error(function () {
                        virtualCollections = [];
                    });
            },

            listUserVirtualCollectionData: function (vcName) {
                $log.info("listing virtual collection data");

                if (!vcName) {
                    virtualCollectionContents = [];
                    return;
                }

                return $http({method: 'GET', url: 'virtualCollection'}).success(function (data) {
                    virtualCollections = data;
                }).error(function () {
                        virtualCollections = [];
                    });

            }

        };

        return virtualCollectionsService;

    }]);
