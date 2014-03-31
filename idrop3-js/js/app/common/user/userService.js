/**
 * Service for user and identity information.  This service will access and maintain information about the logged in user and
 * provides user related operations
 *
 * Created by Mike on 3/28/14.
 */

angular.module('userServiceModule', [])

    .service('userService', ['$http', '$log', '$q', function ($http, $log, $q) {



            /**
             * Get stored identity value
             * @returns UserIdentity JSON
             */
          this.retrieveLoggedInIdentity = function () {


                $log.info("doing get of userIdentity from server");
                return  $http({method: 'GET', url: 'user'});
            };



    }]);

