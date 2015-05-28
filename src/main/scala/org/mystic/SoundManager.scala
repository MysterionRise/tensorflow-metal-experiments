package org.mystic

import com.jme3.asset.AssetManager
import com.jme3.audio.AudioNode

import scala.util.Random

class SoundManager(assetManager: AssetManager) {

  private val shots = new Array[AudioNode](4)
  private val explosions = new Array[AudioNode](8)
  private val spawns = new Array[AudioNode](8)
  private var music: AudioNode = _
  private var extraLifeSound: AudioNode = _
  loadSounds


  def loadSounds = {
    music = new AudioNode(assetManager, "Sounds/Music.ogg")
    music.setPositional(false)
    music.setReverbEnabled(false)
    music.setLooping(true)

    extraLifeSound = new AudioNode(assetManager, "Sounds/extralife.wav")
    extraLifeSound.setPositional(false)
    extraLifeSound.setReverbEnabled(false)
    extraLifeSound.setLooping(false)

    for (i <- 0 until shots.length) {
      val node: AudioNode = new AudioNode(assetManager, s"Sounds/shoot-0${i + 1}.wav")
      node.setPositional(false)
      node.setReverbEnabled(false)
      node.setLooping(false)
      shots(i) = node
    }

    for (i <- 0 until explosions.length) {
      val node: AudioNode = new AudioNode(assetManager, s"Sounds/explosion-0${i + 1}.wav")
      node.setPositional(false)
      node.setReverbEnabled(false)
      node.setLooping(false)
      explosions(i) = node
    }

    for (i <- 0 until spawns.length) {
      val node: AudioNode = new AudioNode(assetManager, s"Sounds/spawn-0${i + 1}.wav")
      node.setPositional(false)
      node.setReverbEnabled(false)
      node.setLooping(false)
      spawns(i) = node
    }
  }

  def startMusic = music.play

  def shoot = shots(new Random().nextInt(shots.length)).playInstance()

  def explosion = explosions(new Random().nextInt(explosions.length)).playInstance()

  def spawn = spawns(new Random().nextInt(spawns.length)).playInstance()

  def extraLife = extraLifeSound.play

}
