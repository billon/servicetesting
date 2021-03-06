'use strict';

angular.module('service-testing-tool').controller('TestcasesController', ['$scope', 'Testcases', 'Teststeps', 'Testruns', '$stateParams', '$state', 'uiGridConstants', '$modal',
  function($scope, Testcases, Teststeps, Testruns, $stateParams, $state, uiGridConstants, $modal) {
    $scope.schema = {
      type: "object",
      properties: {
        id: { type: "integer" },
        name: { type: "string", maxLength: 200 },
        description: { type: "string" }
      },
      "required": ["name", "description"]
    };

    $scope.form = [
      {
        key: "name",
        title: "Name",
        validationMessage: "The Name is required and should be less than 200 characters"
      },
      {
        key: "description",
        title: "Description",
        type: "textarea"
      }
    ];

    $scope.columnDefs = [
      {
        name: 'name', width: 200, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        },
        cellTemplate: 'testcaseGridNameCellTemplate.html'
      },
      {name: 'description', width: 585, minWidth: 300}
    ];

    $scope.teststepColumnDefs = [
      {
        name: 'sequence', width: 120, maxWidth: 120,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        },
      },
      {
        name: 'name', width: 150, minWidth: 50,
        cellTemplate: 'teststepGridNameCellTemplate.html'
      },
      {name: 'description', width: 400, minWidth: 200},
      {
        name: 'intface.name', displayName: 'Interface', width: 200, minWidth: 100,
        cellTemplate: 'teststepGridIntfaceCellTemplate.html'
      },
      {
        name: 'result', width: 100, minWidth: 100,
        cellTemplate: 'teststepGridResultCellTemplate.html'
      }
    ];

    $scope.testcase = {};

    $scope.alerts = [];

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if (this.testcase.id) {
          var testcase = this.testcase;
          testcase.$update(function() {
            $scope.alerts.push({type: 'success', msg: 'The Test Case has been updated successfully'});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        } else {
          var testcase = new Testcases(this.testcase);
          testcase.$save(function(response) {
            $state.go('testcase_edit', {testcaseId: response.id});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        }
      }
    };

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };

    $scope.remove = function(testcase) {
      var context = {
        message: 'Do you want to delete the test case "' + testcase.name + '" and all its test steps?'
      };

      var modalInstance = $modal.open({
        animation: false,
        templateUrl: '/ui/views/common/messagebox-modal.html',
        controller: 'MessageboxModalController',
        windowClass: 'small-modal',
        resolve: {
          context: function () {
            return context;
          }
        }
      });

      modalInstance.result.then(function (isOK) {
        if (isOK) {
          testcase.$remove(function(response) {
            $state.go('testcase_all');
          });
        }
      });
    };

    $scope.viewEnvironment = function(environmentId) {
      var context = {
        environmentId: environmentId
      };

      var modalInstance = $modal.open({
        animation: true,
        templateUrl: '/ui/views/environments/edit-modal.html',
        controller: 'EnvironmentsModalController',
        windowClass: 'large-modal',
        resolve: {
          context: function () {
            return context;
          }
        }
      });
    };

    $scope.viewIntface = function(intfaceId) {
      var context = {
        intfaceId: intfaceId
      };

      var modalInstance = $modal.open({
        animation: true,
        templateUrl: '/ui/views/intfaces/edit-modal.html',
        controller: 'IntfacesModalController',
        windowClass: 'large-modal',
        resolve: {
          context: function () {
            return context;
          }
        }
      });
    };

    $scope.changeEnvironment = function(testcase) {
      var context = {
        environmentId: testcase.environmentId,
        expect: 'Single'
      };

      var modalInstance = $modal.open({
        animation: true,
        templateUrl: '/ui/views/environments/list-modal.html',
        controller: 'EnvironmentsModalController',
        windowClass: 'large-modal',
        resolve: {
          context: function () {
            return context;
          }
        }
      });

      modalInstance.result.then(function (selectedEnvironment) {
        if (selectedEnvironment) {
          testcase.environmentId = selectedEnvironment.id;
          testcase.environment = selectedEnvironment;
        }
      });
    };
    
    $scope.run = function() {
      if (! $scope.testcase.environmentId) {
        $scope.alerts.push({type: 'warning', msg: 'Please select an environment to run'});
        return;
      }

      var teststeps = $scope.testcase.teststeps;
      for (var index in teststeps) {
        if (! teststeps[index].intfaceId) {
          $scope.alerts.push({type: 'warning', msg: 'Please assign an Interface for all the Test Steps'});
          return;
        }
      }

      var testrun = new Testruns({
        testcase: $scope.testcase,
        environmentId: $scope.testcase.environmentId
      });
      testrun.$save(function(response) {
        $scope.testcase = response.testcase;
      },function(error) {
        $scope.alerts.push({type: 'warning', msg: error.data});
      });
    };

    $scope.find = function() {
      Testcases.query(function(testcases) {
        $scope.testcases = testcases;
      });
    };

    $scope.findOne = function() {
      if ($stateParams.testcaseId) {
        Testcases.get({
          testcaseId: $stateParams.testcaseId
        }, function(testcase) {
          $scope.testcase = testcase;
        });
      }
    };

    $scope.createTeststep = function() {
      // calculate the max sequence number
      var sequences = _.pluck($scope.testcase.teststeps, 'sequence');

      var maxSequence = 0;
      if (sequences.length > 0) {
        maxSequence = _.max(sequences);
      }
      var newSequence = Math.floor(maxSequence / 10) * 10 + 10;
      $state.go('teststep_create', {testcaseId: $scope.testcase.id, sequence: newSequence});
    };
  }
]);
