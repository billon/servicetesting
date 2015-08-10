'use strict';

angular.module('service-testing-tool').controller('EnvironmentsController', ['$scope', 'Environments', 'EnvEntries', 'PageNavigation', '$location', '$stateParams', '$state', 'uiGridConstants', '$modal',
  function($scope, Environments, EnvEntries, PageNavigation, $location, $stateParams, $state, uiGridConstants, $modal) {
    $scope.schema = {
      type: "object",
      properties: {
        id: { type: "integer" },
        name: { type: "string", maxLength: 50 },
        description: { type: "string", maxLength: 500 }
      },
      "required": ["name", "description"]
    };

    $scope.form = [
      {
        key: "name",
        validationMessage: "The Name is required and should be less than 50 characters"
      },
      {
        key: "description",
        type: "textarea",
        validationMessage: "The Description is required and should be less than 500 characters"
      }
    ];

    $scope.environment = {};

    $scope.selectedEntries = [];

    $scope.alerts = [];

    $scope.envGridColumnDefs = [
      {
        name: 'name', width: 200, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        },
        cellTemplate:'gridCellTemplate.html'
      },
      {
        name: 'description', width: 600, minWidth: 300
      }
    ];

    $scope.enventryGridOptions = {
      paginationPageSizes: [10,20,50,100], paginationPageSize: 10,
      enableFiltering: true,
      columnDefs: [
        {
          field: 'intface.name', displayName: 'Interface', width: 200, minWidth: 100,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          },
          cellTemplate:'gridIntfaceCellTemplate.html'
        },
        {
          field: 'endpoint.name', displayName: 'Endpoint',width: 600, minWidth: 300,
          cellTemplate:'gridEndpointCellTemplate.html'
        }
      ],
      onRegisterApi: function(gridApi) {
        //set gridApi on scope
        $scope.gridApi = gridApi;
        gridApi.selection.on.rowSelectionChanged($scope, function (row) {
          if (row.isSelected) {
            $scope.selectedEntries.push(row.entity);
          } else {
            $scope.selectedEntries = _.without($scope.selectedEntries, row.entity);
          }
        });
      }
    };

    $scope.removeEntries = function() {
      $scope.environment.entries = _.difference($scope.environment.entries, $scope.selectedEntries);
      $scope.enventryGridOptions.data = $scope.environment.entries;
    };

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if ($scope.environment.id) {
          $scope.environment.$update(function(response) {
            $scope.environment = response;
            $scope.alerts.push({type: 'success', msg: 'The Environment has been updated successfully'});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        } else {
          var environment = new Environments($scope.environment);
          environment.$save(function(response) {
            PageNavigation.contexts.push($scope.context);
            $state.go('environment_edit', {environmentId: response.id});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        }
      }
    };

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };

    $scope.remove = function(environment) {
      environment.$remove(function(response) {
          $state.go('environment_all');
      });
    };

    $scope.find = function() {
      Environments.query(function(environments) {
        $scope.environments = environments;
      });
    };

    $scope.goto = function(state, params, expect, assignto) {
      var intfaceIds = _.pluck($scope.environment.entries, 'intfaceId');

      var context = {
        model: $scope.environment,
        intfaceIds: intfaceIds,
        expect: expect,
        assignto: assignto
      };

      var modalInstance = $modal.open({
        animation: true,
        templateUrl: '/ui/views/intfaces/list-modal.html',
        controller: 'IntfacesController',
        windowClass: 'large-Modal',
        resolve: {
          context: function () {
            return context;
          }
        }
      });
    };

    $scope.return = function() {
      PageNavigation.returns.push($scope.context.model);
      $location.path($scope.context.url);
    };

    $scope.select = function() {
      $scope.context.model.environmentId = $scope.environment.id;

      Environments.get({
        environmentId: $scope.context.model.environmentId
      }, function(environment) {
        $scope.context.model.environment = environment;

        PageNavigation.returns.push($scope.context.model);
        $location.path($scope.context.url);
      });
    };

    $scope.findOne = function() {
      // Invoked from other page
      $scope.context = PageNavigation.contexts.pop();

      // Returned from other page
      var returnObj = PageNavigation.returns.pop();
      if (returnObj) {
        var selectedIntfaces = returnObj.selectedIntfaces;
        if (selectedIntfaces) {
          _.each(selectedIntfaces, function(selectedIntface, index, list) {
            returnObj.model.entries.push({
              intfaceId: selectedIntface.id,
              intface: selectedIntface
            });
          });
        }
        $scope.environment = returnObj.model;
        $scope.enventryGridOptions.data = returnObj.model.entries;

      } else {
        // A standalone page
        if ($stateParams.environmentId) {
          Environments.get({
            environmentId: $stateParams.environmentId
          }, function(environment) {
            $scope.environment = environment;
            $scope.enventryGridOptions.data = environment.entries;
          });
        }
      }
    };
  }
]);
