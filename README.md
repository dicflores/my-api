# My API
Simple REST Api for testing purposes.

## Problem description
There is a campsite which is available to be reserved. This campsite is small, so it may be reserved by one single person at a time. There are a few constraints with respect to reservations:
  - The campsite can be reserved for max 3 days.
  - The campsite can be reserved minimum 1 day ahead of arrival, and up to 1 month in advance.
  - Reservations can be cancelled at any time.
  - There is no check-in/check-out conflicts. The reservation is for the whole day.

## Requirements
  - Users will need to find out when the campsite is available for a given date range (if none provided, default to 1 month ahead).
  - To reserve the campsite, user will provide his/her full name, email along with intended arrival date and departure date. If the reservation is successful, a unique booking identifier should be returned to allow future operations to that booking.
  - Given a booking identifier, users may update the booking information.
  - Given a booking identifier, users may cancel that reservation.
  - Given a booking identifier, users may get details of a reservation.
  - Provide appropiate error messages to indicate error cases.

## Tools
The following tools/frameworks were used:
  - Spring Boot
  - Spring Data JPA
  - Spring Web/WebMVC
  - Maven
  - JUnit
  - Spring Test

## Operations
### Get available dates
With default 1 month data range:
```sh
curl -v -X GET localhost:8080/calendar
Note: Unnecessary use of -X or --request, GET is already inferred.
*   Trying ::1:8080...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /calendar HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.67.0
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Mon, 13 Jan 2020 03:33:06 GMT
< 
* Connection #0 to host localhost left intact
["2020-01-13","2020-01-14","2020-01-15","2020-01-16","2020-01-17", ..., "2020-02-11","2020-02-12","2020-02-13"]
```

With custom data range:
```sh
curl -v -X GET 'localhost:8080/calendar?arrival=2020-01-14&departure=2020-01-20'
Note: Unnecessary use of -X or --request, GET is already inferred.
*   Trying ::1:8080...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /calendar?arrival=2020-01-14&departure=2020-01-20 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.67.0
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Mon, 13 Jan 2020 04:05:28 GMT
< 
* Connection #0 to host localhost left intact
["2020-01-14","2020-01-15","2020-01-16","2020-01-17","2020-01-18","2020-01-19","2020-01-20"]
```