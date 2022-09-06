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

  val scn = scenario("Complex custom feeder")

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
