var footinfo = angular.module('footinfo', ['ngRoute', 'ngAnimate']);

footinfo.config(['$httpProvider', function ($httpProvider){
    $httpProvider.defaults.useXDomain = true;
    $httpProvider.defaults.headers.common = {};
    $httpProvider.defaults.headers.post = {};
    $httpProvider.defaults.headers.put = {};
    $httpProvider.defaults.headers.patch = {};
}]);
footinfo.factory("apiFactory",['$http', function($http){
    var factory = {};
    factory.postToApi =  function () {
        return $http({
            method: 'POST',
            url: "http://localhost:8080/jersey/v1/api/addTeam",
            headers: {"Content-Type" : "Application/JSON"},
        });
    };
    return factory;
}]);

footinfo.controller('sampleController', function (apiFactory, $log, $scope){
    function init() {
        apiFactory.postToApi().success(function (response){
            $scope.data = response;
            console.log(response);
        });
    }
    init();
});