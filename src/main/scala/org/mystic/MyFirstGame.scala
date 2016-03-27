package org.mystic

import com.jme3.app.SimpleApplication
import com.jme3.cursors.plugins.JmeCursor
import com.jme3.input.controls.{ActionListener, AnalogListener, KeyTrigger, MouseButtonTrigger}
import com.jme3.input.{KeyInput, MouseInput}
import com.jme3.material.Material
import com.jme3.material.RenderState.BlendMode
import com.jme3.math.Vector3f
import com.jme3.niftygui.NiftyJmeDisplay
import com.jme3.post.FilterPostProcessor
import com.jme3.post.filters.BloomFilter
import com.jme3.scene.{Node, Spatial}
import com.jme3.system.AppSettings
import com.jme3.texture.Texture2D
import com.jme3.ui.Picture
import org.mystic.Utils._
import org.mystic.controls._

import scala.collection.JavaConversions._
import scala.util.Random

object MyFirstGame extends SimpleApplication with ActionListener with AnalogListener {

  private var player: Spatial = _
  val Alive = "alive"
  val Radius = "radius"
  val DieTime: String = "dieTime"
  private val Left = "left"
  private val Right = "right"
  private val Up = "up"
  private val Down = "down"
  private val MouseClick = "mouseClick"

  private var bulletCooldown: Long = 0
  private var bulletNode: Node = _
  private var enemyNode: Node = _

  private var gameOver = false

  private var enemySpawnCooldown: Long = _
  private var enemySpawnChance: Float = 80

  private var blackHoleCooldown: Long = 0
  private var blackHoleNode: Node = _

  private var extraLifeNode: Node = _

  private var sound: SoundManager = _

  private var hud: Hud = _

  private var particleManager: ParticleManager = _

  private var spawnTime = 0L

