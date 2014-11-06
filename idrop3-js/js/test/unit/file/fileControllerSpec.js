/**
 * Test for file controller and services
 * Created by Mike Conway on 11/3/14.
 */

describe("Tests of the file controller", function () {

    var $http, $httpBackend, $log, $translate, ctrlScope, controller, messageCenterService,  starService, file, fileService;
    beforeEach(module('fileModule'));


    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_, _$translate_, _$rootScope_, $controller) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = _$rootScope_.$new();
        starService = {
            addStar: function (path) {
                return {};
            }
        };

        fileService = {

        };

        file = {};

        controller = $controller('fileController', { $scope:ctrlScope, messageCenterService:messageCenterService, starService:starService, fileService:fileService, file:file});

    }));

    it("should toggle file as starred and set in scope to true", function () {
        var path = "/this/is/a/path/to/a/file.txt";
        ctrlScope.file = {starred:false, domainObject:{absolutePath:path}};
        ctrlScope.toggleStar();
        expect(starService.addStar).toHaveBeenCalled();
        expect(ctrlScope.file.starred).toBe(true);

    });




});
