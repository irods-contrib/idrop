/**
 * Services to support starring of files and collections
 * Created by Mike Conway on 10/28/14.
 */

angular.module('StarModule',[])
    .factory('starService', ['$http', '$log', function ($http, $log) {

        var starService = {

            /**
             * Set a path in iRODS to be tagged as 'starred'.  This service acts in an idempotent fashion
             * @param path
             * @returns {*}
             */

            addStar: function (path) {

                $log.info("addStar()");

                if (!path) {
                    $log.error("path is missing");
                    throw "path is missing";
                }

                var uriPath = 'star' + path;

                return $http({method: 'PUT', url: uriPath}).success(function (data) {
                    return data;

                }).error(function () {
                    return null;
                });
            }

        };

        return starService;

    }]);

