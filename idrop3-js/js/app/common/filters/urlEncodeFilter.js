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
