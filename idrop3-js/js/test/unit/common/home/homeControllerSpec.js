/**
 *
 */


describe("Tests of the home controller", function () {

    var $http, $httpBackend, $log, $translate, ctrlScope, controller, rootScope, _$q_;
    beforeEach(module('home'));

    /**
     * Mocking userService for controller, see http://stackoverflow.com/questions/15854043/mock-a-service-in-order-to-test-a-controller
     */


    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_, _$translate_, _$rootScope_, $controller, _$q_) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = _$rootScope_.$new();
        controller = $controller;
        rootScope = _$rootScope_;
        $q = _$q_;

        @deferredGetBy = $q.defer()
        @fakeService
        {
            listVirtualCollections : function () {
                @deferredGetBy.promise
            }
            ;
        }
        spyOn(@fakeService, 'listVirtualCollections').andCallThrough()


    }));


    it("home should init virtual colls", function () {

       controller('homeController',
        $scope: ctrlScope,
        virtualCollectionsService: @fakeService
        )


        var vcData = {"name": "vc1", description: "desc1", sourcePath: "source/path"};

        ctrlScope.$apply() {}
        @deferredGetBy.resolve(fakeBookDetail)

        expect(@scope.bookDetails).toEqual(fakeBookDetails)

    });

});