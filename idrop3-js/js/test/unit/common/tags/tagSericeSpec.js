/**
 *
 */


describe("tests of tag service", function () {

    var tagService, $http, $httpBackend, $log;
    beforeEach(module('tagServiceModule'));
    beforeEach(inject(function (_tagService_, _$http_, _$httpBackend_, _$log_) {
        tagService = _tagService_;
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
    }));

    it("convert list of tags to tag string", function () {
        var actual;

        var tags = ["tag1", "tag2"];
        var actual = tagService.tagListToTagString(tags);


        expect(actual).toEqual("tag1 tag2 ");
    });

});