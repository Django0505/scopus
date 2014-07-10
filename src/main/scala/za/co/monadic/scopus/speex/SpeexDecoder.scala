package za.co.monadic.scopus.speex

import za.co.monadic.scopus.speex.Speex._
import za.co.monadic.scopus.{SampleFrequency, DecodeFloat, DecodeShort}

import scala.util.{Success, Failure, Try}

sealed trait SpeexBase {

  val sampleFreq: SampleFrequency
  val enhance: Boolean

  val en = if(enhance) 1 else 0
  val decoder = decoder_create(getMode(sampleFreq), en)
  if (decoder <= 0) throw new RuntimeException("Failed to create Speex decoder state")
  var clean = false

  def cleanup() = {
    if (!clean) {
      decoder_destroy(decoder)
    }
  }

}

/**
 *
 */
class SpeexDecoder(val sampleFreq: SampleFrequency, val enhance: Boolean) extends SpeexBase with DecodeShort {

  val decodedBuf = new Array[Short](1024)
  /**
   * Decode an audio packet to an array of Shorts
   * @param compressedAudio The incoming audio packet
   * @return A Try containing decoded audio in Short format
   */
  override def apply(compressedAudio: Array[Byte]): Try[Array[Short]] = {
    val len = decode_short(decoder, compressedAudio, compressedAudio.length, decodedBuf, 1024)
    if (len < 0)
      Failure(new RuntimeException(s"speex_decode() failed}"))
    else
      Success(decodedBuf.slice(0, len))
  }

  /**
   * Decode an erased (i.e. not received) audio packet. Note you need to specify
   * how many samples you think you have lost so the decoder can attempt to
   * deal with the erasure appropriately.
   * @return A Try containing decompressed audio in Float format
   */
  override def apply(count: Int): Try[Array[Short]] = {
    val len = decode_short(decoder,null, 0, decodedBuf, 1024)
    if (len < 0)
      Failure(new RuntimeException(s"speex_decode() failed}"))
    else
      Success(decodedBuf.slice(0, len))
  }
}

object SpeexDecoder {
  def apply(sampleFreq: SampleFrequency, enhance: Boolean) = new SpeexDecoder(sampleFreq,enhance)
}

class SpeexDecoderFloat(val sampleFreq: SampleFrequency, val enhance: Boolean) extends SpeexBase with DecodeFloat {

  val decodedBuf = new Array[Float](1024)
  /**
   * Decode an audio packet to an array of Floats
   * @param compressedAudio The incoming audio packet
   * @return A Try containing the decoded audio packet in Float format
   */
  override def apply(compressedAudio: Array[Byte]): Try[Array[Float]] = {
    val len = decode_float(decoder, compressedAudio, compressedAudio.length, decodedBuf, 1024)
    if (len < 0)
      Failure(new RuntimeException(s"speex_decode() failed}"))
    else
      Success(decodedBuf.slice(0, len))
  }

  /**
   * Decode an erased (i.e. not received) audio packet. Note you need to specify
   * how many samples you think you have lost so the decoder can attempt to
   * deal with the erasure appropriately.
   * @return A Try containing decompressed audio in Float format
   */
  override def apply(count: Int): Try[Array[Float]] = {
    val len = decode_float(decoder,null, 0, decodedBuf, 1024)
    if (len < 0)
      Failure(new RuntimeException(s"speex_decode()short failed}"))
    else
      Success(decodedBuf.slice(0, len))
  }
}

object SpeexDecoderFloat {
  def apply(sampleFreq: SampleFrequency, enhance: Boolean) = new SpeexDecoderFloat(sampleFreq,enhance)
}
