'use strict';

angular.module('service-testing-tool').controller('MessageboxModalController', ['$scope', '$modalInstance', 'context',
    function($scope, $modalInstance, context) {
        $scope.cancel = function() {
            $modalInstance.close(false);
        };

        $scope.ok = function(selectedIntface) {
            $modalInstance.close(true);
        };

        $scope.getContext = function() {
          $scope.context = context;
        };
    }
]);
