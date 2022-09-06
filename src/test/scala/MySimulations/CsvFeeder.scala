package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeeder extends Simulation {

  /**
   * Swagger UI: https://videogamedb.uk/swagger-ui/index.html
   *
   * @return
   */

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  val csvFeeder = csv("data/gameCsvFile.csv").circular

  def getSpecificGame() = {
    repeat(5) {
      feed(csvFeeder).exec(
          http("Get specific game: #{gameName}")
            .get("/videogame/#{gameId}")
            .check(jsonPath("$.name").is("#{gameName}"))
            .check(status.is(200))
        ).pause(1)
    }
  }

  val scn = scenario("CSV Feeder")
    .exec(getSpecificGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
