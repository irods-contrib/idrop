/**
 *
 */


describe("Tests of the breadcrumbService", function () {

    var  $log,  breadcrumbsService, rootScope;
    beforeEach(module('globalsModule'));
    beforeEach(inject(function (_$log_ , _$rootScope_, _breadcrumbsService_) {
        $log = _$log_;
        breadcrumbsService = _breadcrumbsService_;
        rootScope = _$rootScope_;
    }));


    it(" should build a path based on an index", function () {
        var path = "/this/is/a/path/to/a/file.txt";
        breadcrumbsService.setCurrentAbsolutePath(path);
        var pathTo3 = breadcrumbsService.buildPathUpToIndex(3);
        expect(pathTo3).toEqual("/this/is/a/path");
    });

    it("should parse a path into parts", function () {
        var path = "/this/is/a/path/to/a/file.txt";
        breadcrumbsService.setCurrentAbsolutePath(path);
        var paths = ["this","is","a","path","to","a","file.txt"];
        expect(breadcrumbsService.getWholePathComponents()).toEqual(paths);
    });


});