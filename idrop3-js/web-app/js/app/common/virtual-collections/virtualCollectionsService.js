/**
 * Service providing access to virtual collections
 */
(function () {
    'use strict';
    // this function is strict...
}());
angular.module('virtualCollectionsModule', [])

    .factory('virtualCollectionsService', ['$http', '$log', '$q',function ($http, $log, $q) {

        return {
        listUserVirtualCollections: function (irodsAccount) {

            if (!irodsAccount) {
                throw new Error("no iRODS account");
            }

            $log.info("doing get of virtual collections");
            var response;

           var promise =  $http({method: 'GET', url: '/virtualCollections'}).then(function (data, status, headers, config) {
                $log.info("success!");
                $log.info("data is:" + data);

                $log.info("returning:" + data);
                    return data;

                },function (data, status, headers, config) {
                    $log.error("error! " + data + " status:" + status);
                });

            return promise;


        }
    }


}])
;
