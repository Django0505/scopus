package za.co.monadic.scopus.speex

import za.co.monadic.scopus._
import scala.util.{Failure, Success, Try}
import Speex._

/**
 *
 */
class SpeexEncoder(sampleFreq: SampleFrequency) extends Encoder {

  val bufferSize = 8192
  val decodePtr = new Array[Byte](bufferSize)

  val state: Long = encoder_create(getMode(sampleFreq))
  if (state <= 0) throw new RuntimeException("Failed to construct a Speex encoder")
  var clean = false

  def getDetail = s"Speex encoder with sf= ${sampleFreq()}"

  def reset() = encoder_ctl(state,SPEEX_RESET_STATE,0)

  /**
   * @return The sample rate for this codec's instance
   */
  override def getSampleRate: Int = encoder_ctl(state,SPEEX_GET_SAMPLING_RATE,0)

  /**
   * Encode a block of raw audio  in integer format using the configured encoder
   * @param audio Audio data arranged as a contiguous block interleaved array of short integers
   * @return An array containing the compressed audio or the exception in case of a failure
   */
  override def apply(audio: Array[Short]): Try[Array[Byte]] = {
    val len: Int = encode_short(state, audio, audio.length, decodePtr, bufferSize)
    if (len < 0)
      Failure(new IllegalArgumentException(s"speex_encode() failed"))
    else
      Success(decodePtr.slice(0, len))
  }

  /**
   * Encode a block of raw audio  in float format using the configured encoder
   * @param audio Audio data arranged as a contiguous block interleaved array of floats
   * @return An array containing the compressed audio or the exception in case of a failure
   */
  override def apply(audio: Array[Float]): Try[Array[Byte]] = {
    val len: Int = encode_float(state, audio, audio.length, decodePtr, bufferSize)
    if (len < 0)
      Failure(new IllegalArgumentException(s"speex_encode() failed"))
    else
      Success(decodePtr.slice(0, len))
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

