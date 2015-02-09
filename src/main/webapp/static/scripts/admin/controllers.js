var footinfo = angular.module('footinfo', ['ngRoute', 'ngAnimate', 'n3-line-chart']);

// ANGULAR CONFIG
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
    .when("/player/:player_id/:player_team/:player_name", {
        controller: "playerStatsController",
        templateUrl: "views/player.html"
    })
    .when("/", {
        redirectTo: "/b"
    });
}]);

// FACTORY
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

    factory.playerFoul = function (commitedBy, foulOn, gameId) {
        return $http({
            method: 'POST',
            url: 'http://localhost:8080/footinfo/v1/api/playerFoul',
            data: {
                'committed_by' : commitedBy,
                'foul_on' : foulOn,
                'match_id': gameId
            }
        });
    };

    factory.playerCard = function (awardedTo, cardType, matchId) {
        return $http({
            method: 'POST',
            url: 'http://localhost:8080/footinfo/v1/api/playerCard',
            data: {
                'awarded_to' : awardedTo,
                'card_type' : cardType,
                'match_id' : matchId
            }
        });
    };

    factory.matchGoal = function (scoredBy, goalType, matchId, teamId) {
        return $http({
            method: 'POST',
            url: 'http://localhost:8080/footinfo/v1/api/matchGoal',
            data: {
                'scored_by' : scoredBy,
                'goal_type': goalType,
                'match_id' : matchId,
                'team_id' : teamId
            }
        });
    };

    factory.getPlayerStats = function (playerId) {
        return $http({
            method: 'POST',
            url: 'http://localhost:8080/footinfo/v1/api/getPlayerStats',
            data : {
                'player_id': playerId
            }
        });
    };
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
        $scope.getAllTeams();
    };

    $scope.getAllTeams = function () {
        apiFactory.getAllTeams().success(function (response){            
            $scope.teams = response.teams[0];
        });
    };

    // Populate table on load of view.
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
            $scope.team_a_id = response.team_a_id;
            $scope.team_b_id = response.team_b_id;
    });

    $scope.playerFoul = function () {   

        // Angular will not bind to hiddden elements.     
        var commitedBy = document.getElementById("commitedBy").value;
        var foulOn = document.getElementById("foulOn").value;
        
        apiFactory.playerFoul(commitedBy, foulOn, $scope.match_id).success(function (response) {
            $log.log(response);
            if(response["status"] == "succeeded") {
                swal({
                    type: 'success',
                    title: 'Done!',
                    text: 'Foul Recorded.'
                }); 
            }
        });
    };

    $scope.playerCard = function () {   
        // Angular will not bind to hidden elements
        var awardedTo = document.getElementById("awardedTo").value;
        var cardType = document.getElementById("playerCardType").value;

        apiFactory.playerCard(awardedTo, cardType, $scope.match_id).success(function (response){
            if(response["status"] == "succeeded") {
                swal({
                    type: 'success',
                    title: 'Carded!',
                    text: 'Player has been carded!'
                });
            }
        });
    };

    $scope.matchGoal = function () {
        var data = document.getElementById("scoredBy").value.split(",");
     
        var goalType = document.getElementById("goalType").value;

        apiFactory.matchGoal(data[0], goalType, $scope.match_id, data[1]).success(function (response){
            if(response["status"] == "succeeded") {
                swal({
                    type: 'success',
                    title: 'Goal!',
                    text: 'Goal has been recorded.'
                });
            }
        });
    };

    $scope.finishMatch = function () {
        swal({
                type: "warning",
                title: "End Game?",
                text: "Are You sure?",
                showCancelButton: true,
                confirmButtonText: "End Game.",
                closeOnConfirm: false,
            },
            function() {
                apiFactory.finishGame($scope.match_id).success(function (response){
                  if(response.status == "succeeded") {
                        swal({
                            type: 'success',
                            title: 'Game Ended!',
                            text: 'Game has finished!'
                        });
                    }
                });
            }
        );
    };
});

footinfo.controller('playerStatsController', function (apiFactory, $log, $scope, $routeParams){
    $scope.playerName = $routeParams.player_name;
    $scope.playerTeam = $routeParams.player_team;

    apiFactory.getPlayerStats($routeParams.player_id).success(function (response){

        var graphData = [];
        var matchLabels = [];        
        $log.log(response);

        $scope.totalGoals = 0;
        $scope.totalRedCards = 0;
        $scope.totalYellowCards = 0;
        $scope.totalFouls = 0

        for(var counter=0;counter<response.length;counter++){
            for(var game in response[counter]){
                var datum = {};
                
                matchLabels.push(game);
                datum.x = counter;                
                datum.goals = response[counter][game][0].goals;
                datum.fouls = response[counter][game][1].fouls;
                datum.yellow_cards = response[counter][game][2].yellow_cards;
                datum.red_cards = response[counter][game][3].red_cards;            
                
                $scope.totalGoals += parseInt(datum.goals);
                $scope.totalRedCards += parseInt(datum.red_cards);
                $scope.totalYellowCards += parseInt(datum.yellow_cards);
                $scope.totalFouls += parseInt(datum.fouls);

                graphData.push(datum);
            }
        }
        
        // Assign data to scope for n3.
        $scope.data = graphData;

        // assign options to scope for n3.
        $scope.options = {
            stacks: [{axis: "y", series: ["goals", "fouls", "yellow_cards", "red_cards"]}],
            lineMode: "cardinal",
            series: [
                {
                    id: "goals",
                    y: "goals",
                    label: "Goals",
                    type: "line",
                    color: "green",
                    axis: "y",
                    thickness: "2px"
                },
                {
                    id: "fouls",
                    y: "fouls",
                    label: "Fouls",
                    type: "line",
                    color: "orange",
                    axis: "y",
                    thickness: "2px"
                },
                {
                    id: "yellow_cards",
                    y: "yellow_cards",
                    label: "Yellow Cards",
                    type: "line",
                    color: "yellow",
                    axis: "y",
                    thickness: "2px"
                },
                {
                    id: "red_cards",
                    y: "red_cards",
                    label: "Red Cards",
                    type: "line",
                    color: "red",
                    axis: "y",
                    thickness: "2px"
                }
            ],
            axes: {
                x: {
                    type: "linear",
                    key: "x",
                    labelFunction: function (label){
                        return matchLabels[label];
                    }
                 },
                y: {
                    type: "linear",
                    key: "y"
                }
            },
            tension: 0.7,
            lineMode: "linear",
            tooltip: {
                mode: "scrubber",
                formatter: function (x,y, series) {
                    return series.label + " : "+y
                }
            },
            drawLegend: true,
            drawDots: true,
            columnsHGap: 5
        };


    });    
});