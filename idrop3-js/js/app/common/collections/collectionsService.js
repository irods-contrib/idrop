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
                alert(vcName);
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

                return $http({method: 'GET', url: 'collection/' + reqVcName, params: {path: reqParentPath, offset: reqOffset }}).success(function (data) {
                    pagingAwareCollectionListing = data;

                }).error(function () {
                        pagingAwareCollectionListing = {};

                    });

            }
        };

        return collectionsService;

    }]);

