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
            ADD_TO_CART: 'Add to Cart',
            DELETE: 'Delete',
            HOST: 'Host',
            LOGIN_HEADLINE: 'Please login to iDrop',
            MOVE_COPY: 'Move/Copy',
            NEED_HELP: 'Need help?',
            PASSWORD: 'Password',
            PORT: 'Port',
            RENAME: 'Rename',
            SIGN_IN: 'Sign in',
            TOOLS: 'Tools',
            USER_NAME: 'User Name',
            VIEW_DETAILS: 'View Details',
            ZONE: 'Zone'
        });
        $translateProvider.preferredLanguage('en');
    });
