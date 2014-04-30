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
            virtualCollection:"",
            parentPath:"",

            listCollectionContents: function (reqVcName, reqParentPath, reqOffset) {
                $log.info("doing get of the contents of a virtual collection");

                if (!reqVcName) {
                    $log.error("recVcName is missing");
                    throw "reqMcName is missing";

                }

                if (!reqParentPath) {
                    $log.error("reqParentPath is missing");
                    throw "reqParentPath is missing";

                }

                if (!reqOffset) {
                    $log.error("reqOffset is missing");
                    throw "reqOffset is missing";

                }

                return $http({method: 'GET', url: 'collection',  params: {virtualCollection: reqVcName, path:reqParentPath, offset:reqOffset }}).success(function (data) {
                    pagingAwareCollectionListing = data;
                    virtualCollection = reqVcName;
                    parentPath = reqParentPath;
                }).error(function () {
                        virtualCollections = [];
                    });

            }

        };

        return collectionsService;

    }]);

