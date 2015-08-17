'use strict';

angular.module('service-testing-tool').controller('TeststepsController', ['$scope', 'Teststeps', 'Testruns', '$stateParams', '$state', '$http', '$modal',
  function($scope, Teststeps, Testruns, $stateParams, $state, $http, $modal) {
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

    var timer;
    $scope.teststep = {};
    //  use object instead of primitives, so that child scope can update the values
    $scope.savingStatus = {
      saveSuccessful: null,
      savingErrorMessage: null
    };
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
          alert('Error');
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
          alert('Error');
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.remove = function(teststep) {
      teststep.$remove(function(response) {
        $state.go('testcase_edit', { testcaseId: $stateParams.testcaseId });
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
        });
      } else {
        // create a new entry
        $scope.teststep.testcaseId = $stateParams.testcaseId;
      }
    };

    $scope.createDSFieldContainAssertion = function(fieldName) {
      $scope.$broadcast('createDSFieldContainAssertion', fieldName);
    };

    $scope.evaluateDataSet = function() {
      $scope.$broadcast('evaluateDataSet', $scope.responseOptions.data);
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
              menuItems: [
                {
                  title: 'Create An Assertion',
                  icon: 'ui-grid-icon-plus-squared',
                  context: $scope,
                  action: function() {
                    this.context.createDSFieldContainAssertion(this.context.col.colDef.field);
                  }
                }
              ]
            });
          }
        }
      }, function(error) {
        alert('Error');
      });
    };

    $scope.assertionsAreaLoadedCallback = function() {
      $scope.$broadcast('assertionsAreaLoaded');
    };
  }
]);
