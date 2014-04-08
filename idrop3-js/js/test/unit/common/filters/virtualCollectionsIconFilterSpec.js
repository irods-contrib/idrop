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

});