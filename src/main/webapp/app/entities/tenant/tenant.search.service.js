(function() {
    'use strict';

    angular
        .module('moestuinApp')
        .factory('TenantSearch', TenantSearch);

    TenantSearch.$inject = ['$resource'];

    function TenantSearch($resource) {
        var resourceUrl =  'api/_search/tenants/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
