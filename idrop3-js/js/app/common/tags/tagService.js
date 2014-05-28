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
                  tagString = tagString + tagList[tag] + " ";
              }

              return tagString;


            };



    }]);

