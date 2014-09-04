/**
 *
 */

/**
 * temporarily turned off until I can get around missing route providcer error
 */
describe("Tests of the home controller", function () {

    var $http, $httpBackend, $log, $translate, ctrlScope, controller, rootScope, $q, controllerFactory, $routeProvider, breadcrumbsService, $location, messageCenterService;
    beforeEach(module('home'));

    var mockVcService = {
        listVirtualCollections: function () {
        }
    };

    var vcData = {"name": "vc1", description: "desc1", sourcePath: "source/path"};


    /**
     * Mocking userService for controller, see http://stackoverflow.com/questions/15854043/mock-a-service-in-order-to-test-a-controller
     *
     * note: restyled to this: http://codingsmackdown.tv/blog/2012/12/28/mocking-promises-in-unit-tests/
     */


    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_, _$translate_, _$rootScope_, $controller, _$q_,_breadcrumbsService_, _$location_) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = _$rootScope_.$new();
        //  controller = $controller;
        rootScope = _$rootScope_;
        $q = _$q_;
        controllerFactory = $controller;
       // $routeProvider = _$routeProvider_;
        breadcrumbsService = _breadcrumbsService_;
        $location = _$location_;
        mockVcService = {
            listUserVirtualCollections: function () {
                var deferred = $q.defer();
                deferred.resolve(vcData);
                return deferred.promise;
            }
        };

        mockSelectedVc = {};
        mockPagingAwareCollectionListing = {};
        mockMessageCenterService = {
            add: function (type,msg) {
            }
        };

        messageCenterService = mockMessageCenterService;
        controller = $controller('homeController', { $scope:ctrlScope, virtualCollectionsService: mockVcService, selectedVc:mockSelectedVc, pagingAwareCollectionListing:mockPagingAwareCollectionListing, messageCenterService:messageCenterService});

    }));

    it("should set path when going to a breadrumb", function () {
        var path = "/this/is/a/path/to/a/file.txt";
        breadcrumbsService.setCurrentAbsolutePath(path);
        ctrlScope.goToBreadcrumb(3);


    });


    it("home should init virtual colls", function () {
        ctrlScope.listVirtualCollections();
        ctrlScope.$apply();
        expect(ctrlScope.virtualCollections).not.toBe([]);

    });

    it("new folder action without path should have an error", function () {
        ctrlScope.pagingAwareCollectionListing = {};
        ctrlScope.initiateAddDirectory();
        var dir = "";
        spyOn(messageCenterService,'add');
        ctrlScope.addDirectory(dir);
        expect(messageCenterService.add).toHaveBeenCalled();

    });

    it("new folder action and then cancel should set new folder in scope to false", function () {
        ctrlScope.pagingAwareCollectionListing = {};
        ctrlScope.initiateAddDirectory();
        ctrlScope.cancelAddDirectory();
        expect(ctrlScope.newFolderAction).toBe(false);

    });

    it("new folder action should work just fine and call the service to do the add", function()
    {
        ctrlScope.pagingAwareCollectionListing = {parentAbsolutePath:"/test1/home/test1"};
        ctrlScope.initiateAddDirectory();
        var dir = "newDir";
        ctrlScope.addDirectory(dir);

    });

    it("initiating and then completing a new folder action with data should call update service", function () {
        ctrlScope.pagingAwareCollectionListing = {parentAbsolutePath:"/test1/home/test1"};
        ctrlScope.initiateAddDirectory();
        ctrlScope.newFolderInfo.name = "test";
        ctrlScope.addDirectory();
        expect(ctrlScope.newFolderAction).toBe(false);
        expect(ctrlScope.newFolderInfo.name).toBe(undefined);

    });

});