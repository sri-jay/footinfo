var footinfo = angular.module('footinfo', ['ngRoute', 'ngAnimate']);

footinfo.config(['$routeProvider', function ($routeProvider){
    $routeProvider.when("/a",{
        controller: "teamController",
        templateUrl: "views/team.html"
    })
    .when("/b", {
        controller: "matchController",
        templateUrl: "views/match.html"
    })
    .when("/", {
        redirectTo: "/b"
    });
}]);

footinfo.factory("apiFactory",['$http', function($http){
    var factory = {};
    factory.addTeam =  function (teamData) {
        return $http({
            method: 'POST',
            url: "http://localhost:8080/footinfo/v1/api/addTeam",
            headers: {"Content-Type" : "application/json"},
            data: {                
                "team_name": teamData.teamName,
                "players": teamData.players
            }
        });
    };
    factory.createGame = function (participants) {
            return $http({
                method: 'POST',
                url:'http://localhost:8080/footinfo/v1/api/createGame',
                data: {
                    "TEAM_A" : participants.teamA,
                    "TEAM_B" : participants.teamB
                }
            });
    };
    factory.getGameStatuses = function () {
        return $http({
            method: 'GET',
            url: 'http://localhost:8080/footinfo/v1/api/getGameStatus',
            headers: {'Content-Type' :'application/json'}
        });
    }
    factory.getAllPlayers = function () {
        return $http({
            method: 'GET',
            url: 'http://localhost:8080/footinfo/v1/api/getAllPlayers',            
        });
    };

    factory.finishGame = function (match_id) {
        return $http({
            method: 'POST',
            url: 'http://localhost:8080/footinfo/v1/api/finishGame',
            data: {"match_id" : match_id}
        });
    }
    return factory;
}]);

footinfo.controller('teamController', function (apiFactory, $log, $scope){
    $scope.announcement = "Team Controls";
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
        $scope.getAllPlayers();
    };

    $scope.getAllPlayers = function () {
        apiFactory.getAllPlayers().success(function (response){
            $log.log(response.players[0]);
            $scope.allPlayers = response.players[0];
        });
    };

    $scope.getAllPlayers();
});

footinfo.controller('matchController', function (apiFactory, $log, $scope){
    $scope.announcement = "Match Controls";
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
        $scope.getGameStatuses();
    }

    $scope.getGameStatuses = function () {
        apiFactory.getGameStatuses().success(function (response){
            $scope.gameStatus = response.game_statuses[0];
            $log.log(response.game_statuses[0]);
        });
    }

    $scope.finishMatch = function ($event, match_id) {
        function setStyles() {
            $event.target.classList.add("green");
            $event.target.classList.add("disabled");
            $event.target.classList.remove("red");
            $event.target.classList.remove("inverted");
            $event.target.classList.remove("animated");
            $event.target.innerHTML = "Finished";
            $event.target.parentNode.parentNode.getElementsByClassName("match-status")[0].innerHTML = "finished";
        }
        apiFactory.finishGame(match_id).success(function (response){
            if(response.status == "succeeded") {
                setStyles();
            }
        });
    };

    $scope.getGameStatuses();
});