  override def simpleInitApp(): Unit = {
    // create sounds manager
    sound = new SoundManager(assetManager)
    sound.startMusic
    // set up camera for 2D
    cam.setParallelProjection(true)
    cam.setLocation(new Vector3f(0, 0, 10.0f))
    getFlyByCamera().setEnabled(false)

    // turn off stats view
    //    setDisplayStatView(false)
    //    setDisplayFps(false)

    // add listener for keyboard
    inputManager.addMapping(Left, new KeyTrigger(KeyInput.KEY_LEFT))
    inputManager.addMapping(Right, new KeyTrigger(KeyInput.KEY_RIGHT))
    inputManager.addMapping(Up, new KeyTrigger(KeyInput.KEY_UP))
    inputManager.addMapping(Down, new KeyTrigger(KeyInput.KEY_DOWN))
    inputManager.addListener(this, Left)
    inputManager.addListener(this, Right)
    inputManager.addListener(this, Up)
    inputManager.addListener(this, Down)

    // add listener for mouse
    inputManager.addMapping(MouseClick, new MouseButtonTrigger(MouseInput.BUTTON_LEFT))
    inputManager.addListener(this, MouseClick)

    //        setup the bulletNode
    bulletNode = new Node("bullets")
    guiNode.attachChild(bulletNode)

    // set up the enemyNode
    enemyNode = new Node("enemies")
    guiNode.attachChild(enemyNode)

    blackHoleNode = new Node("blackholes")
    guiNode.attachChild(blackHoleNode)

    extraLifeNode = new Node("extralifes")
    guiNode.attachChild(extraLifeNode)

    spawnTime = System.currentTimeMillis()

    //create particle manager
    particleManager = new ParticleManager(guiNode, getSpatial("Laser"), getSpatial("Glow"))

    // add player
    player = getSpatial("Player")
    player.setUserData(Alive, true)
    player.move(settings.getWidth() / 2, settings.getHeight() / 2, 0)
    guiNode.attachChild(player)
    player.addControl(new PlayerControl(settings.getWidth(), settings.getHeight(), particleManager))

    inputManager.setMouseCursor(assetManager.loadAsset("Textures/Pointer.ico").asInstanceOf[JmeCursor])

    hud = new Hud(assetManager, guiNode, settings.getWidth(), settings.getHeight())
    hud.loadFont
    hud.reset


    // add bloom filter
    val fpp = new FilterPostProcessor(assetManager)
    val bloom = new BloomFilter()
    fpp.addFilter(bloom)
    guiViewPort.addProcessor(fpp)
    guiViewPort.setClearColor(true)

    // add hud as nifty display
    val niftyJmeDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort)
    val nifty = niftyJmeDisplay.getNifty
    nifty.fromXml("Interface/hud.xml", "hud")
    guiViewPort.addProcessor(niftyJmeDisplay)


  }

  var screenHeight = 0
  var screeWidth = 0


  // entry point of the game
  def main(args: Array[String]): Unit = {
    val mySettings = new AppSettings(true)
    mySettings.setTitle("Neon shooter")
    mySettings.setSettingsDialogImage("Interface/splashscreen.png")
    screenHeight = mySettings.getHeight
    screeWidth = mySettings.getWidth
    //    val device = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice
    //    val modes = device.getDisplayModes
    //    val widths = new HashSet[Int]
    //    val heights = new HashSet[Int]
    //    val refreshRate = new HashSet[Int]
    //    val bitDepths = new HashSet[Int]
    //    modes.foreach(x => {
    //      widths + x.getWidth
    //      heights + x.getHeight
    //      refreshRate + x.getRefreshRate
    //      bitDepths + x.getBitDepth
    //      println(s"${x.getWidth} ${x.getHeight} ${x.getRefreshRate} ${x.getBitDepth}")
    //    })
    //    val lastMode = modes.length - 1
    //    mySettings.setResolution(modes(lastMode).getWidth, modes(lastMode).getHeight)
    //    mySettings.setFrequency(modes(lastMode).getRefreshRate)
    //    mySettings.setDepthBits(modes(lastMode).getBitDepth)
    //    mySettings.setFullscreen(device.isFullScreenSupported)
    //    MyFirstGame.setShowSettings(false)
    MyFirstGame.setSettings(mySettings)
    MyFirstGame.start()
  }

  private def getSpatial(name: String): Spatial = {
    val node = new Node(name)
    // load picture
    val pic = new Picture(name)
    val tex: Texture2D = assetManager.loadTexture(s"Textures/${name}.png").asInstanceOf[Texture2D]
    pic.setTexture(assetManager, tex, true)

    // adjust picture
    val width = tex.getImage.getWidth
    val height = tex.getImage.getHeight
    pic.setWidth(width)
    pic.setHeight(height)
    // for rotation purposes
    pic.move(-width / 2f, -height / 2f, 0)

    // add a material to the picture
    val pictureMaterial = new Material(assetManager, "Common/MatDefs/Gui/Gui.j3md")
    pictureMaterial.getAdditionalRenderState.setBlendMode(BlendMode.AlphaAdditive)
    node.setMaterial(pictureMaterial)

    // set the radius of the spatial
    // (using width only as a simple approximation)
    node.setUserData(Radius, (width / 2.0f))

    // attach the picture to the node and return it
    node.attachChild(pic)
    node
  }

  override def onAction(name: String, isPressed: Boolean, tpf: Float): Unit = {
    checkSpatialIsAlive(player, () => {
      name match {
        // TODO change it to getControl by class
        case Up => player.getControl(0).asInstanceOf[PlayerControl].up = isPressed
        case Down => player.getControl(0).asInstanceOf[PlayerControl].down = isPressed
        case Left => player.getControl(0).asInstanceOf[PlayerControl].left = isPressed
        case Right => player.getControl(0).asInstanceOf[PlayerControl].right = isPressed
        case _ =>
      }
    }, () => {})
  }

  def getAimDirection: Vector3f = {
    val mouse = inputManager.getCursorPosition
    val playerPosition = player.getLocalTranslation
    val difference = new Vector3f(mouse.x - playerPosition.x, mouse.y - playerPosition.y, 0)
    difference.normalizeLocal
  }

  def launchTwoBullets = {
    if (System.currentTimeMillis() - bulletCooldown > 83f) {
      sound.shoot
      bulletCooldown = System.currentTimeMillis
      val aim = getAimDirection
      val offset = new Vector3f(aim.y / 3, -aim.x / 3, 0)

      //                    init bullet 1
      val bullet = getSpatial("Bullet")
      var finalOffset = aim.add(offset).mult(30)
      var trans = player.getLocalTranslation().add(finalOffset)
      bullet.setLocalTranslation(trans)
      bullet.addControl(new BulletControl(aim, settings.getWidth(), settings.getHeight(), particleManager))
      bulletNode.attachChild(bullet)

      //                    init bullet 2
      val bullet2 = getSpatial("Bullet")
      finalOffset = aim.add(offset.negate()).mult(30)
      trans = player.getLocalTranslation().add(finalOffset)
      bullet2.setLocalTranslation(trans)
      bullet2.addControl(new BulletControl(aim, settings.getWidth(), settings.getHeight(), particleManager))
      bulletNode.attachChild(bullet2)
    }
  }

  override def onAnalog(name: String, value: Float, tpf: Float): Unit = {
    checkSpatialIsAlive(player, () => {
      name match {
        case MouseClick => launchTwoBullets
        case _ =>
      }
    }, () => {})
  }

  def getSpawnPosition: Vector3f = {
    var pos: Vector3f = new Vector3f(new Random().nextInt(settings.getWidth()), new Random().nextInt(settings.getHeight()), 0)
    do {
      pos = new Vector3f(new Random().nextInt(settings.getWidth()), new Random().nextInt(settings.getHeight()), 0)
    } while (pos.distanceSquared(player.getLocalTranslation()) < 8000)
    pos
  }


  def createSeeker = {
    val seeker = getSpatial("Seeker")
    seeker.setLocalTranslation(getSpawnPosition)
    seeker.addControl(new SeekerControl(player))
    seeker.setUserData("active", false)
    enemyNode.attachChild(seeker)
  }


  def createWanderer = {
    val wanderer = getSpatial("Wanderer")
    wanderer.setLocalTranslation(getSpawnPosition)
    wanderer.addControl(new WandererControl(settings.getWidth, settings.getHeight))
    wanderer.setUserData("active", false)
    enemyNode.attachChild(wanderer)
  }

  def createBlackHole = {
    val blackHole = getSpatial("BlackHole")
    blackHole.setLocalTranslation(getSpawnPosition)
    val control: BlackHoleControl = new BlackHoleControl()
    control.setParticleManager(particleManager)
    blackHole.addControl(control)
    blackHole.setUserData("active", false)
    blackHoleNode.attachChild(blackHole)
  }

  def spawnEnemies = {
    if (System.currentTimeMillis() - enemySpawnCooldown >= 17) {
      enemySpawnCooldown = System.currentTimeMillis
      //      sound.spawn

      if (enemyNode.getQuantity < 50) {
        if (new Random().nextInt(enemySpawnChance.toInt) == 0) {
          createSeeker
        }
        if (new Random().nextInt(enemySpawnChance.toInt) == 0) {
          createWanderer
        }
      }
      //increase Spawn Time
      if (enemySpawnChance >= 1.1f) {
        enemySpawnChance -= 0.005f
      }
    }
  }

  def spawnBlackHoles = {
    if (blackHoleNode.getQuantity < 10 && System.currentTimeMillis - blackHoleCooldown > 5000 && new Random().nextInt(500) == 1) {
      blackHoleCooldown = System.currentTimeMillis
      createBlackHole
    }
  }

  def createExtraLife = {
    val extraLife = getSpatial("ExtraLife")
    extraLife.setLocalTranslation(getSpawnPosition)
    extraLife.addControl(new ExtraLifeControl())
    extraLife.setUserData("active", false)
    extraLifeNode.attachChild(extraLife)
  }

  def spawnExtraLife = {
    if (extraLifeNode.getQuantity < 2 && new Random().nextInt(1000) == 1) {
      createExtraLife
    }
  }


  def checkCollision(a: Spatial, b: Spatial): Boolean = {
    val distance = a.getLocalTranslation().distance(b.getLocalTranslation())
    val maxDistance = a.getUserData[Float](Radius) + b.getUserData[Float](Radius)
    distance <= maxDistance
  }

  def killPlayer = {
    particleManager.playerExplosion(player.getLocalTranslation())
    player.removeFromParent()
    player.getControl(0).asInstanceOf[PlayerControl].reset()
    player.setUserData(Alive, false)
    player.setUserData(DieTime, System.currentTimeMillis)
    enemyNode.detachAllChildren
    blackHoleNode.detachAllChildren
    extraLifeNode.detachAllChildren
  }

  def handleCollisions = {
    // should the player die?
    enemyNode.getChildren.foreach(enemy => {
      checkSpatialIsAlive(enemy, () => {
        if (checkCollision(player, enemy))
          killPlayer
      }, () => {})
      bulletNode.getChildren.foreach(bullet => {
        if (checkCollision(enemy, bullet)) {
          particleManager.enemyExplosion(enemy.getLocalTranslation())
          sound.explosion
          enemyNode.detachChild(enemy)
          bulletNode.detachChild(bullet)
          hud.addPoint
          hud.addMultiplier
        }
      })
    })
    blackHoleNode.getChildren.foreach(blackHole => {
      if (checkCollision(blackHole, player)) {
        killPlayer
      }
      enemyNode.getChildren.foreach(enemy => {
        if (checkCollision(blackHole, enemy)) {
          sound.explosion
          enemyNode.detachChild(enemy)
          particleManager.enemyExplosion(enemy.getLocalTranslation())
        }
      })
      bulletNode.getChildren.foreach(bullet => {
        if (checkCollision(blackHole, bullet)) {
          val control: BlackHoleControl = blackHole.getControl(0).asInstanceOf[BlackHoleControl]
          control.takeShot
          particleManager.blackHoleExplosion(blackHole.getLocalTranslation, spawnTime)
          bulletNode.detachChild(bullet)
          if (control.isDead) {
            hud.addPointForBlackHole
            hud.addMultiplier
            blackHoleNode.detachChild(blackHole)
            sound.explosion
          }

        }
      })
    })
    extraLifeNode.getChildren.foreach(extra => {
      if (checkCollision(extra, player)) {
        sound.extraLife
        extraLifeNode.detachChild(extra)
        hud.addLife
      }
    })
  }

  def isNear(a: Spatial, b: Spatial): Boolean = {
    val v1 = a.getLocalTranslation
    val v2 = b.getLocalTranslation
    // todo may be still not the best solution
    v1.distanceSquared(v2) <= Math.min(screenHeight, screeWidth) * Math.min(screenHeight, screeWidth)
  }

  def applyGravity(blackHole: Spatial, target: Spatial, tpf: Float) = {
    val gravity = blackHole.getLocalTranslation.subtract(target.getLocalTranslation)
    val distance = gravity.length
    gravity.divideLocal(distance * distance)

    target.getName match {
      case "Player" => target.getControl(0).asInstanceOf[PlayerControl].applyGravity(gravity.multLocal(500f))
      case "Bullet" => target.getControl(0).asInstanceOf[BulletControl].applyGravity(gravity.multLocal(-0.99f))
      case "Seeker" => target.getControl(0).asInstanceOf[SeekerControl].applyGravity(gravity.multLocal(60000f))
      case "Wanderer" => target.getControl(0).asInstanceOf[WandererControl].applyGravity(gravity.multLocal(60000f))
      case "Laser" => target.getControl(0).asInstanceOf[ParticleControl].applyGravity(gravity.mult(15000f), distance)
      case "Glow" => target.getControl(0).asInstanceOf[ParticleControl].applyGravity(gravity.mult(15000f), distance)
    }
  }

  def createGravity(tpf: Float): Unit = {
    blackHoleNode.getChildren.foreach(blackHole => {
      if (isNear(blackHole, player)) {
        applyGravity(blackHole, player, tpf)
      }
      bulletNode.getChildren.foreach(bullet => {
        if (isNear(blackHole, bullet)) {
          applyGravity(blackHole, bullet, tpf)
        }
      })
      particleManager.getParticleNode.getChildren.filter(p => p.getUserData("affectedByGravity").asInstanceOf[Boolean]).foreach(particle => {
        applyGravity(blackHole, particle, tpf)
      })
      enemyNode.getChildren.foreach(enemy => {
        if (isNear(blackHole, enemy)) {
          applyGravity(blackHole, enemy, tpf)
        }
      })
    })
  }

  override def simpleUpdate(tpf: Float): Unit = {
    checkSpatialIsAlive(player, () => {
      spawnEnemies
      spawnBlackHoles
      handleCollisions
      spawnExtraLife
      createGravity(tpf)
      hud.updateHUD
    }, () => {
      if (System.currentTimeMillis() - player.getUserData(DieTime).asInstanceOf[Long] > 4000f && !gameOver) {
        if (hud.lives > 0) {
          hud.removeLife
          hud.multiplier = 1
          player.setLocalTranslation(settings.getWidth() / 2, settings.getHeight() / 2, 0)
          guiNode.attachChild(player)
          player.setUserData(Alive, true)
        } else {
          hud.endGame
          gameOver = true
        }
      }
    })
  }
}
