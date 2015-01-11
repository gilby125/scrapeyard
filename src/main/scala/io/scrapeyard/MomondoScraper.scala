package io.scrapeyard

import io.scrapeyard.Models.{SearchParams, SearchResult}
import org.joda.time.format.DateTimeFormat
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import org.scalatest.selenium.Firefox

import scala.concurrent.duration._
import scala.language.postfixOps

// val ff = new FirefoxDriver with Firefox
object MomondoScraper extends Firefox with Matchers with Eventually {

  val host = "http://momondo.com"

  def doIt(ps: SearchParams): SearchResult = {
    val fmt = DateTimeFormat.forPattern("dd-MM-yyyy")
    val org = ps.origin
    val dst = ps.destination
    val dep = fmt.print(ps.departure)
    val ret = fmt.print(ps.returning)
    val query = s"http://www.momondo.com/flightsearch/?Search=true&TripType=2&SegNo=2" +
      s"&SO0=$org&SD0=$dst&SDP0=$dep&SO1=$dst" +
      s"&SD1=$org&SDP1=$ret&AD=1&TK=ECO&DO=false&NA=false#Search=true&TripType=2&SegNo=2" +
      s"&SO0=$org&SD0=$dst&SDP0=$dep&SO1=$dst&SD1=$org&SDP1=$ret&AD=1&TK=ECO&DO=false&NA=false"
    go to query

    implicitlyWait(3 minutes)

    eventually (timeout(10 minutes)){
      find("searchProgressText").get.text should be ("Search complete")
    }

    val value = find(cssSelector("span[class=value]")).get.text
    val currency = find(cssSelector("span[class=unit]")).get.text
    val price = value + " " + currency

    SearchResult(ps, price, query)
  }
}