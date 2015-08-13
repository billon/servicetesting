'use strict';

angular.module('service-testing-tool').controller('IntfacesModalController', ['$scope', 'Intfaces', '$stateParams', 'uiGridConstants', '$modalInstance', 'context',
  function($scope, Intfaces, $stateParams, uiGridConstants, $modalInstance, context) {
    $scope.schema = {
      type: "object",
      properties: {
        id: { type: "integer" },
        name: { type: "string", maxLength: 50 },
        description: { type: "string", maxLength: 500 },
        /*relpath: {
          type: "string",
          maxLength: 50,
          pattern: "^(\/([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-\.]*[A-Za-z0-9])+)*$"
        },*/
        deftype: {
          type: "string",
          maxLength: 50
        },
        defurl: {
          type: "string",
          maxLength: 200,
          pattern: "^http:\/{2}(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9])(\/([a-z0-9_\.-])+)*\/?[A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-\.]*[A-Za-z0-9]$"
        }
      },
      "required": ["name", "description", "deftype", "defurl"]
    };

    $scope.form = [
      {
        key: "name",
        title: "Name",
        validationMessage: "The Name is required and should be less than 50 characters"
      },
      {
        key: "description",
        title: "Description",
        type: "textarea",
        validationMessage: "The Description is required and should be less than 500 characters"
      },
      {
        key: "deftype",
        title: "Definition Type",
        type: "select",
        titleMap: [
          { value: "WSDL", name: "WSDL"},
          { value: "WADL", name: "WADL"},
          { value: "DBInterface", name: "DBInterface"}
        ]
      },
      {
        key: "defurl",
        title: "Interface Definition",
        validationMessage: "The Definition is required and should be at a valid URL"
      }
    ];

    $scope.selectedIntfaces = [];

    $scope.intface = {};

    $scope.alerts = [];

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if (this.intface.id) {
          var intface = this.intface;
          intface.$update(function() {
            $scope.alerts.push({type: 'success', msg: 'The Intface has been updated successfully'});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        } else {
          var intface = new Intfaces(this.intface);
          intface.$save(function(response) {
            $state.go('intface_edit', {intfaceId: response.id});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        }
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

    $scope.gridOptions = {
      paginationPageSizes: [10,20,50,100], paginationPageSize: 10,
      enableFiltering: true,
      enableSelectionBatchEvent: false,
      columnDefs: [
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
        },
        {
          name: 'deftype', displayName: "Definition Type", width: 200, minWidth: 100
        }
      ],
      onRegisterApi: function(gridApi){
        //set gridApi on scope
        $scope.gridApi = gridApi;
        if (gridApi.selection) {
          gridApi.selection.on.rowSelectionChanged($scope,function(row){
            if (row.isSelected) {
              $scope.selectedIntfaces.push(row.entity);
            } else {
              $scope.selectedIntfaces = _.without($scope.selectedIntfaces, row.entity);
            }
          });
        }
      }
    };

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };

    $scope.find = function() {
      $scope.context = context;

      Intfaces.query(function(intfaces) {
        if ($scope.context) {
          intfaces = _.filter(intfaces, function(intface) {
            return ! _.contains($scope.context.intfaceIds, intface.id)
          });
        }
        $scope.intfaces = intfaces;
        $scope.gridOptions.data = intfaces;
      });
    };

    $scope.return = function() {
      $modalInstance.close();
    };

    $scope.select = function() {
      if ($scope.intface.id) {
        $scope.context.model.intfaceId = $scope.intface.id;

        Intfaces.get({
          intfaceId: $scope.context.model.intfaceId
        }, function(intface) {
          $scope.context.model.intface = intface;
        });
      } else {
        $modalInstance.close($scope.selectedIntfaces);
      }
    };

    $scope.findOne = function() {
      $scope.context = context;

      if ($scope.context.intfaceId) {
        Intfaces.get({
          intfaceId: $scope.context.intfaceId
        }, function(intface) {
          $scope.intface = intface;
        });
      }
    };
  }
]);
