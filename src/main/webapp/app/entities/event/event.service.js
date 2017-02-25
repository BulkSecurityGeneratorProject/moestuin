(function() {
    'use strict';
    angular
        .module('moestuinApp')
        .factory('Event', Event);

    Event.$inject = ['$resource', 'DateUtils'];

    function Event ($resource, DateUtils) {
        var resourceUrl =  'api/events/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.eventOpenTime = DateUtils.convertDateTimeFromServer(data.eventOpenTime);
                        data.eventCloseTime = DateUtils.convertDateTimeFromServer(data.eventCloseTime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
