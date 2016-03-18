package org.mystic

import com.jme3.asset.AssetManager
import com.jme3.font.BitmapText
import com.jme3.scene.Node

class Hud(assetManager: AssetManager, guiNode: Node, width: Int, height: Int) {

  def reset = {
    lives = 1
    score = 0
  }

  def addPoint = score += 1

  def removeLife = lives -= 1

  def addLife = lives += 1

  def loadFont = {
    val guiFont = assetManager.loadFont("Interface/output-saucerbb-2048.fnt")
    livesText = new BitmapText(guiFont, false)
    livesText.setLocalTranslation(30, height - 30, 0)
    livesText.setSize(fontSize)
    livesText.setText("LIVES: " + lives)
    guiNode.attachChild(livesText)

    scoreText = new BitmapText(guiFont, true)
    scoreText.setLocalTranslation(width - 200, height - 30, 0)
    scoreText.setSize(fontSize)
    scoreText.setText("SCORE: " + score)
    guiNode.attachChild(scoreText)
  }

  def showBeatingHighScoreMessage = {
    // todo create something
  }

  val fontSize = 35
  var livesText: BitmapText = _
  var scoreText: BitmapText = _

  var lives = 1
  var score = 0


  def updateHUD = {
    scoreText.setText("SCORE: " + score)
    livesText.setText("LIVES: " + lives)
  }
}
