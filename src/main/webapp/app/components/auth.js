angular.module('myApp.security', [])
        .controller('AppLoginCtrl', function ($scope, $rootScope, $http, $window, $location, $uibModal, jwtHelper) {

          $rootScope.$on('logOutEvent', function () {
            $scope.logout();
          });

          $scope.$on("NotAuthenticatedEvent", function (event, res) {
            $scope.$emit("logOutEvent");
            
            if (typeof res.data.error !== "undefined" && res.data.error.message) {
              if (res.data.error.message.indexOf("No authorization header") === 0) {
                //Provide a friendly message
                $scope.openErrorModal("You are not authenticated to perform this operation. Please login");
              }
              else {
                $scope.openErrorModal(res.data.error.message);
              }
            }
            else {
              //You should never get here - format your error messages as suggested by the seed (backend)
              $scope.openErrorModal("You are not authenticated");
            }

          });

          $scope.$on("NotAuthorizedEvent", function (event, res) {
            if (typeof res.data.error !== "undefined" && res.data.error.message) {
              $scope.openErrorModal(res.data.error.message);
            }
            else {
              $scope.openErrorModal("You are not authorized to perform the requested operation");
            }
          });

          $scope.$on("HttpErrorEvent", function (event, res) {
            if (typeof res.data.error !== "undefined" && res.data.error.message) {
              $scope.openErrorModal(res.data.error.message);
            }
            else {
              $scope.openErrorModal("Unknown error during http request");
            }
          });

          clearUserDetails($scope);

          $scope.login = function () {
            $http.post('api/login', $scope.user)
                    .success(function (data) {
                      $window.sessionStorage.id_token = data.token;
                      initializeFromToken($scope, $window.sessionStorage.id_token, jwtHelper);
                      $location.path("#/view1");
                    })
                    .error(function (data) {
                      delete $window.sessionStorage.id_token;
                      clearUserDetails($scope);
                    });
          };

          $rootScope.logout = function () {
            $scope.isAuthenticated = false;
            $scope.isAdmin = false;
            $scope.isUser = false;
            delete $window.sessionStorage.id_token;
            $location.path("/view1");
          };

          $rootScope.openErrorModal = function (text) {
            var modalInstance = $uibModal.open({
              animation: true,
              templateUrl: 'errorModal.html',
              controller: function ($scope, $uibModalInstance) {
                $scope.error = text;
                $scope.ok = function () {
                  $uibModalInstance.close();
                };
              },
              size: 'sm'
            });
          };

          //This sets the login data from session store if user pressed F5 (You are still logged in)
          var init = function () {
            var token = $window.sessionStorage.id_token;
            if (token) {
              initializeFromToken($scope, $window.sessionStorage.id_token, jwtHelper);
            }
          };
          init();// and fire it after definition
        })
        .factory('AuthInterceptor', function ($rootScope, $q) {
          return {
            responseError: function (response) {
              var name = "";
              switch (response.status) {
                case 401:
                  name = "NotAuthenticatedEvent";
                  break;
                case 403:
                  name = "NotAuthorizedEvent";
                  break;
                default :
                  name = "HttpErrorEvent";
              }
              $rootScope.$broadcast(name, response);
              return $q.reject(response);
            }
          };
        })
        .config(function Config($httpProvider, jwtInterceptorProvider) {
          jwtInterceptorProvider.tokenGetter = function () {
            return sessionStorage.getItem('id_token');
          };
          $httpProvider.interceptors.push('jwtInterceptor');
        });



function initializeFromToken($scope, token, jwtHelper) {
  $scope.isAuthenticated = true;
  var tokenPayload = jwtHelper.decodeToken(token);
  $scope.username = tokenPayload.username;
  $scope.isAdmin = false;
  $scope.isUser = false;
  tokenPayload.roles.forEach(function (role) {
    if (role === "Admin") {
      $scope.isAdmin = true;
    }
    if (role === "User") {
      $scope.isUser = true;
    }
  });
}

function clearUserDetails($scope) {
  $scope.username = "";
  $scope.isAuthenticated = false;
  $scope.isAdmin = false;
  $scope.isUser = false;
}






