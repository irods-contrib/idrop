/**
 *
 */

/**
 * temporarily turned off until I can get around missing route providcer error
 */
describe("Tests of the metadata controller", function () {

    var $http, $httpBackend, $log, $translate, ctrlScope, controller, rootScope, $q, controllerFactory, $routeProvider;
    beforeEach(module('fileMetadata'));


    /**
     * Mocking userService for controller, see http://stackoverflow.com/questions/15854043/mock-a-service-in-order-to-test-a-controller
     *
     * note: restyled to this: http://codingsmackdown.tv/blog/2012/12/28/mocking-promises-in-unit-tests/
     */


    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_, _$translate_, _$rootScope_, $controller, _$q_,_$routeProvider_) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = _$rootScope_.$new();
        rootScope = _$rootScope_;
        $q = _$q_;
        controllerFactory = $controller;
        $routeProvider = _$routeProvider_;
        mockMetadataService = {
           /* listUserVirtualCollections: function () {
                var deferred = $q.defer();
                deferred.resolve(vcData);
                return deferred.promise;
            }*/
        };

        controller = $controller('metadataController', { $scope:ctrlScope, metadataService: mockMetadataService });

    }));


    it("metadataController should init dataProfile", function () {
        expect($route.current).toBeUndefined();
        $location.path('/file/metadata');
        $rootScope.$digest();

        //expect($route.current.loadedTemplateUrl).toBe('app/users/user-list.tpl.html');
        expect($route.current.controller).toBe('fileMetadataController');

    });

});