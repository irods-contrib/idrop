/*
 Central module registry for the entire application
 */


(function () {
    'use strict';
    // this function is strict...
}());

angular.module('app', ['ngRoute', 'ngResource', 'httpInterceptorModule', 'home', 'login', 'flash']);

angular.module('flash', []);

angular.module('app')

    .config(function ($routeProvider) {
        // route for the home page
        $routeProvider.when('/home', {
            templateUrl: 'assets/home/home-angularjs.html',
            controller: 'homeController'
        })

            // route for the login page
            .when('/login', {
                templateUrl: 'assets/home/login-angularjs.html',
                controller: 'loginController'
            }).otherwise({redirectTo: "/home"});
    })

/**
 * Provides a directive to prevent form loading per http://stackoverflow.com/questions/12319758/angularjs-clicking-a-button-within-a-form-causes-page-refresh
 * use the prevent-default directive on form buttons
 */.directive('preventDefault', function () {
        return function (scope, element, attrs) {
            angular.element(element).bind('click', function (event) {
                event.preventDefault();
                event.stopPropagation();
            });
        }
    })

    .controller('appController', function () {


    });

/**
 * Represents an iRODS account identity for login
 * Created by Mike on 2/28/14.
 */
(function () {
    'use strict';
    // this function is strict...
}());
var irodsAccount = function (host, port, zone, userName, password, authType, resource) {
    return {
        host: host,
        port: port,
        zone: zone,
        userName: userName,
        password: password,
        authType: authType,
        resource: resource

    };
};


/**
 * Supporting code for integration with angular-translate
 *
 * see:
 *
 * http://www.ng-newsletter.com/posts/angular-translate.html
 *
 * Created by mikeconway on 3/13/14.
 */

angular.module('angularTranslateApp', ['pascalprecht.translate']).config(function ($translateProvider) {
        $translateProvider.translations('en', {
            HOST: 'Host',
            LOGIN_HEADLINE: 'Please login to iDrop',
            NEED_HELP: 'Need help?',
            PASSWORD: 'Password',
            PORT: 'Port',
            SIGN_IN: 'Sign in',
            USER_NAME: 'User Name',
            ZONE: 'Zone'
        });
        $translateProvider.preferredLanguage('en');
    });

/**
 * Flash error processing service
 * Created by mikeconway on 3/18/14.
 */

angular.module('flashModule', []).factory("flash", function ($rootScope) {
        var queue = [];
        var currentMessage = "";

        $rootScope.$on("$routeChangeSuccess", function () {
            currentMessage = queue.shift() || "";
        });

        return {
            setMessage: function (message) {
                queue.push(message);
            },
            getMessage: function () {
                return currentMessage;
            }
        };
    });


/**
 *
 * Defines interceptors for auth and error handling with the REST back end
 * Created by mikeconway on 3/7/14.
 *
 *
 */

angular.module('httpInterceptorModule', []).factory('myHttpResponseInterceptor', ['$q', '$location', '$log', function ($q, $location, $log) {
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
                    alert("redirect to login === remove me later!!!!!");
                    $location.path("/login");
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
    }])//Http Intercpetor to check auth failures for xhr requests
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('myHttpResponseInterceptor');
    }]);
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

/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home', ['httpInterceptorModule', 'angularTranslateApp', 'virtualCollectionsModule'])

    .controller('homeController', ['$scope','virtualCollectionsService','$translate', '$log', '$http', '$location',function ($scope, virtualCollectionsService, $translate, $log, $http, $location) {

        $scope.listVirtualCollections = function () {

            $log.info("getting virtual colls");
            virtualCollectionsService.listUserVirtualCollections().then(function (virColls) {
                console.log(virColls.data);
                $scope.virtualCollections = virColls.data;
            });
        };

        $scope.hideDrives = "false";
        /*
        Init the virtual collections
         */

           $scope.listVirtualCollections();

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
    }]);




/**
 * Created by mikeconway on 3/8/14.
 */
/**
 * Main angular module for login/logout views
 *
 * @author Mike Conway (DICE)
 */


angular.module('login', [ 'httpInterceptorModule', 'angularTranslateApp','userServiceModule'])


    .config(function () {
        /*
         * configuration block
         */

    })


    /*
     * login controller fçunction here
     */

    .controller('loginController', ['$scope', '$translate', '$log', '$http', '$location', 'userService', function ($scope, $translate, $log, $http, $location, userService) {

        $scope.login = {};

        $scope.changeLanguage = function (langKey) {
            $translate.use(langKey);
        };

        $scope.getLoggedInIdentity = function () {
            return userService.retrieveLoggedInIdentity.success(function(identity) {$scope.loggedInIdentity = identity});

        };


        $scope.submitLogin = function () {
            var actval = irodsAccount($scope.login.host, $scope.login.port, $scope.login.zone, $scope.login.userName, $scope.login.password, "STANDARD", "");
            $log.info("irodsAccount for host:" + actval);
            $http({
                method: 'POST',
                url: 'login',
                data: actval,
                headers: { 'Content-Type': 'application/json' }  // set the headers so angular passing info as request payload
            }).success(function (data) {
                    $log.info(data);
                   // userService.setLoggedInIdentity(data);
                    $location.path("/home");

                });
        };

    }]);









