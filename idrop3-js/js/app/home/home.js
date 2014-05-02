/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home', ['httpInterceptorModule', 'angularTranslateApp', 'virtualCollectionsModule', 'MessageCenterModule', 'CollectionsModule'])

    .controller('homeController', ['$scope', 'virtualCollectionsService', '$translate', '$log', '$http', '$location', 'messageCenterService', 'collectionsService', function ($scope, virtualCollectionsService, $translate, $log, $http, $location, $messageCenterService, collectionsService) {


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

        $scope.selectVirtualCollection = function (vcName) {
            if (!vcName) {
                $messageCenterService.add('danger', "missing vcName");
                return;
            }
            $log.info("initializing virtual collection for:" + vcName);

            collectionsService.listCollectionContents(vcName, "", 0).then(function (listing) {
                $scope.pagingAwareCollectionListing = listing.data;
            });

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



