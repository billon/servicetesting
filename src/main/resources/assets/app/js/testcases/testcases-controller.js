'use strict';

angular.module('service-testing-tool').controller('TestcasesController', ['$scope', 'Testcases', 'Teststeps', 'Testruns', '$stateParams', '$state', 'uiGridConstants', '$timeout', '$location', 'PageNavigation',
  function($scope, Testcases, Teststeps, Testruns, $stateParams, $state, uiGridConstants, $timeout, $location, PageNavigation) {
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
        name: 'name', width: 200, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        },
        cellTemplate: 'teststepGridNameCellTemplate.html'
      },
      {name: 'description', width: 485, minWidth: 300},
      {
        name: 'intface.name', displayName: 'Interface', width: 200, minWidth: 100,
        cellTemplate: 'teststepGridIntfaceCellTemplate.html'
      },
      {
        name: 'delete', width: 100, minWidth: 80, enableSorting: false, enableFiltering: false,
        cellTemplate: 'teststepGridDeleteCellTemplate.html'
      },
      {
        name: 'result.error', displayName: 'Result', width: 100, minWidth: 80,
        cellTemplate: 'teststepGridResultCellTemplate.html'
      }
    ];

    $scope.testcase = {};

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.testcase.$update(function(response) {
          $scope.saveSuccessful = true;
          $scope.testcase = response;
        }, function(error) {
          $scope.savingErrorMessage = error.data.message;
          $scope.saveSuccessful = false;
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.create = function(isValid) {
      if (isValid) {
        var testcase = new Testcases({
          name: this.name,
          description: this.description
        });
        testcase.$save(function(response) {
          $state.go('testcase_edit', {testcaseId: response.id});
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.remove = function(testcase) {
      var testcaseService = new Testcases(testcase);
      testcaseService.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      });
    };

    $scope.goto = function(state, params, expect) {
      var context = {
        model: $scope.testcase,
        url: $location.path(),
        expect: expect
      };

      PageNavigation.contexts.push(context);

      $state.go(state, params);
    };

    $scope.run = function() {
      var testrun = new Testruns({
        testcaseId: $scope.testcase.id,
        environmentId: $scope.testcase.environmentId
      });
      testrun.$save(function(response) {
        $scope.testcase = response.testcase;
      },function(error) {
        alert('Error');
      });
    };

    $scope.removeTeststep = function(teststep) {
      var teststepService = new Teststeps(teststep);
      teststepService.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      }, function(error) {
        alert('Error');
      });
    };

    $scope.find = function() {
      Testcases.query(function(testcases) {
        $scope.testcases = testcases;
      });
    };

    $scope.findOne = function() {
      var model = PageNavigation.returns.pop();
      if (model) {
        $scope.testcase = model;
      } else {
        Testcases.get({
          testcaseId: $stateParams.testcaseId
        }, function(testcase) {
          $scope.testcase = testcase;
        });
      }
    };
  }
]);
