/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home', ['httpInterceptorModule', 'angularTranslateApp', 'virtualCollectionsModule', 'MessageCenterModule', 'CollectionsModule','ngRoute'])

    .config(function ($routeProvider) {
        // route for the home page
        $routeProvider.when('/home/:vcName', {
            templateUrl: 'assets/home/home-angularjs.html',
            controller: 'homeController',
            resolve:{
                // set vc name as selected
                selectedVcName: function($route) {
                    var vcName = $route.current.params.vcName;
                    return vcName;
                },
                // do a listing
                pagingAwareCollectionListing : function($route, collectionsService) {
                    var vcName =  $route.current.params.vcName;
                    return collectionsService.listCollectionContents(vcName, "", 0);
                }

            }
        })
            .when('/home', {
            templateUrl: 'assets/home/home-angularjs.html',
            controller: 'homeController',
            resolve:{
                // set vc name as selected
                selectedVcName: function($route) {

                    return "";
                },
                // do a listing
                pagingAwareCollectionListing : function($route, collectionsService) {
                  return {};
                }

            }
        })
            .otherwise({redirectTo: "/home"});
    })

    .controller('homeController', ['$scope', 'virtualCollectionsService', '$translate', '$log', '$http', '$location', 'messageCenterService', 'collectionsService', 'selectedVcName', 'pagingAwareCollectionListing',
        function ($scope, virtualCollectionsService, $translate, $log, $http, $location, $messageCenterService, collectionsService, selectedVcName, pagingAwareCollectionListing) {

        $scope.selectedVcName = selectedVcName;
        $scope.pagingAwareCollectionListing = pagingAwareCollectionListing.data;

        $scope.listVirtualCollections = function () {

            $log.info("getting virtual colls");
            virtualCollectionsService.listUserVirtualCollections().then(function (virColls) {
                $scope.virtualCollections = virColls.data;
            });
        };

        $scope.hideDrives = "false";

        /*
         Init the virtual collections
         */

        $scope.listVirtualCollections();


            /*
             * Handle the selection of a virtual collection from the virtual collection list, by causing a route change
             * @param vcName
             */
        $scope.selectVirtualCollection = function (vcName) {
            if (!vcName) {
                $messageCenterService.add('danger', "missing vcName");
                return;
            }
            $log.info("initializing virtual collection for:" + vcName);

            $location.path("/home/" + vcName);

        }


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



