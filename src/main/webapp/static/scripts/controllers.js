var footinfo = angular.module('footinfo', ['ngRoute', 'ngAnimate']);

// Config
footinfo.config(['$routeProvider', function ($routeProvider){
    $routeProvider.when("/a",{
        controller: "teamOverviewController",
        templateUrl: "views/teamOverview.html"
    })
    .when("/b", {
        controller: "matchOverviewController",
        templateUrl: "views/matchOverview.html"
    })
    .when("/team/:team_name/:team_id",     {
        controller: "teamController",
        templateUrl: "views/team.html"
    })
    .when("/match/:match_id/:participant_a/:participant_b",     {
        controller: "matchController",
        templateUrl: "views/match.html"
    })
    .when("/", {
        redirectTo: "/b"
    });
}]);

// Factory
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
                    "teamA" : participants.teamA,
                    "teamB" : participants.teamB
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
    factory.getAllTeams = function () {
        return $http({
            method: 'GET',
            url: 'http://localhost:8080/footinfo/v1/api/getAllTeams',            
        });
    };

    factory.finishGame = function (match_id) {
        return $http({
            method: 'POST',
            url: 'http://localhost:8080/footinfo/v1/api/finishGame',
            data: {"match_id" : match_id}
        });
    }
    factory.getTeamData = function (team_id) {
        return $http({
            method: 'POST',
            url: 'http://localhost:8080/footinfo/v1/api/getTeamData',
            data: {'team_id' : team_id}
        });
    };

    factory.getGameData = function (match_id) {
        return $http({
            method: 'POST',
            url: 'http://localhost:8080/footinfo/v1/api/getGameData',
            data: {"match_id" : match_id}
        });
    }
    return factory;
}]);

// CONTROLLERS
footinfo.controller('teamOverviewController', function (apiFactory, $log, $scope){
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

    $scope.getAllTeams = function () {
        apiFactory.getAllTeams().success(function (response){            
            $scope.teams = response.teams[0];
        });
    };

    $scope.getAllTeams();
});

footinfo.controller('matchOverviewController', function (apiFactory, $log, $scope){
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

footinfo.controller('teamController', function (apiFactory, $log, $scope, $routeParams) {
    apiFactory.getTeamData($routeParams.team_id).success(function (response){
        $log.log(response);    
        $scope.players = response.teamData[0];
        setTeamName($routeParams.team_name, $routeParams.team_id);
    });

    function setTeamName(name, id){
        $scope.team_id = id;
        $scope.team_name = name;
    }
});

footinfo.controller('matchController', function (apiFactory, $log, $scope, $routeParams){
    $scope.match_id = $routeParams.match_id;
    $scope.participant_a = $routeParams.participant_a;
    $scope.participant_b = $routeParams.participant_b;

    apiFactory.getGameData($routeParams.match_id).success(function (response){
            $log.log(response);
            $scope.team_a = response.team_a[0];
            $scope.team_b = response.team_b[0];
    });

});