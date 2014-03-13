/**
 * Supporting code for integration with angular-translate
 *
 * see:
 *
 * http://www.ng-newsletter.com/posts/angular-translate.html
 *
 * Created by mikeconway on 3/13/14.
 */

angular.module('angularTranslateApp', ['pascalprecht.translate'])
    .config(function($translateProvider) {
        $translateProvider.translations('en', {
            LOGIN_HEADLINE: 'Please login to iDrop'
        });
        $translateProvider.preferredLanguage('en');
 });
