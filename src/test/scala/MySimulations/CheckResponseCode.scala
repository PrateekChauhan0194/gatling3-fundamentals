package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseCode extends Simulation {

  val httpConf = http.baseUrl("https://postcodes.io")
    .acceptHeader("application/json")

  val scn = scenario("Get postcodes")
    .exec(
      http("Get a random postcode")
        .get("/random/postcodes")
        .check(status.is(200))
    )
    .pause(2)

    .exec(
      http("Validate postcode")
        .get("/postcodes/BS3%204SR/validate")
        .check(status.in(200 to 210))
    )
    .pause(2)

    .exec(
      http("Nearest postcodes for postcode")
        .get("/postcodes/BS3%204SR/nearest")
        .check(status.not(404), status.not(500))
    )

  setUp(
    scn.inject(atOnceUsers(1000))
  ).protocols(httpConf)
}
