
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("getCoverURLfromISBN", function(request, response) {

	Parse.Cloud.httpRequest({
		url: "www.flipkart.com/search&q=" + request.params.ISBN,
	}).then(function(httpResponse) {
  // success
  var doc = httpResponse.text;

  // var redirectedDoc = doc.match(/"(http.*)"/);
  var myRegexp = /productImage.*(\s*.*){1,3}data-src="(.*)">/;
  var match = doc.match(myRegexp);


  var firstQ = doc.indexOf("\"");
  var lastQ = doc.indexOf("\"", firstQ + 1);
  redirectedDoc = doc.substring(firstQ, lastQ);
  response.success(doc);

},function(httpResponse) {
	response.success('Request failed with response code ' + httpResponse.status);
});
});
