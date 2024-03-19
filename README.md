## Task
Create a weather application that makes HTTP calls to an external web service. The application should allow users to search for the weather of a city and make GET requests to https://openweathermap.org for the following information under key "weather":
1. Icon
2. Main
3. Description

Requested information should be in JSON format. If the information is already inside redis cache, server should draw information from cache. If information is not inside cache, server should make a GET request to OpenWeatherMap for the necessary information and store the information in the cache. All weather information within the cache should expire in 30 mins from storage. If information does not exist e.g. due to invalid city, application should display a message indicating "invalid city".