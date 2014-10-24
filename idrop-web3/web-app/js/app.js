/*
 Central module registry for the entire application
 */


(function () {
    'use strict';
    // this function is strict...
}());

angular.module('app', ['ngAnimate','ngRoute', 'ngResource', 'httpInterceptorModule', 'home', 'login', 'fileModule','flash','virtualCollectionFilter','MessageCenterModule','urlEncodingModule','tagServiceModule','angular-loading-bar', 'globalsModule','mimeTypeServiceModule']);

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
            })
            . otherwise({
                redirectTo: '/home/home'
            });
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

                $log.info("requesting vc:" + reqVcName + " and path:" + reqParentPath);
                return $http({method: 'GET', url: 'collection/' + reqVcName, params: {path: reqParentPath, offset: reqOffset }}).success(function (data) {
                    pagingAwareCollectionListing = data;

                }).error(function () {
                        pagingAwareCollectionListing = {};

                    });

            },
            addNewCollection: function(parentPath, childName) {
                $log.info("addNewCollection()");



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

    .filter('fileIcon', function ($log) {
        /**
         * Given a listing entry, return the appropriate file icon
         */

        return function (listingEntry) {
            if (!listingEntry) {
                return "";
            }


         /*
         right now it's a
          */
         if (listingEntry.objectType.name == "COLLECTION") {
            return "glyphicon-folder-close";
         } else {
             return "glyphicon-file";
         }


        }
    })
    .filter('hideSizeForCollection', function ($log) {
        /**
         * Given an absolute path, make a space-saving abbreviation by redacting parts of the full string
         */

        return function (collectionListingEntry) {
            if (!collectionListingEntry) {
                return "";
            }


            if (collectionListingEntry.objectType.name == "COLLECTION") {
                return "";
            } else {
                return collectionListingEntry.displayDataSize;
            }

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
 * Global state holders
 * Created by mikeconway on 7/8/14.
 */


angular.module('globalsModule', [])

    .factory('globals', function ($rootScope) {

        var f = {};


        /**
         * Saved path in case an auth exception required a new login
         * @type {null}
         */
        f.lastPath = null;
        f.loggedInIdentity = null;



        /**
         * Saved path when a not authenticated occurred
         * @param newLastPath
         */
        f.setLastPath = function (newLastPath) {
            this.lastPath = newLastPath;
        };


        /**
         * Retrieve a path to re-route when a login screen was required
         * @returns {null|*|f.lastPath}
         */
        f.getLastPath = function () {
            return this.lastPath;
        };

        /**
         * Retrieve the user identity, server info, and options for the session
         * @returns {null|*}
         */
        f.getLoggedInIdentity = function() {
            return this.loggedInIdentity;
        }

        /**
         * Set the user identity, server info, and options for the session
         * @param inputIdentity
         */
        f.setLoggedInIdentity = function(inputIdentity) {
            this.loggedInIdentity = inputIdentity;
        }

        return f;

}).factory('breadcrumbsService',  function ($rootScope, $log) {

        var bc = {};

        /**
         * Global representation of current file path for display
         */
        bc.currentAbsolutePath = null;
        bc.pathComponents = [];


        /**
         * Set the current iRODS path and split into components for use in breadcrumbs
         * @param pathIn
         */
        bc.setCurrentAbsolutePath = function (pathIn) {

            if (!pathIn) {
               this.clear();
                return;
            }

            this.currentAbsolutePath = pathIn;
            $log.info("path:" + pathIn);
            this.pathComponents = this.pathToArray(pathIn);
            $log.info("path components set:" + this.pathComponents);

        }

        /**
         * Turn a path into
         * @param pathIn
         * @returns {*}
         */
        bc.pathToArray = function(pathIn)  {
            if (!pathIn) {
                $log.info("no pathin");
                return [];
            }

            var array = pathIn.split("/");
            $log.info("array orig is:" + array);
            // first element may be blank because it's the root, so it'll be trimmed from the front

            if (array.length == 0) {
                return [];
            }

           array.shift();
            return array;

        }

        /**
         * given an index into the breadcrumbs, roll back and build an absolute path based on each element in the
         * bread crumbs array
         * @param index int wiht the index in the breadcrumbs that is the last part of the selected path
         * @returns {string}
         */
        bc.buildPathUpToIndex = function(index) {

            var path = this.getWholePathComponents();

            if (!path) {
                $log.error("no path components, cannot go to breadcrumb");
                throw("cannot build path");
            }

            var totalPath = "";

            for (var i = 0; i <= index; i++) {

                // skip a blank path, which indicates an element that is a '/' for root, avoid double slashes
                if (path[i]) {

                    totalPath = totalPath + "/" + path[i];
                }
            }

            $log.info("got total path:" + totalPath);
            return totalPath;


        }

        /**
         * Get all of the path components
         * @returns {*}
         */
        bc.getWholePathComponents = function() {

            if (!this.pathComponents) {
                return [];
            } else {
                return this.pathComponents;
            }

        }


        /**
         * Reset path data
         */
        bc.clear = function() {
            this.currentAbsolutePath = null;
            this.pathComponents = [];
        }

        return bc;

    })
;


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
            AUDIT: 'Audit',
            CHECKSUM: 'Checksum',
            COPY: 'Copy',
            CREATED: 'Created',
            COMPUTE_CHECKSUM: 'Generate Checksum',
            DATA_OWNER_NAME: 'Owner',
            DATA_OWNER_ZONE: 'Owner Zone',
            DATA_PATH: 'Data Path',
            DATA_TYPE: 'Data Type',
            DELETE: 'Delete',
            DOWNLOAD: 'Download',
            EDIT:'Edit',
            EXPIRY: 'Expiry',
            FILE: 'File',
            HOME:'Home',
            HOST: 'Host',
            IDROP: 'iDrop',
            INFO: 'Info',
            LENGTH: 'Length',
            LOGIN_HEADLINE: 'Please login to iDrop',
            LOGOUT: 'Logout',
            METADATA: 'Metadata',
            MODIFIED: 'Modified',
            MOVE_COPY: 'Move/Copy',
            MOVE_TO_TRASH: 'Move to Trash',
            NAME: 'Name',
            NEW_FILE: 'New File',
            NEW_FOLDER: 'New Folder',
            NEW_FOLDER_NAME: 'New Folder Name',
            NEED_HELP: 'Need help?',
            NO_DIRECTORY_PROVIDED: 'No folder name was provided',
            OBJECT_PATH: 'Object Path',
            PROFILE: 'Profile',
            PASSWORD: 'Password',
            PERMISSION: 'Permission',
            PERMISSIONS: 'Permissions',
            PORT: 'Port',
            RENAME: 'Rename',
            RULE: 'Rule',
            SIGN_IN: 'Sign in',
            TAGS: 'Tags',
            TOOLS: 'Tools',
            TYPE: 'Type',
            USER_NAME: 'User Name',
            UPDATE_TAGS: 'Update Tags',
            VIEWS: 'Views',
            VIEW_DETAILS: 'View Details',
            WORKFLOW:'Workflow',
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

angular.module('httpInterceptorModule', []).factory('myHttpResponseInterceptor', ['$q', '$location', '$log', 'messageCenterService', 'globals', function ($q, $location, $log, messageCenterService, globals) {
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
                    //save last path for subsequent re-login
                    if ($location.path() != "/login") {
                        $log.info("intercepted unauthorized, save the last path");
                        globals.setLastPath($location.path());
                        $log.info("saved last path:" + $location.path());
                    }

                    $location.path("/login");
                } else if (status == 400) { // validation error display errors
                    //alert(JSON.stringify(rejection.data.error.message)); // here really we need to format this but just showing as alert.
                    var len = rejection.data.errors.errors.length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            messageCenterService.add('warning', rejection.data.errors.errors[i].message);
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

        /* configure xsrf token
         see: http://stackoverflow.com/questions/14734243/rails-csrf-protection-angular-js-protect-from-forgery-makes-me-to-log-out-on
         */


    }]);
/**
 * Created by Mike Conway on 10/23/2014.
 * Service to translate mime types to css classes for icon display and other mime-based purposes
 */

angular.module('mimeTypeServiceModule', [])

    .service('mimeTypeService', ['$log', function ($log) {

        /**
         * Get css class values based on the passed in mime type
         * @returns css classes that can be used to set icons for file type
         */
        this.iconClassFromMimeTypeFullSize = function (mimeType) {

            if (!mimeType) {
                return "glyphext2x glyphext-file-2x";
            }


            if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
                return "glyphext2x glyphext-doc-2x";
            } else {

                return "glyphext2x glyphext-file-2x";
            }

        };

    }]);

/**
 * Service for free tagging and tag clouds
 *
 * Created by Mike on 3/28/14.
 */

angular.module('tagServiceModule', [])

    .service('tagService', ['$http', '$log', '$q', function ($http, $log, $q) {


            /**
             * translate an array of tag values into a free tag string
             */
          this.tagListToTagString = function (tagList) {
                if (!tagList) {
                    return "";
                }

              var tagString = "";

              for (var tag in tagList) {
                  tagString = tagString + tagList[tag].tagData + " ";
              }

              return tagString;
            };



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
 * File controller function here, representing collection and data object catalog info and operations
 */
angular.module('fileModule', ['httpInterceptorModule', 'angularTranslateApp', 'MessageCenterModule', 'ngRoute', 'tagServiceModule'])

    /*
     * handle config of routes for home functions
     */
    .config(function ($routeProvider) {
        // route for the home page
        $routeProvider.when('/file', {
            templateUrl: 'assets/file/file-master-angularjs.html',
            controller: 'fileController',
            resolve: {
                // do a listing
                file: function ($route, fileService) {
                    var path = $route.current.params.path;
                    if (path == null) {
                        path = "/";
                    }
                    return fileService.retrieveFileBasics(path);
                },
                vc: function ($route, fileService) {
                    var vc = $route.current.params.vc;
                    if (vc == null) {
                        vc = "root";
                    }
                    return vc;
                }

            }
        });
    })

    .controller('fileController', ['$scope', 'fileService', '$translate', '$log', '$http', '$location', 'messageCenterService', 'file', 'tagService','mimeTypeService','$window','vc',function ($scope, fileService, $translate, $log, $http, $location, $messageCenterService, file, tagService, mimeTypeService, $window,vc) {

        $scope.file = file;
        $scope.infoTab = true;
        $scope.vc = vc;

        $scope.checksumVisible = false;
        $scope.tagVisible = false;

        $scope.showChecksum = function() {
          $scope.checksumVisible = true;
        };

        $scope.hideChecksum = function() {
            $scope.checksumVisible = false;
        };

        $scope.showTag = function() {
            $scope.tagVisible = true;
        };

        $scope.hideTag = function() {
            $scope.tagVisible = false;
        };


        $scope.showFileMetadata = function (absPath) {
            alert("show file metadata");
            $log.info("showFileMetadata()");
            if (!absPath) {
                throw "no apsPath provided";
            }

            $log.info("absPath:" + absPath);
            $location.path("/file/metadata");
            $location.search("path", absPath);

        };

        /**
         * Return to the parent collection in the home view
         */
        $scope.selectParentCollection = function() {
            $location.path("/home/");
            $location.search("path", path);
            $location.search("vc", $scope.selectedVc.data.uniqueName);
        };

        /**
         * Get a css class string based on the mime type of the current file.  This handles null data by returning a blank class
         */
        $scope.getFileIconClass = function() {
            if (!$scope.file) {
                return "";
            }

            $log.info("getting mime type for file:" + $scope.file.data.mimeType);

            return mimeTypeService.iconClassFromMimeTypeFullSize($scope.file.data.mimeType);

        }

    }])
    .factory('fileService', ['$http', '$log', 'tagService', function ($http, $log, tagService) {

        var fileService = {
            /**
             * List the contents of a collection, based on the type of virtual collection, and any subpath
             * @param reqVcName
             * @param reqParentPath
             * @param reqOffset
             * @returns {*|Error}
             */
            retrieveFileBasics: function (path) {

                $log.info("get basic info about the file");

                if (!path) {
                    $log.error("path is missing");
                    throw "path is missing";
                }

                return $http({method: 'GET', url: 'file/', params: {path: path}}).success(function (data) {

                    // decorate data with tag string

                    data.tagString = tagService.tagListToTagString(data.irodsTagValues);
                    return data;

                }).error(function () {
                        return null;
                    });
            },

            /**
             * Create a new child folder underneath the given parent collection
             * @param parentPath path of parent
             * @param newChildName name of new folder
             * @returns {*|Error}
             */
            createNewFolder: function (parentPath, newChildName) {

                $log.info("createNewFolder()");

                if (!parentPath) {
                    $log.error("parentPath is missing");
                    throw "parentPath is missing";
                }

                if (!newChildName) {
                    $log.error("newChildName is missing");
                    throw "newChildName is missing";
                }

                var path = parentPath + "/" + newChildName;


                return $http({method: 'PUT', url: 'collection/', params: {path: path}}).success(function (data) {
                    $log.info("successfully added:" + path);

                   // var newFolder = { "collection": true, "createdAt": "", "dataObject": false, "dataSize": 0, "description": "", "displayDataSize": "", "formattedAbsolutePath": path, "nodeLabelDisplayValue": newChildName, "objectType": {"enumType": "org.irods.jargon.core.query.CollectionAndDataObjectListingEntry$ObjectType", "name": "COLLECTION"},
                     //   "parentPath": parentPath, "pathOrName": "/" + newChildName, "specColType": {"enumType": "org.irods.jargon.core.pub.domain.ObjStat$SpecColType", "name": "NORMAL"}};
                    return data;

                }).error(function () {
                        return null;
                    });
            }

        };

        return fileService;

    }]);




/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home', ['httpInterceptorModule', 'angularTranslateApp', 'virtualCollectionsModule', 'MessageCenterModule', 'CollectionsModule', 'ngRoute','globalsModule'])

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
            })

    })

    .controller('homeController', ['$scope', 'virtualCollectionsService', '$translate', '$log', '$http', '$location', 'messageCenterService', 'collectionsService', 'fileService','selectedVc', 'pagingAwareCollectionListing','breadcrumbsService', '$filter',function ($scope, virtualCollectionsService, $translate, $log, $http, $location, $messageCenterService, collectionsService, fileService, selectedVc, pagingAwareCollectionListing,breadcrumbsService, $filter) {

        $scope.selectedVc = selectedVc;
        $scope.pagingAwareCollectionListing = pagingAwareCollectionListing.data;
        $scope.numberSelected = 0;
        $scope.hideDrives = "false";
        $scope.selection = [];
        $scope.newFolderInfo = [];

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
            $location.path("/home/" + vcName + "?path=");

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
            $location.search("path", "");

        };

        /**
         * Upon the selection of an element in a breadrumb link, set that as the location of the browser, triggering
         * a view of that collection
         * @param index
         */
        $scope.goToBreadcrumb = function (index) {

            if (!index) {
                $log.error("cannot go to breadcrumb, no index");
                return;
            }

            $location.path("/home/root");
            $location.search("path", breadcrumbsService.buildPathUpToIndex(index));

        };


        /**
         * View the details for the selected file, by evaluating the selections and finding the selected path
         */
        $scope.viewInfoDetails = function() {

            $log.info("viewInfoDetails...looking for selected path...");

            if ($scope.selection.length == 0) {
                $log.info("nothing to select");
                return;
            }

            // find the selected path and chain the call to show the details

            var path = $scope.selection[0];
            $scope.showFileDetails(path);

        };

        /**
         * Show the file details view
         * @param path
         */
        $scope.showFileDetails = function(path) {
            $location.path("/file");
            $location.search("path", path);
            $location.search("vc", $scope.selectedVc.data.uniqueName);
        };


        /**
         * Cause the collections panel on the left to display
         */
        $scope.showCollections = function () {
            $scope.hideDrives = "false";
        };

        $scope.initiateAddDirectory = function() {
            $log.info("initiateAddDirectory()");
            $scope.newFolderAction=true;
            $scope.newFolderInfo = {};
            //collectionAndDataObjectListingEntries.collectionAndDataObjectListingEntries.unshift("{newFolderAction:true}");

        };

        /**
         * Cancel a new folder action
         */
        $scope.cancelAddDirectory = function() {
            $log.info("cancelAddDirectory()");
            $scope.newFolderAction=false;
            $scope.newFolderInfo = {};

        }

        /**
         * Following an action to initiateAddDirectory(), take the required new folder name and
         * do the actual folder create
         **/
        $scope.addDirectory = function() {
            $log.info("addDirectory()");

            if(!$scope.newFolderInfo.name) {
                // show an error
                var message = $filter('translate')('NO_DIRECTORY_PROVIDED');
                $messageCenterService.add('danger', message);
                return;
            }

            $log.info("subdirectory name is:" + $scope.newFolderInfo.name);


            var data = fileService.createNewFolder($scope.pagingAwareCollectionListing.parentAbsolutePath, $scope.newFolderInfo.name);

            $scope.newFolderAction=false;
            $scope.newFolderInfo = {};

            // do new folder action stuff

            $scope.pagingAwareCollectionListing.collectionAndDataObjectListingEntries.unshift(data);

        };

        /**
         * Get the breadcrumbs from the pagingAwareCollectionListing in the scope.  This updates the path
         * in the global scope breadcrmubsService.  I don't know if that's the best way, but gotta get it somehow.
         * Someday when I'm better at angualar we can do this differently.
         */
        $scope.getBreadcrumbPaths = function () {

            if (!$scope.pagingAwareCollectionListing) {
                return [];
            }

            breadcrumbsService.setCurrentAbsolutePath($scope.pagingAwareCollectionListing.parentAbsolutePath);
            return breadcrumbsService.getWholePathComponents();

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
        $scope.updateSelectedFromCollection = function (absolutePath) {

           $log.info("update selected:" + absolutePath);
           // alert("selected!");
            //var checkbox = action.target;
            //(checkbox.checked ? $scope.numberSelected++ : $scope.numberSelected--);

            var idx = $scope.selection.indexOf(absolutePath);

            // is currently selected
            if (idx > -1) {
                $scope.selection.splice(idx, 1);
                $scope.numberSelected--;
            }

            // is newly selected
            else {
                $scope.selection.push(absolutePath);
                $scope.numberSelected++;
            }

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


angular.module('login', [ 'globalsModule','httpInterceptorModule', 'angularTranslateApp', 'userServiceModule', 'MessageCenterModule'])


    .config(function () {
        /*
         * configuration block
         */

    })

    /*
     * login controller function here
     */

    .controller('loginController', ['$scope', '$translate', '$log', '$http', '$location', 'userService','globals','$q', '$timeout' ,function ($scope, $translate, $log, $http, $location, userService, globals, $q, $timeout) {


        $scope.login = {
            authType: 'STANDARD'
        };


        $scope.changeLanguage = function (langKey) {
            $translate.use(langKey);
        };

        $scope.getLoggedInIdentity = function () {
            return userService.retrieveLoggedInIdentity.success(function (identity) {
                $scope.loggedInIdentity = identity
            });

        };


        $scope.submitLogin = function () {
            var actval = irodsAccount($scope.login.host, $scope.login.port, $scope.login.zone, $scope.login.userName, $scope.login.password, $scope.login.authType, "");
            $log.info("irodsAccount for host:" + actval);
            $http({
                method: 'POST',
                url: 'login',
                data: actval,
                headers: { 'Content-Type': 'application/json' }  // set the headers so angular passing info as request payload
            }).then(function (data) {
                    $log.info("login successful" + data);
                    // userService.setLoggedInIdentity(data);

                    var path = globals.getLastPath();
                    return $q.when(path);

                }).then(function(path) {

                    if (!path) {
                        $log.info("hard code to go home");
                       path="/home/home";
                    } else {
                        // setpath
                        $log.info("setting location to last path:" + path);
                    }

                    $timeout(function () {
                        $location.path(path);
                    });

                    $log.info("end login success processing");

                });
        };

    }]);










/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * File metadata controller function here, representing collection and data object catalog info and operations
 */
angular.module('fileMetadata', ['globalsModule','httpInterceptorModule', 'angularTranslateApp', 'MessageCenterModule', 'ngRoute', 'fileModule',  'tagServiceModule'])

    /*
     * handle config of routes for home functions
     */
    .config(function ($routeProvider) {
        // route for the home page
        $routeProvider.when('/file/metadata', {
            templateUrl: 'assets/file/metadata/file-metadata-master-angularjs.html',
            controller: 'fileMetadataController',
            resolve: {
                // do a listing
                fileData: function ($route, fileService) {
                    var path = $route.current.params.path;
                    if (path == null) {
                        path = "/";
                    }
                    return fileService.retrieveFileBasics(path);
                }
            }
        });
    })

    .controller('fileMetadataController', ['$scope', 'fileService', 'fileMetadataService', '$translate', '$log', '$http', '$location', 'messageCenterService','fileData',function ($scope, fileService, fileMetadataService, $translate, $log, $http, $location, $messageCenterService, fileData) {

        $scope.file = fileData;
        $scope.metadataTab = true;


        $scope.showFileMetadata = function (absPath) {
            alert("show file metadata");
            $log.info("showFileMetadata()");
            if (!absPath) {
                throw "no apsPath provided";
            }

            $log.info("absPath:" + absPath);
            $location.path("/file/metadata");
            $location.search("path", absPath);

        };


    }])
    .factory('fileMetadataService', ['$http', '$log', 'tagService', function ($http, $log, tagService) {

        var fileMetadataService = {


        };

        return fileMetadataService;

    }]);



