'use strict';

/* Place your Global Filters in this file */

angular.module('myApp.filters', []).
  filter('checkmark', function () {
    return function(input) {
      return input ? '\u2713' : '\u2718';
    };
  });
