package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicCustomFeeder extends Simulation {
  /**
   * Swagger: https://videogamedb.uk/swagger-ui/index.html
   *
   * @return
   */

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  val idNumbers = (1 to 10).iterator

  val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getGame() = {
    repeat(10) {
      feed(customFeeder)
        .exec(
          http("Get game with ID: #{gameId}")
            .get("/videogame/#{gameId}")
            .check(status.is(200))
        ).pause(1)
    }
  }

  val scn = scenario("Basic custom feeder")
    .exec(getGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
