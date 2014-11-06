/**
 * Test for file controller and services
 * Created by Mike Conway on 11/3/14.
 */

describe("Tests of the file controller", function () {

    var $http, $httpBackend, $log, $translate, ctrlScope, controller, messageCenterService,  starService, file, fileService, vc;
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
            },
            removeStar: function (path) {
                return {};
            }
        };

        fileService = {

        };

        file = {};
        vc = {};

        controller = $controller('fileController', { $scope:ctrlScope, messageCenterService:messageCenterService, starService:starService, fileService:fileService, file:file, vc:vc});

    }));

    it("should toggle file as starred and set in scope to true", function () {
        var path = "/this/is/a/path/to/a/file.txt";
        ctrlScope.file = {starred:false, domainObject:{absolutePath:path}};
        spyOn(starService,'addStar');
        ctrlScope.toggleStar();
        expect(starService.addStar).toHaveBeenCalled();
        expect(ctrlScope.file.starred).toBe(true);

    });

    it("should toggle file as unstarred and set in scope to false", function () {
        var path = "/this/is/a/path/to/a/file.txt";
        ctrlScope.file = {starred:true, domainObject:{absolutePath:path}};
        spyOn(starService,'removeStar');
        ctrlScope.toggleStar();
        expect(starService.removeStar).toHaveBeenCalled();
        expect(ctrlScope.file.starred).toBe(false);

    });



});
