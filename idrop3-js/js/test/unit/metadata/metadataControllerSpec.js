/**
 *
 */

describe("Tests of the metadata controller", function () {

    var $http, $httpBackend, $log, $translate, ctrlScope, controller, rootScope, $q, controllerFactory, $route, $location;
    beforeEach(module('fileMetadata'));


    /**
     * Mocking userService for controller, see http://stackoverflow.com/questions/15854043/mock-a-service-in-order-to-test-a-controller
     *
     * note: restyled to this: http://codingsmackdown.tv/blog/2012/12/28/mocking-promises-in-unit-tests/
     */


    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_, _$translate_, _$rootScope_, $controller, _$q_, _$route_, _$location_) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = _$rootScope_.$new();
        rootScope = _$rootScope_;
        $q = _$q_;
        controllerFactory = $controller;
        $route = _$route_;
        $location = _$location_;
        mockMetadataService = {
            /* listUserVirtualCollections: function () {
             var deferred = $q.defer();
             deferred.resolve(vcData);
             return deferred.promise;
             }*/
        };

        mockFileService = {

        };

        mockTagService = {

        };

        mockFileData = {};

        controller = $controller('fileMetadataController', { $scope: ctrlScope, metadataService: mockMetadataService, fileService:mockFileService, tagService:mockTagService, fileData:mockFileData});

    }));


    /*
    for inspiration, see http://stackoverflow.com/questions/17963717/can-we-write-unit-test-for-angularjs-routeprovider
    Need a better way to test route behavior, prob as midway or e2e type test
    see dev notes for some links
     */

    it("fileMetadataController should init dataProfile", function () {

        $httpBackend.whenGET('file/?path=%2F').respond(mockFileData);  //TODO: this looks funny..mc
        $httpBackend.whenGET('assets/file/metadata/file-metadata-master-angularjs.html').respond("");

    //    inject(function ($route, $location, $rootScope) {
            expect($route.current).toBeUndefined();
            $location.path('/file/metadata');
            rootScope.$digest();

            //expect($route.current.loadedTemplateUrl).toBe('app/users/user-list.tpl.html');
            expect($route.current.controller).toBe('fileMetadataController');
        });

  //  });

});