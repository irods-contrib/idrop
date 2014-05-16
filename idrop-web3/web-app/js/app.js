/*
 Central module registry for the entire application
 */


(function () {
    'use strict';
    // this function is strict...
}());

angular.module('app', ['ngRoute', 'ngResource', 'httpInterceptorModule', 'home', 'login', 'flash','virtualCollectionFilter','MessageCenterModule','urlEncodingModule']);

angular.module('flash', []);

angular.module('app')

    .config(function ($routeProvider) {
        // route for the home page
        $routeProvider
            /*
            .when('/home', {
            templateUrl: 'assets/home/home-angularjs.html',
            controller: 'homeController'
        })*/

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
 * Service to handle listings of and manipulations of iRODS collections.
 * <p/>
 * This is differentiated from the virtualCollectionsService in that it handles listing and display operations of what are iRODS collections, iCAT
 * data, and so forth.  Once a virtual collection is found, it is queried and the result is always an iRODS collection
 *
 * Created by Mike on 4/30/14.
 */
angular.module('CollectionsModule', [])

    .factory('collectionsService', ['$http', '$log', function ($http, $log) {

        var collectionsService = {

            pagingAwareCollectionListing: {},

            selectVirtualCollection : function(vcName) {
                //alert(vcName);
            },


            /**
             * List the contents of a collection, based on the type of virtual collection, and any subpath
             * @param reqVcName
             * @param reqParentPath
             * @param reqOffset
             * @returns {*|Error}
             */
            listCollectionContents: function (reqVcName, reqParentPath, reqOffset) {
                $log.info("doing get of the contents of a virtual collection");

                if (!reqVcName) {
                    $log.error("recVcName is missing");
                    throw "reqMcName is missing";
                }

                if (!reqParentPath) {
                    reqParentPath = "";
                }

                if (!reqOffset) {
                    reqOffset = 0;
                }

                return $http({method: 'GET', url: 'virtualCollection/' + reqVcName, params: {path: reqParentPath, offset: reqOffset }}).success(function (data) {
                    pagingAwareCollectionListing = data;

                }).error(function () {
                        pagingAwareCollectionListing = {};

                    });

            }
        };

        return collectionsService;

    }]);


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
 * Filter to url encode a link
 * Created by mike conway on 4/7/14.
 */

angular.module('urlEncodingModule', []).filter('encodeIt', function ($log) {
    /**
     * Given an input type url encode it
     */

    return function (input) {
        return window.encodeURIComponent(input);
    };
});

/**
 * Filter to assign icons to virtual collections
 * Created by mikeconway on 4/7/14.
 */

angular.module('virtualCollectionFilter', []).filter('vcIcon', function ($log) {
    /**
     * Given an input type which is a virtual collection i18n icon name, convert to an appropriate icon css class
     */

    return function (inputType) {
        if (!inputType) {
            $log.info("no type, use default icon");
            return "glyphicon-folder-close";
        } else if (inputType == "virtual.collection.default.icon") {
            $log.info("use default");
            return "glyphicon-folder-close";
        } else if (inputType == "virtual.collection.icon.starred") {
            $log.info("use star icon");
            return "glyphicon-star";
        } else {
            $log.info("unknown type, use default");
            return "glyphicon-folder-close";
        }

    };
})

/**
 * Filter that will create an absolute path for a breadcrumb by chaining together parent paths
 */
    .filter('breadcrumbUrl', function ($log) {
        /**
         * Given an absolute path, make a space-saving abbreviation by redacting parts of the full string
         */

        return function (index, paths) {
            if (!index) {
                return "";
            }

            if (!paths) {
                return "";
            }

            // i know it's an array?

            if (!paths instanceof Array) {
               return "";
            }

            var totalPath = "";

            for (var i = 0; i <+ index; i ++) {
                totalPath = totalPath + "/" + paths[i];
            }

            return totalPath;

        };
    })

    .filter('abbreviateFileName', function ($log) {
        /**
         * Given an absolute path, make a space-saving abbreviation by redacting parts of the full string
         */

        return function (fullPath) {
            if (!fullPath) {
                return "";
            }

            // check length

            var newPath = "";

            if (fullPath.length > 100) {
                newPath = fullPath.substring(0, 50);
                newPath = newPath + "...";
                newPath = newPath + fullPath.substring(fullPath.length - 50);

            } else {
                newPath = fullPath;
            }

            return newPath;
        };
    });

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
            ADD_TO_CART: 'Add to Cart',
            DELETE: 'Delete',
            HOST: 'Host',
            LOGIN_HEADLINE: 'Please login to iDrop',
            MOVE_COPY: 'Move/Copy',
            NEED_HELP: 'Need help?',
            PASSWORD: 'Password',
            PORT: 'Port',
            RENAME: 'Rename',
            SIGN_IN: 'Sign in',
            TOOLS: 'Tools',
            USER_NAME: 'User Name',
            VIEW_DETAILS: 'View Details',
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

angular.module('httpInterceptorModule', []).factory('myHttpResponseInterceptor', ['$q', '$location', '$log', 'messageCenterService', function ($q, $location, $log, messageCenterService) {
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
                if (response.config.method.toUpperCase() != 'GET') {
                    messageCenterService.add('success', 'Success');
                }
                // Return the response or promise.
                return response || $q.when(response);
            },

            // On response failture
            responseError: function (rejection) {
                // console.log(rejection); // Contains the data about the error.
                $log.error(rejection);
                var status = rejection.status;

                if (status == 401) { // unauthorized - redirect to login again
                    //alert("redirect to login === remove me later!!!!!");
                    $location.path("/login");
                } else if (status == 400) { // validation error display errors
                    //alert(JSON.stringify(rejection.data.error.message)); // here really we need to format this but just showing as alert.
                    var len = rejection.data.errors.length;
                    if(len > 0) {
                        for(var i=0; i<rejection.data.errors.length; i++) {
                            messageCenterService.add('warning',rejection.data.errors[i]);
                        }
                    }

                    return $q.reject(rejection);
                } else {
                    // otherwise reject other status codes

                    var msg = rejection.data.error;
                    if (!msg) {
                        msg = "unknown exception occurred";  //FIXME: i18n
                    }

                    messageCenterService.add('danger', msg.message);
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

                return $http({method: 'GET', url: 'virtualCollection/' + vcName}).success(function (data) {
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
angular.module('home', ['httpInterceptorModule', 'angularTranslateApp', 'virtualCollectionsModule', 'MessageCenterModule', 'CollectionsModule', 'ngRoute'])

    /*
     * handle config of routes for home functions
     */.config(function ($routeProvider) {
        // route for the home page
        $routeProvider.when('/home/:vcName', {
            templateUrl: 'assets/home/home-angularjs.html',
            controller: 'homeController',
            resolve: {

                // set vc name as selected
                selectedVc: function ($route, virtualCollectionsService) {

                    var vcData = virtualCollectionsService.listUserVirtualCollectionData($route.current.params.vcName);
                    return vcData;
                },
                // do a listing
                pagingAwareCollectionListing: function ($route, collectionsService) {
                    var vcName = $route.current.params.vcName;

                    var path = $route.current.params.path;
                    if (path == null) {
                        path = "";
                    }

                    return collectionsService.listCollectionContents(vcName, path, 0);
                }

            }
        }).when('/home', {
                templateUrl: 'assets/home/home-angularjs.html',
                controller: 'homeController',
                resolve: {
                    // set vc name as selected
                    selectedVc: function ($route) {

                        return null;
                    },
                    // do a listing
                    pagingAwareCollectionListing: function ($route, collectionsService) {
                        return {};
                    }

                }
            }).otherwise({redirectTo: "/home"});
    })

    .controller('homeController', ['$scope', 'virtualCollectionsService', '$translate', '$log', '$http', '$location', 'messageCenterService', 'collectionsService', 'selectedVc', 'pagingAwareCollectionListing', function ($scope, virtualCollectionsService, $translate, $log, $http, $location, $messageCenterService, collectionsService, selectedVc, pagingAwareCollectionListing) {

        $scope.selectedVc = selectedVc;
        $scope.pagingAwareCollectionListing = pagingAwareCollectionListing.data;
        $scope.numberSelected = 0;
        $scope.breadcrumbs = [];
        $scope.hideDrives = "false";

        /**
         * List all virtual collections for the user
         */
        $scope.listVirtualCollections = function () {

            $log.info("getting virtual colls");
            virtualCollectionsService.listUserVirtualCollections().then(function (virColls) {
                $scope.virtualCollections = virColls.data;
            });
        };

        /**
         * Handle the selection of a virtual collection from the virtual collection list, by causing a route change and updating the selected virtual collection
         * @param vcName
         */
        $scope.selectVirtualCollection = function (vcName) {
            if (!vcName) {
                $messageCenterService.add('danger', "missing vcName");
                return;
            }

            $log.info("initializing virtual collection for:" + vcName);
            $location.path("/home/" + vcName + "?path=/");

        };

        /**
         * Handle the selection of a collection from the iRODS and make a new iRODS parent
         *
         * @param vcName
         *
         */
        $scope.selectVirtualCollection = function (vcName) {
            if (!vcName) {
                $messageCenterService.add('danger', "missing vcName");
                return;
            }

            $log.info("initializing virtual collection for:" + vcName);
            $location.path("/home/" + vcName)
            $location.search("path", "/");

        };

        $scope.goToBreadcrumb = function (index, path) {

            if (!index) {
                $log.error("cannot go to breadcrumb, no index");
                return;
            }

            if (!path) {
                $log.error("no path components, cannot go to breadcrumb");
                return;
            }

            // i know it's an array?

            if (!path instanceof Array) {
                return;
            }

            var totalPath = "";

            for (var i = 0; i < +index; i++) {

                // skip a blank path, which indicates an element that is a '/' for root, avoid double slashes
                if (path[i]) {

                    totalPath = totalPath + "/" + path[i];
                }
            }


            $location.path("/home/root");
            $location.search("path", totalPath);

        };


        /**
         * Cause the collections panel on the left to display
         */
        $scope.showCollections = function () {
            alert("show collections");
            $scope.hideDrives = "false";
        };

        /**
         * Cause the collections panel on the left to be hidden
         */
        $scope.hideCollections = function () {
            $scope.hideDrives = "true";
        };

        /**
         * respond to selection of a check box in the listing
         */
        $scope.updateSelectedFromCollection = function (action, id) {
            var checkbox = action.target;
            (checkbox.checked ? $scope.numberSelected++ : $scope.numberSelected--);
        }

        /**
         * Indicates whether a virtual collection has been selected
         * @returns {boolean}
         */
        $scope.noVcSelected = function () {
            var selected = true;

            if ($scope.selectedVc == null) {
                selected = true;
            } else {
                selected = false;
            }

            return selected;
        };

        /**
         * INIT
         */

        $scope.listVirtualCollections();

    }]);




/**
 * Created by mikeconway on 3/8/14.
 */
/**
 * Main angular module for login/logout views
 *
 * @author Mike Conway (DICE)
 */


angular.module('login', [ 'httpInterceptorModule', 'angularTranslateApp','userServiceModule','MessageCenterModule'])


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









