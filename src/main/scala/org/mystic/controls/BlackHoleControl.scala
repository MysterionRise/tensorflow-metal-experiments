package org.mystic.controls

import com.jme3.math.{FastMath, ColorRGBA}
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.Node
import com.jme3.scene.control.AbstractControl
import com.jme3.ui.Picture
import org.mystic.{ParticleManager, Utils, MyFirstGame}
import org.mystic.Utils._

import scala.util.Random

class BlackHoleControl extends AbstractControl {

  private val spawnTime = System.currentTimeMillis
  private var hitpoints = 50
  private val random = new Random()
  private var lastSprayTime = 0L

  private var particleManager: ParticleManager = _

  def setParticleManager(particleManager: ParticleManager) = this.particleManager = particleManager

  private var sprayAngle: Float = 0.0f

  override def controlUpdate(tpf: Float): Unit = {
    checkSpatialIsAlive(spatial,
      () => {
        val sprayDif = System.currentTimeMillis() - lastSprayTime
        if (System.currentTimeMillis() % 2 == 0 && sprayDif > 20) {
          lastSprayTime = System.currentTimeMillis()

          val sprayVel = Utils.getVectorFromAngle(sprayAngle).mult(random.nextFloat() * 3 + 6)
          val randVec = Utils.getVectorFromAngle(random.nextFloat() * FastMath.PI * 2)
          randVec.multLocal(4 + random.nextFloat() * 4)
          val position = spatial.getLocalTranslation().add(sprayVel.mult(2f)).addLocal(randVec)

          particleManager.sprayParticle(position, sprayVel.mult(30f))
        }
        sprayAngle -= FastMath.PI * tpf / 10f
      },
      () => {
        val diff = System.currentTimeMillis() - spawnTime
        if (diff >= 1000f) {
          spatial.setUserData(MyFirstGame.Alive, true)
        }
        val color = new ColorRGBA(random.nextFloat(), random.nextFloat(), random.nextFloat(), diff / 1000f)
        val spatialNode = spatial.asInstanceOf[Node]
        val picture = spatialNode.getChild("BlackHole").asInstanceOf[Picture]
        picture.getMaterial.setColor("Color", color)
      })
  }

  def takeShot = {
    hitpoints -= 1
  }

  def isDead: Boolean = hitpoints == 0

  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = {}
}
