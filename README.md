# SimpleDirections
An application that utilizes the Google Directions API, AsyncTask, and Google Maps map fragment to download driving directions. 

It was created for an assignment in CEN4411 Advanced Mobile Application Development at Rasmussen College. Directions are downloaded within an AsyncTask so lengthy downloads do not lag the main thread. The parser parses the xml response from the Directions API to display simple step-by-step directions to the user. The directions are also interpretted into connected polylines on a GoogleMap object and displayed with a start and ending marker.
