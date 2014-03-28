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

