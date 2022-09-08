package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class RampUsersLoadSimulation extends Simulation {
  /**
   * Swagger: https://videogamedb.uk/swagger-ui/index.html
   *
   * @return
   */

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  def getAllGames() = {
    exec(
      http("Get all games")
        .get("/videogame")
    )
  }

  def getGame() = {
    exec(
      http("Get game")
        .get("/videogame/1")
    )
  }

  val scn = scenario("Ramp users load simulation")
    .exec(getAllGames()).pause(1)
    .exec(getGame()).pause(1)
    .exec(getAllGames()).pause(1)

  /**
   * The building blocks for open model profile injection are:
   *
   * nothingFor(duration): Pause for a given duration.
   * atOnceUsers(nbUsers): Injects a given number of users at once.
   * rampUsers(nbUsers).during(duration): Injects a given number of users distributed evenly on a time window of a given duration.
   * constantUsersPerSec(rate).during(duration): Injects users at a constant rate, defined in users per second, during a given duration. Users will be injected at regular intervals.
   * constantUsersPerSec(rate).during(duration).randomized: Injects users at a constant rate, defined in users per second, during a given duration. Users will be injected at randomized intervals.
   * rampUsersPerSec(rate1).to.(rate2).during(duration): Injects users from starting rate to target rate, defined in users per second, during a given duration. Users will be injected at regular intervals.
   * rampUsersPerSec(rate1).to(rate2).during(duration).randomized: Injects users from starting rate to target rate, defined in users per second, during a given duration. Users will be injected at randomized intervals.
   * stressPeakUsers(nbUsers).during(duration): Injects a given number of users following a smooth approximation of the heaviside step function stretched to a given duration.
   *
   */
  setUp(
    scn.inject(
      nothingFor(4), // 1
      atOnceUsers(10), // 2
      rampUsers(10).during(5), // 3
      constantUsersPerSec(20).during(15), // 4
      constantUsersPerSec(20).during(15).randomized, // 5
      rampUsersPerSec(10).to(20).during(10.minutes), // 6
      rampUsersPerSec(10).to(20).during(10.minutes).randomized, // 7
      // stressPeakUsers(1000).during(20) // 8
    ).protocols(httpProtocol)
  )
}
