/**
 * Global state holders
 * Created by mikeconway on 7/8/14.
 */


angular.module('globalsModule', [])

    .factory('globals', function ($rootScope) {

        var f = {};


        f.lastPath = null;

        f.setLastPath = function (newLastPath) {
            this.lastPath = newLastPath;
        };

        f.getLastPath = function () {
            return this.lastPath;
        };

        return f;

})
;

