var feedApp = angular.module('feedApp',['ngRoute']);


feedApp.factory('feedFactory', ['$http', function ($http){
	var factory = {};
	
	factory.getMatches = function () {
		return $http({
			method: 'GET',
			url: 'http://localhost:8080/footinfo/v1/api/getGameStatus'			
		});
	};

	factory.getMatchFeed = function (matchId) {
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

feedApp.controller('feedController', function(feedFactory, $rootScope, $scope, $log, $interval, $timeout){
	
	$scope.matchFeeds = [];
	function getMatches() {
		feedFactory.getMatches().success(function (response){	
			$log.log(response);		
			$scope.matchCount = response.game_statuses[0].length;
			updateMatchFeeds(response.game_statuses[0]);		
		});
	};


	function updateMatchFeeds(matches) {
		$scope.matchFeeds = [];
		for(var i=0;i<matches.length;i++){
			feedFactory.getMatchFeed(matches[i].match_id).success(function (response){
				$scope.matchFeeds.push(response);
			});
		}
	}

	getMatches();

	$interval(getMatches, 30000);

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

	$scope.getColumns = function (count) {
		$log.log(count);
		return [ 
			'one',
			'two',
			'three',
			'four', 
			'five',
			'six',
			'seven', 
			'eight', 
			'nine', 
			'ten', 
			'eleven', 
			'twelve', 
			'thirteen',
			'fourteen',
			'fifteen', 
			'sixteen', 
			'seventeen',
			'eighteen',
			'nineteen'
		][count-1];

	};
});	
