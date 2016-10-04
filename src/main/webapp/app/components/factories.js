'use strict';

/* Place your global Factory-service in this file */

angular.module('myApp.factories', []).
  factory('InfoFactory', function () {
    var info = "Hello World from a Factory";
    var getInfo = function getInfo(){
      return info;
    };
    return {
      getInfo: getInfo
    };
  });