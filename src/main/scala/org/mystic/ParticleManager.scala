package org.mystic

import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.scene.{Spatial, Node}
import org.mystic.controls.ParticleControl

import scala.util.Random

class ParticleManager(guiNode: Node, standardParticle: Spatial, glowParticle: Spatial) {

  def playerExplosion(loc: Vector3f) = {
    val color1 = ColorRGBA.Yellow
    val color2 = ColorRGBA.Red
    for (i <- 0 until 2000) {

      val velocity = getRandomVelocity(1500)
      val particle = standardParticle.clone()
      particle.setLocalTranslation(loc)
      val color = new ColorRGBA()
      color.interpolate(color1, color2, random.nextFloat)
      particle.addControl(new ParticleControl(velocity, 3000, color))
      particle.setUserData("affectedByGravity", true)
      particleNode.attachChild(particle)
    }
  }

  def bulletExplosion(loc: Vector3f) = {
    for (i <- 0 until 20) {

      val velocity = getRandomVelocity(500)
      val particle = standardParticle.clone()
      particle.setLocalTranslation(loc)
      val color = new ColorRGBA(0.676f, 0.844f, 0.898f, 1)
      particle.addControl(new ParticleControl(velocity, 1000, color))
      particle.setUserData("affectedByGravity", true)
      particleNode.attachChild(particle)
    }
  }


  var particleNode: Node = new Node("particles")
  guiNode.attachChild(particleNode)
  val random = new Random()

  def getParticleNode = particleNode

  def enemyExplosion(position: Vector3f) = {
    val hue1 = random.nextFloat() * 6
    val hue2 = (random.nextFloat() * 2) % 6f
    val color1 = Utils.hsvToColor(hue1, 0.5f, 1f)
    val color2 = Utils.hsvToColor(hue2, 0.5f, 1f)

    for (i <- 0 until 120) {
      val velocity = getRandomVelocity(250)

      val particle = standardParticle.clone()
      particle.setLocalTranslation(position)
      val color = new ColorRGBA()
      color.interpolate(color1, color2, random.nextFloat() * 0.5f)
      particle.addControl(new ParticleControl(velocity, 3100, color))
      particle.setUserData("affectedByGravity", true)
      particleNode.attachChild(particle)
    }
  }

  def getRandomVelocity(max: Float) = {
    val velocity = new Vector3f(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, 0).normalizeLocal()
    val particleSpeed = max * (1f - 0.6f / (random.nextFloat() * 5 + 1))
    velocity.multLocal(particleSpeed)
    velocity
  }
}
