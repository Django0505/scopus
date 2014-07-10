package za.co.monadic.scopus

import scala.util.Try

/**
 *  Decoder for short data types
 */
trait DecodeFloat {

  /**
   * Decode an audio packet to an array of Floats
   * @param compressedAudio The incoming audio packet
   * @return A Try containing the decoded audio packet in Float format
   */
  def apply(compressedAudio: Array[Byte]): Try[Array[Float]]

  /**
   * Decode an erased (i.e. not received) audio packet. Note you need to specify
   * how many samples you think you have lost so the decoder can attempt to
   * deal with the erasure appropriately.
   * @return A Try containing decompressed audio in Float format
   */
  def apply(count: Int): Try[Array[Float]]
}

/**
 * Decoder for float data types
 */
trait DecodeShort {

  /**
   * Decode an audio packet to an array of Shorts
   * @param compressedAudio The incoming audio packet
   * @return A Try containing decoded audio in Short format
   */
  def apply(compressedAudio: Array[Byte]): Try[Array[Short]]

  /**
   * Decode an erased (i.e. not received) audio packet. Note you need to specify
   * how many samples you think you have lost so the decoder can attempt to
   * deal with the erasure appropriately.
   * @return A Try containing decompressed audio in short format
   */
  def apply(count: Int): Try[Array[Short]]
}

/**
 * Encoder trait
 */
trait Encoder {
  /**
   * Encode a block of raw audio  in integer format using the configured encoder
   * @param audio Audio data arranged as a contiguous block interleaved array of short integers
   * @return An array containing the compressed audio or the exception in case of a failure
   */
  def apply(audio: Array[Short]): Try[Array[Byte]]


  /**
   * Encode a block of raw audio  in float format using the configured encoder
   * @param audio Audio data arranged as a contiguous block interleaved array of floats
   * @return An array containing the compressed audio or the exception in case of a failure
   */
  def apply(audio: Array[Float]): Try[Array[Byte]]
}