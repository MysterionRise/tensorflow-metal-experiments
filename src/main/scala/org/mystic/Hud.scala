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

  /**
    * public void endGame() {
    // init gameOverNode
    gameOverNode = new Node();
    gameOverNode.setLocalTranslation(screenWidth/2 - 180, screenHeight/2 + 100,0);
    guiNode.attachChild(gameOverNode);

    // check highscore
    int highscore = loadHighscore();
    if (score > highscore) {saveHighscore();}

    // init and display text
    BitmapText gameOverText = new BitmapText(guiFont, false);
    gameOverText.setLocalTranslation(0,0,0);
    gameOverText.setSize(fontSize);
    gameOverText.setText("Game Over");
    gameOverNode.attachChild(gameOverText);

    BitmapText yourScoreText = new BitmapText(guiFont, false);
    yourScoreText.setLocalTranslation(0,-50,0);
    yourScoreText.setSize(fontSize);
    yourScoreText.setText("Your Score: "+score);
    gameOverNode.attachChild(yourScoreText);

    BitmapText highscoreText = new BitmapText(guiFont, false);
    highscoreText.setLocalTranslation(0,-100,0);
    highscoreText.setSize(fontSize);
    highscoreText.setText("Highscore: "+highscore);
    gameOverNode.attachChild(highscoreText);
}
    * @return
    */

  def endGame = {
    guiNode.detachAllChildren()
    val gameOverNode = new Node()
    gameOverNode.setLocalTranslation(width / 2 - 180, height / 2 + 100, 0)
    guiNode.attachChild(gameOverNode)
//    val highscore = loadHighscore()
//    if (score > highscore) saveHighscore()

    val guiFont = assetManager.loadFont("Interface/output-saucerbb-2048.fnt")
    endGameResponse = new BitmapText(guiFont, false)
    endGameResponse.setLocalTranslation(0, 0, 0)
    endGameResponse.setSize(fontSize * 2)
    endGameResponse.setText("GAME OVER")
    gameOverNode.attachChild(endGameResponse)
  }

  val fontSize = 35
  var livesText: BitmapText = _
  var scoreText: BitmapText = _
  var endGameResponse: BitmapText = _

  var lives = 1
  var score = 0


  def updateHUD = {
    scoreText.setText("SCORE: " + score)
    livesText.setText("LIVES: " + lives)
  }
}
