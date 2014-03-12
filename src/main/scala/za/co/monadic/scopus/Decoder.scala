package za.co.monadic.scopus

import za.co.monadic.scopus.Opus._

class Decoder(Fs:SampleFrequency, channels:Int) {

  val bufferLen: Int = math.round(0.120f*Fs()*channels)
  var fec = 0
  val error = Array[Int](0)
  val decoder = decoder_create(Fs(), channels, error)
  if (error(0) != OPUS_OK) {
    throw new RuntimeException(s"Failed to create the Opus encoder: ${error_string(error(0))}")
  }

  val decodedShortBuf = new Array[Short](2880*channels) // 60ms of audio at 48kHz
  val decodedFloatBuf = new Array[Float](2880*channels) // 60ms of audio at 48kHz
  var clean = false

  /**
   * Decode an audio packet to an array of Shorts
   * @param compressedAudio The incoming audio packet
   * @return Decoded audio packet
   */
  def decode(compressedAudio: Array[Byte] ): Array[Short] = {
    val len = decode_short(decoder,compressedAudio,compressedAudio.length,decodedShortBuf,bufferLen, fec)
    if (len < 0) throw new RuntimeException(s"opus_decode() failed: ${error_string(len)}")
    decodedShortBuf.slice(0,len)
  }

  /**
   * Decode an erased (i.e. not received) audio packet
   * @return The decompressed audio for this packet
   */
  def decode(): Array[Short] = {
    val len =  decode_short(decoder,Array[Byte](0),0,decodedShortBuf,bufferLen, fec)
    if (len < 0) throw new RuntimeException(s"opus_decode() failed: ${error_string(len)}")
    decodedShortBuf.slice(0,len)
  }

  /**
   * Decode an audio packet to an array of Floats
   * @param compressedAudio The incoming audio packet
   * @return Decoded audio packet
   */
  def decodeFloat(compressedAudio: Array[Byte] ): Array[Float] = {
    val len = decode_float(decoder,compressedAudio,compressedAudio.length,decodedFloatBuf, bufferLen, fec)
    if (len < 0) throw new RuntimeException(s"opus_decode_float() failed: ${error_string(len)}")
    decodedFloatBuf.slice(0,len)
  }

  /**
   * Decode an erased (i.e. not received) audio packet
   * @return The decompressed audio for this packet
   */
  def decodeFloat(): Array[Float] = {
    val len = decode_float(decoder, Array[Byte](0),0,decodedFloatBuf, bufferLen, fec)
    if (len < 0) throw new RuntimeException(s"opus_decode_float() failed: ${error_string(len)}")
    decodedFloatBuf.slice(0,len)
  }

  /**
   * Release all pointers allocated for the decoder. Make every attempt to call this
   * when you are done with the encoder as finalise() is what it is in the JVM
   */
  def cleanup() : Unit = {
    if (!clean) {
      decoder_destroy(decoder)
      clean = true
    }
  }

  override def finalize() = cleanup()

  private def getter(command: Int) : Int = {
    assert(command %2 == 1) // Getter commands are all odd
    val result = Array[Int](0)
    val err: Int = decoder_get_ctl(decoder,command,result)
    if (err != OPUS_OK) throw new RuntimeException(s"opus_decoder_ctl failed for command $command: ${error_string(err)}")
    result(0)
  }

  private def setter(command: Integer, parameter: Integer): Unit = {
    assert(command %2 == 0) // Setter commands are even
    val err = decoder_set_ctl(decoder, command, parameter)
    if (err != OPUS_OK) throw new RuntimeException(s"opus_decoder_ctl setter failed for command $command: ${error_string(err)}")
  }

  def reset =  decoder_set_ctl(decoder, OPUS_RESET_STATE,0)

  def getSampleRate = getter(OPUS_GET_SAMPLE_RATE_REQUEST)
  def getLookAhead = getter(OPUS_GET_LOOKAHEAD_REQUEST)
  def getBandwidth = getter(OPUS_GET_BANDWIDTH_REQUEST)
  def getPitch = getter(OPUS_GET_PITCH_REQUEST)
  def getGain = getter(OPUS_GET_GAIN_REQUEST)
  def getLastPacketDuration = getter(OPUS_GET_LAST_PACKET_DURATION_REQUEST)

  def setGain(gain: Int) = setter(OPUS_SET_GAIN_REQUEST,gain)

  /**
   * Custom setter for the FEC mode in the decoder
   * @param useFec If true, employ error correction if it is available in the packet
   */
  def setFec(useFec: Boolean) = {
    fec = if (useFec) 1 else 0
  }

  /**
   * Returns the current FEC decoding status.
   * @return  True if FEC is being decoded
   */
  def getFec(useFec: Boolean) = {
    fec == 1
  }
}

object Decoder {
  def apply(Fs:SampleFrequency, channels:Int) = new Decoder(Fs,channels)
}
