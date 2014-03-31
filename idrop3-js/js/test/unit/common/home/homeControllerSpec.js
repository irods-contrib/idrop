/**
 *
 */


describe("Tests of the home controller", function () {

    var $http, $httpBackend, $log, $translate, ctrlScope, controller, rootScope, $q, controllerFactory;
    beforeEach(module('home'));

    var mockVcService = {
        listVirtualCollections: function () {
        }
    };

    var vcData = {"name": "vc1", description: "desc1", sourcePath: "source/path"};


    /**
     * Mocking userService for controller, see http://stackoverflow.com/questions/15854043/mock-a-service-in-order-to-test-a-controller
     */


    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_, _$translate_, _$rootScope_, $controller, _$q_) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = _$rootScope_.$new();
        //  controller = $controller;
        rootScope = _$rootScope_;
        $q = _$q_;
        controllerFactory = $controller;

    }));


    it("home should init virtual colls", function () {

        mockVcService = {
            listUserVirtualCollections: function () {
            }
        };

        controller = controllerFactory('homeController', {
            $scope: ctrlScope,
            virtualCollectionsService: mockVcService
        });

        var deferred = $q.defer();
        deferred.resolve(vcData);
        spyOn(mockVcService, 'listUserVirtualCollections').andReturn(deferred.promise);

        ctrlScope.listVirtualCollections();

        ctrlScope.$apply();
        expect(mockVcService.listUserVirtualCollections).toHaveBeenCalled();


    });

});