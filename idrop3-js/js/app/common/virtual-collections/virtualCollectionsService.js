/**
 * Service providing access to virtual collections
 */
(function () {
    'use strict';
    // this function is strict...
}());
angular.module('virtualCollectionsModule', [])

    .factory('virtualCollectionsService', ['$http', '$log', '$q',function ($http, $log) {

        return {
        listUserVirtualCollections: function (irodsAccount) {

            if (!irodsAccount) {
                throw new Error("no iRODS account");
            }

            $log.info("doing get of virtual collections");
            var response;

            $http({method: 'GET', url: '/virtualCollections'}).success(function (data, status, headers, config) {
                    console.log("success!");
                    console.log("data is:" + data);

                    console.log("returning:" + data);
                    return data;

                }).error(function (data, status, headers, config) {

                });

            console.log("falling out");


        }
    }


}])
;
