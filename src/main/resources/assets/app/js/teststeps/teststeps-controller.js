'use strict';

angular.module('service-testing-tool').controller('TeststepsController', ['$scope', 'Teststeps', 'Testruns',
  '$stateParams', '$state', '$http', '$modal', 'uiGridConstants', 'uiGridEditConstants', 'STTUtils',
  function($scope, Teststeps, Testruns, $stateParams, $state, $http, $modal, uiGridConstants, uiGridEditConstants, STTUtils) {
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

    $scope.teststep = {};
    $scope.tempData = {};
    $scope.showAssertionsArea = false;
    $scope.responseOptions = {
      enableFiltering: true,
      columnDefs: [ ]
    };

    $scope.alerts = [];

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.teststep.$update(function(response) {
          $scope.alerts.push({type: 'success', msg: 'The Test Step has been updated successfully'});
        }, function(error) {
          $scope.alerts.push({type: 'warning', msg: error.data});
        });
      }
    };

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };

    $scope.loadWsdl = function() {
      if ($scope.teststep.intfaceId && $scope.teststep.intface.deftype==='WSDL') {
        $scope.teststep.wsdlUrl = $scope.teststep.intface.defurl;
      }
      $http
        .get('api/wsdls/anywsdl/operations', {
          params: {
            wsdlUrl: $scope.teststep.wsdlUrl
          }
        })
        .success(function(data, status) {
          $scope.teststep.wsdlBindings = data;
          $scope.teststep.wsdlBinding = $scope.teststep.wsdlBindings[0];
          $scope.teststep.wsdlOperations = $scope.teststep.wsdlBindings[0].operations;
          $scope.teststep.wsdlOperation = $scope.teststep.wsdlOperations[0];
        })
        .error(function(data, status) {
          $scope.alerts.push({type: 'warning', msg: data});
        });
    };

    $scope.refreshOperations = function() {
      $scope.wsdlOperations = _.findWhere($scope.wsdlBindings, { name: $scope.wsdlBinding.name }).operations;
      $scope.wsdlOperation = $scope.wsdlOperations[0];
    };

    $scope.create = function(isValid) {
      if (isValid) {
        var teststep = new Teststeps({
          testcaseId: this.teststep.testcaseId,
          name: this.teststep.name,
          description: this.teststep.description
        });
        if (this.teststep.intfaceId) {
          teststep.intfaceId = this.teststep.intfaceId;
          if (this.teststep.intface.deftype === "WSDL") {
            teststep.type = 'SOAP';
            teststep.properties = {
              wsdlUrl: this.teststep.wsdlUrl,
              wsdlBindingName: this.teststep.wsdlBinding.name,
              wsdlOperationName: this.teststep.wsdlOperation
            };
          }
        } else {
          teststep.type = 'SOAP';
          teststep.properties = {
            wsdlUrl: this.teststep.wsdlUrl,
            wsdlBindingName: this.teststep.wsdlBinding.name,
            wsdlOperationName: this.teststep.wsdlOperation
          };
        }
        teststep.$save(function(response) {
          $state.go('teststep_edit', {testcaseId: response.testcaseId, teststepId: response.id});
        }, function(error) {
          $scope.alerts.push({type: 'warning', msg: error.data});
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.remove = function(teststep) {
      var context = {
        message: 'Do you want to delete the test step "' + teststep.name + '" and all its assertions?'
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
          teststep.$remove(function(response) {
            $state.go('testcase_edit', { testcaseId: $stateParams.testcaseId });
          });
        }
      });
    };

    $scope.changeIntface = function(teststep) {
      var context = {
        intfaceId: teststep.intfaceId,
        expect: 'Single'
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

      modalInstance.result.then(function (selectedIntface) {
        if (selectedIntface) {
          teststep.intfaceId = selectedIntface.id;
          teststep.intface = selectedIntface;
        }
      });
    };

    $scope.createIntface = function(teststep) {
      var context = {
        defurl: teststep.wsdlUrl,
        deftype: 'WSDL',
        expect: 'Single'
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

      modalInstance.result.then(function (selectedIntface) {
        if (selectedIntface) {
          teststep.intfaceId = selectedIntface.id;
          teststep.intface = selectedIntface;
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

    $scope.changeEndpoint = function(teststep) {
      var context = {
        endpointId: teststep.endpointId,
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
          teststep.endpointId = selectedEndpoint.id;
          teststep.endpoint = selectedEndpoint;
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

    $scope.findOne = function() {
      if ($stateParams.teststepId) {
        // edit an existing entry
        Teststeps.get({
          testcaseId: $stateParams.testcaseId,
          teststepId: $stateParams.teststepId
        }, function (response) {
          $scope.teststep = response;
          $scope.assertionsModelObj.gridOptions.data = response.assertions;
        });
      } else {
        // create a new entry
        $scope.teststep.testcaseId = $stateParams.testcaseId;
      }
    };

    $scope.invoke = function(teststep) {
      var testrun;
      if ($scope.teststep.endpointId) {
        testrun = {
          request: $scope.teststep.request,
          endpointId: $scope.teststep.endpointId
        };
      } else {
        testrun = {
          request: $scope.teststep.request,
          details: $scope.teststep.properties
        };
      }

      var testrunRes = new Testruns(testrun);
      testrunRes.$save(function(response) {
        $scope.tempData.soapResponse = response.response;
        $scope.responseOptions.data = response.response;
        $scope.responseOptions.columnDefs = [ ];
        if (response.response.length > 0) {
          var row = response.response[0];
          for (var key in row) {
            $scope.responseOptions.columnDefs.push({
              field: key,
              displayName: key,
              cellTemplate: 'responseCellTemplate.html'
            });
          }
        }
      }, function(error) {
        $scope.alerts.push({type: 'warning', msg: error.data});
      });
    };

    $scope.assertionsModelObj = {};

    $scope.assertionsModelObj.gridOptions = {
      columnDefs: [
        {
          name: 'name', width: 250, minWidth: 250,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          },
          editableCellTemplate: 'assertionGridNameEditableCellTemplate.html'
        },
        {name: 'properties.field', displayName: 'Field', width: 100, minWidth: 100, enableCellEdit: false},
        {name: 'properties.operator', displayName: 'Operator', width: 100, minWidth: 100, enableCellEdit: false},
        {
          name: 'properties.value', displayName: 'Value', width: 200, minWidth: 200,
          editableCellTemplate: 'assertionGridValueEditableCellTemplate.html'
        },
        {
          name: 'result', width: 100, minWidth: 100, enableCellEdit: false,
          cellTemplate: 'assertionGridResultCellTemplate.html'
        },
        {name: 'delete', width: 100, minWidth: 100, enableSorting: false, enableCellEdit: false,
          cellTemplate: 'assertionGridDeleteCellTemplate.html'
        }
      ]
    };

    $scope.createDSFieldContainAssertion = function(field, entity) {
      var assertion = {
        teststepId: $stateParams.teststepId,
        name: 'Check ' + field,
        type: 'DSField',
        properties: {
          field: field,
          operator: 'Contains',
          value: entity[field]
        }
      };
      $scope.assertionsModelObj.gridOptions.data.push(assertion);
    };

    $scope.removeAssertion = function(assertion) {
      var assertionId = assertion.id;
      var gridData = $scope.assertionsModelObj.gridOptions.data;
      var indexOfRowToBeDeleted = STTUtils.indexOfArrayElementByProperty(gridData, 'id', assertionId);
      gridData.splice(indexOfRowToBeDeleted, 1);
    };

    $scope.evaluateDataSet = function() {
      var assertions = $scope.assertionsModelObj.gridOptions.data;
      for (var i = 0; i < assertions.length; i ++) {
        var assertion = assertions[i];
        var values = _.pluck($scope.responseOptions.data, assertion.properties.field);
        assertion.result = _.contains(values, assertion.properties.value);
      }
    };

    $scope.assertionsAreaLoadedCallback = function() {
      $scope.$broadcast('assertionsAreaLoaded');
    };
  }
]);
