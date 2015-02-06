var feedApp = angular.module('feedApp',['ngRoute']);


feedApp.factory('feedFactory', ['$http', function ($http){
	var factory = {};
	
	factory.getMatches = function () {
		return $http({
			method: 'GET',
			url: 'http://localhost:8080/footinfo/v1/api/getGameStatus'			
		});
	};

	factory.getMatchData = function (matchId) {
		return $http({
			method: 'POST',
			url: 'http://localhost:8080/footinfo/v1/api/getMatchFeed',
			data: {
				"match_id" : matchId
			}
		});
	};

	factory.addSubscriber = function (emailId) {
		return $http({
			method: 'POST',
			url: 'http://localhost:8080/footinfo/v1/api/addSubscriber',
			data: {
				"email_id": emailId
			}
		});
	};

	return factory;
}]);

feedApp.controller('feedController', function(feedFactory, $scope, $log, $interval){
	
	// Holds the match feeds
	$scope.matchFeeds = [];

	function getOngoingMatches() {
		feedFactory.getMatches().success(function (response){
			$log.log(response);
		});
	}

	function getFeed(matchId) {		
		var tempFeed = [];
		feedFactory.getFeed().success(function(response) {
			for(var i=0;i<response.length;i++) {
				tempFeed.push(response[i]);
				$log.log(response[i]);				
			}
			$scope.feed = tempFeed;
		});				
	}

	getFeed();		


	$scope.subscribe = function () {
		feedFactory.addSubscriber($scope.emailId).success(function (response){
			if(response.status == "ok") {
				$("#userSubscription").modal('toggle');
			}
			else
				$("#userSubscription").modal('toggle');
		});
	};

	$scope.getClass = function (eventId) {
		switch (eventId) {
			case "player_goal" : return "green child";	
			case "match_foul" : return "orange crosshairs";
			case "player_card" : return "orange warning sign";
			case "red_player_card" : return "red ban";
			case "yellow_player_card" : return "warning yellow";
			case "match_start" : return "teal ticket";
			case "match_finish" : return "trophy purple";
		}
		
	};	
});	
