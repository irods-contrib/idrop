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
            AUDIT: 'Audit',
            CHECKSUM: 'Checksum',
            CREATED: 'Created',
            DATA_OWNER_NAME: 'Owner',
            DATA_OWNER_ZONE: 'Owner Zone',
            DATA_PATH: 'Data Path',
            DATA_TYPE: 'Data Type',
            DELETE: 'Delete',
            EDIT:'Edit',
            EXPIRY: 'Expiry',
            HOME:'Home',
            HOST: 'Host',
            INFO: 'Info',
            LENGTH: 'Length',
            LOGIN_HEADLINE: 'Please login to iDrop',
            LOGOUT: 'Logout',
            METADATA: 'Metadata',
            MODIFIED: 'Modified',
            MOVE_COPY: 'Move/Copy',
            NAME: 'Name',
            NEW_FILE: 'New File',
            NEW_FOLDER: 'New Folder',
            NEW_FOLDER_NAME: 'New Folder Name',
            NEED_HELP: 'Need help?',
            NO_DIRECTORY_PROVIDED: 'No folder name was provided',
            OBJECT_PATH: 'Object Path',
            PROFILE: 'Profile',
            PASSWORD: 'Password',
            PERMISSION: 'Permission',
            PERMISSIONS: 'Permissions',
            PORT: 'Port',
            RENAME: 'Rename',
            RULE: 'Rule',
            SIGN_IN: 'Sign in',
            TAGS: 'Tags',
            TOOLS: 'Tools',
            TYPE: 'Type',
            USER_NAME: 'User Name',
            VIEWS: 'Views',
            VIEW_DETAILS: 'View Details',
            WORKFLOW:'Workflow',
            ZONE: 'Zone'
        });
        $translateProvider.preferredLanguage('en');
    });
