(function() {
    'use strict';

    angular
        .module('moestuinApp')
        .controller('TenantDetailController', TenantDetailController);

    TenantDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Tenant', 'Event'];

    function TenantDetailController($scope, $rootScope, $stateParams, previousState, entity, Tenant, Event) {
        var vm = this;

        vm.tenant = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('moestuinApp:tenantUpdate', function(event, result) {
            vm.tenant = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
