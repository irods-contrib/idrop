/**
 * Supporting code for integration with angular-translate
 *
 * see:
 *
 * http://www.ng-newsletter.com/posts/angular-translate.html
 *
 * Created by mikeconway on 3/13/14.
 */

angular.module('angularTranslateApp', ['pascalprecht.translate']).config(function ($translateProvider) {
        $translateProvider.translations('en', {
            HOST: 'Host',
            LOGIN_HEADLINE: 'Please login to iDrop',
            NEED_HELP: 'Need help?',
            PASSWORD: 'Password',
            PORT: 'Port',
            SIGN_IN: 'Sign in',
            USER_NAME: 'User Name',
            ZONE: 'Zone'
        });
        $translateProvider.preferredLanguage('en');
    });
