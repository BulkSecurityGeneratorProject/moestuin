(function() {
    'use strict';

    angular
        .module('moestuinApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('tenant', {
            parent: 'entity',
            url: '/tenant?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'moestuinApp.tenant.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tenant/tenants.html',
                    controller: 'TenantController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tenant');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('tenant-detail', {
            parent: 'entity',
            url: '/tenant/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'moestuinApp.tenant.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tenant/tenant-detail.html',
                    controller: 'TenantDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tenant');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Tenant', function($stateParams, Tenant) {
                    return Tenant.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'tenant',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('tenant-detail.edit', {
            parent: 'tenant-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tenant/tenant-dialog.html',
                    controller: 'TenantDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tenant', function(Tenant) {
                            return Tenant.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tenant.new', {
            parent: 'tenant',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tenant/tenant-dialog.html',
                    controller: 'TenantDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                apiKey: null,
                                streetAddress: null,
                                postalCode: null,
                                city: null,
                                country: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('tenant', null, { reload: 'tenant' });
                }, function() {
                    $state.go('tenant');
                });
            }]
        })
        .state('tenant.edit', {
            parent: 'tenant',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tenant/tenant-dialog.html',
                    controller: 'TenantDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tenant', function(Tenant) {
                            return Tenant.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tenant', null, { reload: 'tenant' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tenant.delete', {
            parent: 'tenant',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tenant/tenant-delete-dialog.html',
                    controller: 'TenantDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Tenant', function(Tenant) {
                            return Tenant.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tenant', null, { reload: 'tenant' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
