(function() {
    'use strict';

    angular
        .module('moestuinApp')
        .controller('TenantDeleteController',TenantDeleteController);

    TenantDeleteController.$inject = ['$uibModalInstance', 'entity', 'Tenant'];

    function TenantDeleteController($uibModalInstance, entity, Tenant) {
        var vm = this;

        vm.tenant = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Tenant.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
