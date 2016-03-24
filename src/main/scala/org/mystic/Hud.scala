package org.mystic

import java.io.{PrintWriter, BufferedReader, File, FileReader}

import com.jme3.asset.AssetManager
import com.jme3.font.BitmapText
import com.jme3.scene.Node

class Hud(assetManager: AssetManager, guiNode: Node, width: Int, height: Int) {

  def reset = {
    lives = 1
    score = 0
    multiplier = 1
  }

  def addPoint = score += multiplier

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
    scoreText.setLocalTranslation(width - 300, height - 30, 0)
    scoreText.setSize(fontSize)
    scoreText.setText("SCORE: " + score)
    guiNode.attachChild(scoreText)

    multiplierText = new BitmapText(guiFont, true)
    multiplierText.setLocalTranslation(width - 300, height - 70, 0)
    multiplierText.setSize(fontSize)
    multiplierText.setText("MULTIPLIER: " + multiplier)
    guiNode.attachChild(multiplierText)
  }

  // todo make it more proper
  def loadHighScore: Int = {
    try {
      val reader = new FileReader(new File("highscore"))
      val buff = new BufferedReader(reader)
      return Integer.valueOf(buff.readLine())
    } catch {
      case e: Exception => e.printStackTrace()
    }
    return 0
  }

  // todo fix it
  def saveHighScore(score: Int) = {
    try {
      val writer = new PrintWriter(new File("highscore"))
      writer.write(String.valueOf(score))
      writer.close()
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def endGame = {
    guiNode.detachAllChildren()
    val gameOverNode = new Node()
    gameOverNode.setLocalTranslation(width / 2 - 200, height / 2 + 100, 0)
    guiNode.attachChild(gameOverNode)

    val guiFont = assetManager.loadFont("Interface/output-saucerbb-2048.fnt")
    val highScore = loadHighScore
    if (score > highScore) {
      val highScore = new BitmapText(guiFont, false)
      highScore.setLocalTranslation(-60, -60, 0)
      highScore.setSize(fontSize * 2)
      highScore.setText("Your score: " + score)
      gameOverNode.attachChild(highScore)
      saveHighScore(score)
    }

    val endGameResponse = new BitmapText(guiFont, false)
    endGameResponse.setLocalTranslation(0, 0, 0)
    endGameResponse.setSize(fontSize * 2)
    endGameResponse.setText("GAME OVER")
    gameOverNode.attachChild(endGameResponse)
  }

  val fontSize = 35
  var livesText: BitmapText = _
  var scoreText: BitmapText = _
  var multiplierText: BitmapText = _

  var lives = 1
  var score = 0
  var multiplier = 1
  var multiplierTimStamp = 0L

  def addMultiplier = {
    multiplier += 1
    multiplierTimStamp = System.currentTimeMillis()
  }


  def updateHUD = {
    if (System.currentTimeMillis() - multiplierTimStamp > 1000 && multiplier > 1) {
      multiplier = multiplier / 2
      if (multiplier == 0) {
        multiplier = 1
      }
      multiplierTimStamp = System.currentTimeMillis()
    }
    scoreText.setText("SCORE: " + score)
    livesText.setText("LIVES: " + lives)
    multiplierText.setText("MULTIPLIER: " + multiplier)
  }
}
