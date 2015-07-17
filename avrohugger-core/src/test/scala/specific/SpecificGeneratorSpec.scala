import avrohugger._
import specific._

import org.specs2._
import mutable._
import specification._

class SpecificGeneratorSpec extends mutable.Specification {

  "a SpecificGenerator" should {
    
    "correctly generate a case class definition that extends `SpecificRecordBase` in a package" in {
      val infile = new java.io.File("avrohugger-core/src/test/avro/mail.avpr")
      val gen = new SpecificGenerator
      val outDir = gen.defaultOutputDir + "specific/"
      gen.fromFile(infile, outDir)
      val source = scala.io.Source.fromFile(s"$outDir/example/proto/Message.scala").mkString
      println(source)
       source === 
"""/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package example.proto

case class Message(var to: String, var from: String, var body: String) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this("", "", "")
  def get(field: Int): AnyRef = {
    field match {
      case pos if pos == 0 => {
        to
      }.asInstanceOf[AnyRef]
      case pos if pos == 1 => {
        from
      }.asInstanceOf[AnyRef]
      case pos if pos == 2 => {
        body
      }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field: Int, value: Any): Unit = {
    field match {
      case pos if pos == 0 => this.to = {
        value match {
          case (value: org.apache.avro.util.Utf8) => value.toString
          case _ => value
        }
      }.asInstanceOf[String]
      case pos if pos == 1 => this.from = {
        value match {
          case (value: org.apache.avro.util.Utf8) => value.toString
          case _ => value
        }
      }.asInstanceOf[String]
      case pos if pos == 2 => this.body = {
        value match {
          case (value: org.apache.avro.util.Utf8) => value.toString
          case _ => value
        }
      }.asInstanceOf[String]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = Message.SCHEMA$
}

object Message {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Message\",\"namespace\":\"example.proto\",\"fields\":[{\"name\":\"to\",\"type\":\"string\"},{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"body\",\"type\":\"string\"}]}")
}"""
    }

    "correctly generate enums with SCHEMA$" in {
      val infile = new java.io.File("avrohugger-core/src/test/avro/enums.avsc")
      val gen = new SpecificGenerator
      val outDir = gen.defaultOutputDir + "specific/"
      gen.fromFile(infile, outDir)

      val source = scala.io.Source.fromFile(s"$outDir/example/Suit.scala").mkString
      source ====
        """
          |/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
          |package example
          |
          |object Suit extends Enumeration {
          |  type Suit = Value
          |  val SPADES, DIAMONDS, CLUBS, HEARTS = Value
          |  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"Suit\",\"namespace\":\"example\",\"symbols\":[\"SPADES\",\"DIAMONDS\",\"CLUBS\",\"HEARTS\"]}")
          |}
        """.stripMargin.trim
    }

    "correctly generate enums in AVDLs with `SpecificRecord`" in {
      val infile = new java.io.File("avrohugger-core/src/test/avro/enums.avdl")
      val gen = new SpecificGenerator
      val outDir = gen.defaultOutputDir + "specific/"
      gen.fromFile(infile, outDir)

      val sourceEnum = scala.io.Source.fromFile(s"$outDir/example/idl/Suit.scala").mkString
      sourceEnum ====
        """
          |/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
          |package example.idl
          |
          |object Suit extends Enumeration {
          |  type Suit = Value
          |  val SPADES, DIAMONDS, CLUBS, HEARTS = Value
          |  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"Suit\",\"namespace\":\"example.idl\",\"symbols\":[\"SPADES\",\"DIAMONDS\",\"CLUBS\",\"HEARTS\"]}")
          |}
        """.stripMargin.trim

      val sourceRecord = scala.io.Source.fromFile(s"$outDir/example/idl/Card.scala").mkString
      sourceRecord ====
      """
        |/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
        |package example.idl
        |
        |case class Card(var suit: Suit.Value, var number: Int) extends org.apache.avro.specific.SpecificRecordBase {
        |  def this() = this(null, 1)
        |  def get(field: Int): AnyRef = {
        |    field match {
        |      case pos if pos == 0 => {
        |        new org.apache.avro.generic.GenericData.EnumSymbol(Suit.SCHEMA$, suit.toString)
        |      }.asInstanceOf[AnyRef]
        |      case pos if pos == 1 => {
        |        number
        |      }.asInstanceOf[AnyRef]
        |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
        |    }
        |  }
        |  def put(field: Int, value: Any): Unit = {
        |    field match {
        |      case pos if pos == 0 => this.suit = {
        |        Suit.withName(value.toString)
        |      }.asInstanceOf[Suit.Value]
        |      case pos if pos == 1 => this.number = {
        |        value
        |      }.asInstanceOf[Int]
        |      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
        |    }
        |    ()
        |  }
        |  def getSchema: org.apache.avro.Schema = Card.SCHEMA$
        |}
        |
        |object Card {
        |  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Card\",\"namespace\":\"example.idl\",\"fields\":[{\"name\":\"suit\",\"type\":{\"type\":\"enum\",\"name\":\"Suit\",\"symbols\":[\"SPADES\",\"DIAMONDS\",\"CLUBS\",\"HEARTS\"]}},{\"name\":\"number\",\"type\":\"int\"}]}")
        |}
      """.stripMargin.trim
    }
  }

}
