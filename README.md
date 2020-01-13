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

## Date ranges
Both arrival and departure dates are inclusive. Samples:
| Arrival | Departure | Number of days |
| --------| --------- | -------------- |
| 2020-01-15 | 2020-01-15 | 1 (Only Jan-15) |
| 2020-01-15 | 2020-01-16 | 2 (Jan-15 and 16) |
| 2020-01-15 | 2020-01-17 | 3 (Jan-15, 16 and 17) |

## Tools
The following tools/frameworks were used:
  - Spring Boot
  - Spring Data JPA
  - Spring Web/WebMVC
  - Maven
  - JUnit
  - Spring Test

## Operations
### Summary Table
| Method | Path | Description |
| ------ | ---- | ------------|
| **GET** | `/calendar` | Get available dates from current date to 1 month in advance. |
| **GET** | `/calendar?arrival=2020-01-15&departure=2020-01-17` | Get available dates with custom date range. |
| **POST** | `/bookings` | Create a Booking. |
| **PUT** | `/bookings/{id}` | Update a Booking. |
| **DELETE** | `/bookings/{id}` | Delete a Booking. |
| **GET** | `/bookings/{id}` | Gets information about a Booking. |

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

### Create a new Booking
To create a new booking, make a POST with JSON body with the following details:
```json
{
  "email": "damian@email.com",
  "fullName": "Damian",
  "dates": {
    "arrival": "2020-01-15",
    "departure": "2020-01-17"
  }
}
```
```sh
curl -v -X POST localhost:8080/bookings -H "Content-Type: application/json" -d '{"email":"damian@email.com","fullName":"Damian","dates": {"arrival":"2020-01-15", "departure":"2020-01-17"}}'
Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying ::1:8080...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> POST /bookings HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.67.0
> Accept: */*
> Content-Type: application/json
> Content-Length: 108
> 
* upload completely sent off: 108 out of 108 bytes
* Mark bundle as not supporting multiuse
< HTTP/1.1 201 
< Location: http://localhost:8080/bookings/1
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Mon, 13 Jan 2020 04:28:47 GMT
< 
* Connection #0 to host localhost left intact
{"id":1,"email":"damian@email.com","fullName":"Damian","dates":{"arrival":"2020-01-15","departure":"2020-01-17"}}
```
The returned JSON will have the unique booking id:
```json
{
  "id": 1,
  "email": "damian@email.com",
  "fullName": "Damian",
  "dates": {
    "arrival": "2020-01-15",
    "departure": "2020-01-17"
  }
}
```

### Update a Booking
To update a booking, make a PUT with JSON body with the following details (Booking id is part of Path)
```json
{
  "email": "damian@newemail.com",
  "fullName": "Damian",
  "dates": {
    "arrival": "2020-01-15",
    "departure": "2020-01-16"
  }
}
```
```sh
curl -v -X PUT localhost:8080/bookings/1 -H "Content-Type: application/json" -d '{"email":"damian@newemail.com","fullName":"Damian","dates": {"arrival":"2020-01-15", "departure":"2020-01-16"}}'
*   Trying ::1:8080...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> PUT /bookings/1 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.67.0
> Accept: */*
> Content-Type: application/json
> Content-Length: 111
> 
* upload completely sent off: 111 out of 111 bytes
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Mon, 13 Jan 2020 04:39:30 GMT
< 
* Connection #0 to host localhost left intact
{"id":1,"email":"damian@newemail.com","fullName":"Damian","dates":{"arrival":"2020-01-15","departure":"2020-01-16"}}
```

### Cancel a Booking
To delete a booking, make a DELETE with booking id on the path:
```sh
curl -v -X DELETE localhost:8080/bookings/1 
*   Trying ::1:8080...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> DELETE /bookings/1 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.67.0
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 
< Content-Length: 0
< Date: Mon, 13 Jan 2020 04:43:57 GMT
< 
* Connection #0 to host localhost left intact
```