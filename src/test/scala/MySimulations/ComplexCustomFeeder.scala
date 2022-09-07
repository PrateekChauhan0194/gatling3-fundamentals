package MySimulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

class ComplexCustomFeeder extends Simulation {
  /**
   * Swagger: https://videogamedb.uk/swagger-ui/index.html
   *
   * @return
   */

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val idNumber = (1 to 10).iterator
  val random = new Random()
  val datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def randomString(length: Int) = {
    random.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getRandomDate(startDate: LocalDate, random: Random) = {
    startDate.minusDays(random.nextInt(30)).format(datePattern)
  }

  val customFeeder = Iterator.continually(Map(
      "gameId" -> idNumber.next(),
      "name" -> ("Game-" + randomString(5)),
      "releaseDate" -> getRandomDate(LocalDate.now(), random),
      "reviewScore" -> random.nextInt(100),
      "category" -> ("Category-" + randomString(5)),
      "rating" -> ("Rating-" + randomString(5)),
    ))

  // Call to authenticate the user and get an auth token to make the further authenticated calls
  def authenticateUser() = {
    exec(
      http("Authenticate user")
        .post("/authenticate")
        .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}")).asJson
        .check(jsonPath("$.token").saveAs("AUTH_TOKEN")) // Saving auth token in a session variable
    )
  }

  def addGame() = {
    repeat(10) {
      feed(customFeeder)
        .exec(
          http("Add a new game: #{name}")
            .post("/videogame")
            .header("Authorization", "Bearer #{AUTH_TOKEN}")
            .body(ElFileBody("bodies/NewGameTemplate.json")).asJson
            .check(bodyString.saveAs("resBody"))
        ).exec { session => println(session("resBody").as[String]); session }
        .pause(1)
    }
  }

  val scn = scenario("Complex custom feeder")
    .exec(authenticateUser())
    .exec(addGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
