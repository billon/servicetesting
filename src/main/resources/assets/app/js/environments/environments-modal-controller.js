'use strict';

angular.module('service-testing-tool').controller('EnvironmentsModalController', ['$scope', 'Environments', '$stateParams', '$state', 'uiGridConstants', '$modal', '$modalInstance', 'context',
  function($scope, Environments, $stateParams, $state, uiGridConstants, $modal, $modalInstance, context) {
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

    $scope.isReturn = function() {
      if ($scope.context) {
        return true;
      }

      return false;
    };

    $scope.isSelect = function() {
      if ($scope.context) {
        if ($scope.context.expect) {
          return true;
        }
      }

      return false;
    };

    $scope.isMultiSelect = function() {
      if ($scope.context) {
        if ($scope.context.expect) {
          if ($scope.context.expect === "Multi") {
            return true;
          }
        }
      }

      return false;
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

    $scope.find = function() {
      $scope.context = context;

      Environments.query(function(environments) {
        if ($scope.context) {
          environments = _.filter(environments, function(environment) {
            return $scope.context.environmentId !== environment.id;
          });
        }
        $scope.environments = environments;
      });
    };

    $scope.addEntries = function() {
      var intfaceIds = _.pluck($scope.environment.entries, 'intfaceId');

      var context = {
        intfaceIds: intfaceIds,
        expect: 'Multi'
      };

      var modalInstance = $modal.open({
        animation: true,
        templateUrl: '/ui/views/intfaces/list-modal.html',
        controller: 'IntfacesModalController',
        windowClass: 'large-modal',
        resolve: {
          context: function () {
            return context;
          }
        }
      });

      modalInstance.result.then(function (selectedIntfaces) {
        if (selectedIntfaces) {
          _.each(selectedIntfaces, function(selectedIntface, index, list) {
            $scope.environment.entries.push({
              intfaceId: selectedIntface.id,
              intface: selectedIntface
            });
          });
        }
      });
    };

    $scope.changeEndpoint = function(entry) {
      var context = {
        endpointId: entry.endpointId,
        expect: 'Single'
      };

      var modalInstance = $modal.open({
        animation: true,
        templateUrl: '/ui/views/endpoints/list-modal.html',
        controller: 'EndpointsModalController',
        windowClass: 'large-modal',
        resolve: {
          context: function () {
            return context;
          }
        }
      });

      modalInstance.result.then(function (selectedEndpoint) {
        if (selectedEndpoint) {
          entry.endpointId = selectedEndpoint.id;
          entry.endpoint = selectedEndpoint;
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

    $scope.viewEndpoint = function(endpointId) {
      var context = {
        endpointId: endpointId
      };

      var modalInstance = $modal.open({
        animation: true,
        templateUrl: '/ui/views/endpoints/edit-modal.html',
        controller: 'EndpointsModalController',
        windowClass: 'large-modal',
        resolve: {
          context: function () {
            return context;
          }
        }
      });
    };

    $scope.return = function() {
      $modalInstance.close();
    };

    $scope.select = function(environment) {
      $modalInstance.close(environment);
    };

    $scope.findOne = function() {
      $scope.context = context;

      if ($scope.context.environmentId) {
        Environments.get({
          environmentId: $scope.context.environmentId
        }, function(environment) {
          $scope.environment = environment;
          $scope.enventryGridOptions.data = environment.entries;
        });
      }
    };
  }
]);
