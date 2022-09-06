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

  /**
   * Creating a feeder instance.
   * This will,
   * 1. Pull the data from the specified csv
   * 2. Store the values in the session storage for it to be used in the test scenario
   */
  val csvFeeder = csv("data/gameCsvFile.csv").circular

  def getSpecificGame() = {
    repeat(5) {
      feed(csvFeeder) // To feed the values from csv
        .exec(
          http("Get specific game: #{gameName}")  // Using the csv feed value from the session
            .get("/videogame/#{gameId}")
            .check(jsonPath("$.name").is("#{gameName}"))  // Using the csv feed value from the session
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
