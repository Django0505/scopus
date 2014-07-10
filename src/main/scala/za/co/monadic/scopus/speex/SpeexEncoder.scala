package za.co.monadic.scopus.speex

import za.co.monadic.scopus._
import scala.util.{Success, Try}
import Speex._

/**
 *
 */
class SpeexEncoder(sampleFreq: SampleFrequency) extends Encoder {

  // Speex can do other frequencies, but these are the best supported ones.
  private val mode = sampleFreq match {
    case Sf8000  => SPEEX_MODEID_NB
    case Sf16000 => SPEEX_MODEID_WB
    case Sf32000 => SPEEX_MODEID_UWB
    case s:SampleFrequency => throw new RuntimeException(s"Invalid sampling frequency ($s) for the Speex codec")
  }

  val state = encoder_create(mode)
  if (state <= 0) throw new RuntimeException("Failed to construct a Speex encoder")
  var clean = false

  /**
   * Encode a block of raw audio  in integer format using the configured encoder
   * @param audio Audio data arranged as a contiguous block interleaved array of short integers
   * @return An array containing the compressed audio or the exception in case of a failure
   */
  override def apply(audio: Array[Short]): Try[Array[Byte]] = {

    Success(Array[Byte](0))
  }

  /**
   * Encode a block of raw audio  in float format using the configured encoder
   * @param audio Audio data arranged as a contiguous block interleaved array of floats
   * @return An array containing the compressed audio or the exception in case of a failure
   */
  override def apply(audio: Array[Float]): Try[Array[Byte]] = {
    Success(Array[Byte](0))
  }

  /**
   * Release all pointers allocated for the encoder. Make every attempt to call this
   * when you are done with the encoder as finalise() is what it is in the JVM
   */
  override def cleanup(): Unit = {
    if (!clean) {
      encoder_destroy(state)
      clean = true
    }
  }
}

object SpeexEncoder {
  def apply(sampleFreq: SampleFrequency) = new SpeexEncoder(sampleFreq)
}

