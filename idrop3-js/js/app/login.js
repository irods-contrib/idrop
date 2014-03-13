/**
 * Created by mikeconway on 3/8/14.
 */
/**
 * Main angular module for login/logout views
 *
 * @author Mike Conway (DICE)
 */


angular.module('login')


    .config(function () {
        /*
         * configuration block
         */


    })

    /*
     * login controller function here
     */

    .controller('loginController', function ($scope, $translate) {


        $scope.changeLanguage = function (langKey) {
            $translate.use(langKey);
        };


    });




