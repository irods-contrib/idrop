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
            CREATED: 'Created',
            DATA_TYPE: 'Data Type',
            DELETE: 'Delete',
            HOST: 'Host',
            LENGTH: 'Length',
            LOGIN_HEADLINE: 'Please login to iDrop',
            METADATA: 'Metadata',
            MODIFIED: 'Modified',
            MOVE_COPY: 'Move/Copy',
            NEED_HELP: 'Need help?',
            PASSWORD: 'Password',
            PORT: 'Port',
            RENAME: 'Rename',
            SIGN_IN: 'Sign in',
            TOOLS: 'Tools',
            TYPE: 'Type',
            USER_NAME: 'User Name',
            VIEW_DETAILS: 'View Details',
            ZONE: 'Zone'
        });
        $translateProvider.preferredLanguage('en');
    });
