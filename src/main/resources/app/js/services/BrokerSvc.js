/**
 * Broker Service
 * @author Lawrence Daniels <lawrence.daniels@gmail.com>
 */
(function () {
    angular.module('trifecta')
        .factory('BrokerSvc', function ($http) {
            var service = {
                "brokers": []
            };

            service.getBrokerDetails = function () {
                return $http.get("/rest/getBrokerDetails")
                    .then(function (response) {
                        return response.data;
                    });
            };

            // pre-load the existing brokers
            service.getBrokerDetails().then(function(brokers) {
                service.brokers = brokers.sort(function(a, b) {
                    if(a.host < b.host) return -1;
                    else if(a.host > b.host) return 1;
                    else return 0;
                });
                if(brokers.length) {
                    brokers[0].expanded = true;
                }
            });

            return service;
        });

})();