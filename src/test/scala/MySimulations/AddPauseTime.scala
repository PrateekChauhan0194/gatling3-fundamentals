package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class AddPauseTime extends Simulation {

  // Http configuration
  val httpConf = http.baseUrl("http://localhost:8000/api/v1")
    .header("Accept", "application/json")

  // Scenario
  val scn = scenario("Video game DB - 3 calls")
    .exec(http("Get all games").get("/videogames"))
    .pause(1)
    .exec(http("Get game with ID").get("/videogames/1"))
    .pause(1, 5)
    .exec(http("Get all games").get("/videogames"))
    .pause(3000.milliseconds)

  //Load simulation
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf);

}
