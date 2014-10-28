/**
 *
 */


describe("Tests of the virtual collections icon filterr", function () {

    var $filter;

    beforeEach(function () {
        module('virtualCollectionFilter');

        inject(function (_$filter_) {
            $filter = _$filter_;
        });
    });


    it("should give default for coll based collection", function () {
      var orig, result;
        result = $filter('vcIcon')("virtual.collection.default.icon");
        expect(result).toEqual("glyphicon-folder-close");

    });

    it("should return a star class when it is starred", function () {
        var orig, result;

        orig = {data:{starred:true}};

        result = $filter('starIcon')(orig);
        expect(result).toEqual("glyphicon-star");

    });

    it("should return an empty star class when it is not starred", function () {
        var orig, result;

        orig = {data:{starred:false}};

        result = $filter('starIcon')(orig);
        expect(result).toEqual("glyphicon-star-empty");

    });

});