var jenkinsApp = angular.module('jenkinsApp', []);

jenkinsApp.controller('JenkinsListCtrl', ['$scope', '$http', function ($scope, $http) {
  $scope.job_vms = [];
  $scope.vm_jobs = [];
  $scope.unused_vms = [];

  $http.get('rest/jenkins/job_vms_map').success(function(data) {
    angular.forEach(data, function(value, key) {
        this.push({job: key, vms: value})
    }, $scope.job_vms);
  });

  $http.get('rest/jenkins/vm_jobs_map').success(function(data) {
    angular.forEach(data, function(value, key) {
        this.push({vm: key, jobs: value})
    }, $scope.vm_jobs);
  });

  $http.get('rest/jenkins/unused_vms').success(function(data) {
    $scope.unused_vms = data;
  });
}]);
