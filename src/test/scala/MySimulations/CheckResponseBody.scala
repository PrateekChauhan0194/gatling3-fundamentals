package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseBody extends Simulation {

  val httpConf = http.baseUrl("https://postcodes.io")
    .acceptHeader("application/json")

  val scn = scenario("Validate the response body")  // Scenario name
    .exec(
      http("Lookup a postcode")   // API call name
        .get("/postcodes/OX49%205NU")   // API endpoint
        .check(
          jsonPath("$.result.country").is("England")  // Validating the data in json response
        )
    ).pause(2)  // Adding a wait time

    .exec(
      http("Get a random postcode")
        .get("/random/postcodes")
        .check(
          status.is(200),   // Validating the http response code
          jsonPath("$.result.postcode").saveAs("POSTCODE"),   // Saving the data from json response to a variable
          jsonPath("$.result.longitude").saveAs("LONGITUDE"),
          jsonPath("$.result.latitude").saveAs("LATITUDE")
        )
    ).pause(2)

    .exec(
      http("Get nearest postcodes for a given longitude & latitude")
        .get("/postcodes?lon=#{LONGITUDE}&lat=#{LATITUDE}")   // Reusing the data that was stored in a variable in the last exec block
        .check(
          status.is(200),
          jsonPath("$.result[0].postcode").saveAs("RESULT"),
          jsonPath("$.result[0].postcode").is("#{POSTCODE}"),
        )
        .check( bodyString.saveAs("responseBody") )   // Saving the whole response body to a session variable
    ).pause(2)
    .exec { session => println(session); session } // Printing all the variables from the current execution session for debugging purposes
    .exec { session => println("RESULT: " + session("RESULT").as[String]); session } // Printing a specific variable from the current execution session for debugging purposes
    .exec { session => println("responseBody: " + session("responseBody").as[String]); session }  // Printing the whole response body that we saved earlier

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
