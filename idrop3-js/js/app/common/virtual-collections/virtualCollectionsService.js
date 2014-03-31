/**
 * Service providing access to virtual collections
 */

angular.module('virtualCollectionsModule', [])

    .factory('virtualCollectionsService', ['$http', '$log', function ($http, $log) {

        var virtualCollectionsService = {

            virtualCollections: [],

            listUserVirtualCollections: function () {
                $log.info("doing get of virtual collections");

                return $http({method: 'GET', url: 'virtualCollection'}).success(function (data) {
                    virtualCollections = data;
                }).error(function () {
                        virtualCollections = [];
                    });

            }

        };

        return virtualCollectionsService;


    }]);
