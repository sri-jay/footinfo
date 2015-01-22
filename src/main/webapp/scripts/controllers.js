var footinfo = angular.module('footinfo', ['ngRoute', 'ngAnimate']);

footinfo.factory("apiFactory",['$http', function($http){
    var factory = {};
    factory.addTeam =  function (teamData) {
        return $http({
            method: 'POST',
            url: "http://localhost:8080/jersey/v1/api/addTeam",
            headers: {"Content-Type" : "application/json"},
            data: {
                "ACTION" : "ADD_TEAM",
                "TEAM_NAME": teamData.teamName,
                "PLAYERS": teamData.players
            }
        });
    };
    factory.createGame = function (participants) {
            return $http({
                method: 'POST',
                url:'http://localhost:8080/jersey/v1/api/createGame',
                data: {
                    "TEAM_A" : participants.teamA,
                    "TEAM_B" : participants.teamB
                }
            });
    };

    return factory;
}]);

footinfo.controller('adminController', function (apiFactory, $log, $scope){
    $scope.addTeam = function () {
        var teamData = {
            "teamName" : $scope.teamName,
            "players" : $scope.players
        };
        apiFactory.addTeam(teamData).success(function (response){
            $log.log(response);
            if(response.STATUS == "SUCCEEDED"){
                swal({
                    type:"success",
                    title: "Added Sucessfully!",
                    text: "Team Added Sucessfully"
                });
            }
            else {
                swal({
                    type: "error",
                    title: "Oops..!",
                    text: "Something went wrong."
                });
            }
        });
    }

    $scope.createGame = function () {
        var participants = {
            "teamA" : $scope.teamA,
            "teamB" : $scope.teamB
        };

        apiFactory.createGame(participants).success(function (response){
          if(response.STATUS == "SUCCEEDED"){
                swal({
                    type:"success",
                    title: "Done!",
                    text: "Match Created Successfully!"
                });
            }
            else {
                swal({
                    type: "error",
                    title: "Oops..!",
                    text: "Something went wrong."
                });
            }
        });
    }
});