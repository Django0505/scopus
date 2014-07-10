package za.co.monadic.scopus

import za.co.monadic.scopus.opus.{OpusEncoder, OpusDecoder}
import za.co.monadic.scopus.speex.{SpeexDecoder, SpeexEncoder, Speex}

import scala.util.Try

/**
 *
 */
object Stub extends App {

  val enc = OpusEncoder(Sf8000, 1, Audio)
  enc.setUseDtx(1)
  // Transmit special short packets if silence is detected

  val dec = OpusDecoder(Sf8000, 1)

  val coded: Try[Array[Byte]] = enc(new Array[Short](160))
  // Transmit

  // On receive end
  val decoded: Try[Array[Short]] = dec(coded.get)

  println(Speex.get_version_string())

  val senc = SpeexEncoder(Sf8000)
  senc.cleanup()

  val sdec = SpeexDecoder(Sf8000,true)
  sdec.cleanup()

  // Send decoded packet off

  println("Done....")
}
