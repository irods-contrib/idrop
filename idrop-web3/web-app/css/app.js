/**
 * Represents an iRODS account identity for login
 * Created by Mike on 2/28/14.
 */
(function () {
    'use strict';
    // this function is strict...
}());
var irodsAccount = function (host, port, zone, user, password, authType, resource) {
    return {
        host:host,
        port:port,
        zone:zone,
        user:user,
        password:password,
        authType:authType,
        resource:resource

    };
};


/**
 *
 * Defines interceptors for auth and error handling with the REST back end
 * Created by mikeconway on 3/7/14.
 *
 *
 */

(function () {
    'use strict';
    // this function is strict...
}());

angular.module('httpInterceptorModule', [])
.factory('myHttpResponseInterceptor',['$q','$location','$log',function($q,$location, $log){
        return {
            // On request success
            request: function (config) {
                // console.log(config); // Contains the data about the request before it is sent.

                // Return the config or wrap it in a promise if blank.
                return config || $q.when(config);
            },

            // On request failure
            requestError: function (rejection) {
                // console.log(rejection); // Contains the data about the error on the request.

                // Return the promise rejection.
                return $q.reject(rejection);
            },

            // On response success
            response: function (response) {
                // console.log(response); // Contains the data from the response.
                $log.info(response);

                // Return the response or promise.
                return response || $q.when(response);
            },

            // On response failture
            responseError: function (rejection) {
                // console.log(rejection); // Contains the data about the error.
                $log.error(rejection);
                var status = rejection.status;

                if (status == 401) { // unauthorized - redirect to login again
                    window.location = "/login";
                } else if (status == 400) { // validation error display errors
                    alert(JSON.stringify(rejection.data.errors)); // here really we need to format this but just showing as alert.
                } else {
                    // otherwise reject other status codes
                    return $q.reject(rejection);
                }
                // Return the promise rejection.
                //return $q.reject(rejection);
            }
        };
}])
//Http Intercpetor to check auth failures for xhr requests
.config(['$httpProvider',function($httpProvider) {
    $httpProvider.interceptors.push('myHttpResponseInterceptor');
}]);
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

           var promise =  $http({method: 'GET', url: '/virtualCollections'});

           return promise;


        }
    }


}])
;

/**
 * Main angular module for home views
 *
 * @author Mike Conway (DICE)
 */

function genId(path) {
    return encodeURIComponent(encodeURIComponent(path));
}

angular.module('home', ['ngRoute', 'ngResource', 'httpInterceptorModule'], function ($provide, $routeProvider, $locationProvider) {

    // route for the home page
    $routeProvider.when('/home', {
        templateUrl: 'assets/home/home-angularjs.html',
        controller: 'homeController'
    })

        // route for the login page
        .when('/login', {
            templateUrl: 'assets/home/login-angularjs.html',
            controller: 'loginController'
        })
        .otherwise({redirectTo: "/home"});
})

    .config(function () {
        /*
         * configuration block
         */


    })

    /*
     * Home controller function here
     */
    .controller('homeController', function ($scope) {

        $scope.hideDrives = "false";
        // create a message to display in our view
        $scope.message = 'Everyone come and see how good I look!';
        /*
         * Cause the collections panel on the left to display
         */
        $scope.showCollections = function () {
            alert("show collections");
            $scope.hideDrives = "false";
        };

        /*
         * Cause the collections panel on the left to be hidden
         */
        $scope.hideCollections = function () {
            $scope.hideDrives = "true";
        };


    });

/**
 * Created by mikeconway on 3/8/14.
 */
/**
 * Main angular module for login/logout views
 *
 * @author Mike Conway (DICE)
 */


angular.module('login', ['ngRoute', 'ngResource', 'httpInterceptorModule'])


    .config(function () {
        /*
         * configuration block
         */


    })

    /*
     * login controller function here
     */

    .controller('loginController', function ($scope) {
// create a message to display in our view
        $scope.message = 'Everyone come and see how good I look!';


    });

