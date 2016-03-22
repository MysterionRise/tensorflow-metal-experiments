package org.mystic.controls

import com.jme3.math.{FastMath, ColorRGBA, Vector3f}
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.Node
import com.jme3.scene.control.AbstractControl
import com.jme3.ui.Picture

class ParticleControl(var velocity: Vector3f, lifespan: Float, color: ColorRGBA) extends AbstractControl {

  var spawnTime = System.currentTimeMillis()

  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = {}

  override def controlUpdate(tpf: Float): Unit = {
    spatial.move(velocity.mult(tpf * 3f))
    velocity.multLocal(1f - 3f * tpf)
    if (Math.abs(velocity.x) + Math.abs(velocity.y) < 0.001f) {
      velocity = Vector3f.ZERO
    }
    // rotation
    if (velocity != Vector3f.ZERO) {
      spatial.rotateUpTo(velocity.normalize())
      spatial.rotate(0, 0, FastMath.PI / 2f)
    }
    // scaling and alpha
    val speed = velocity.length()
    val difTime = System.currentTimeMillis() - spawnTime
    val percentLife = 1 - difTime / lifespan
    var alpha = lesserValue(1.5f, lesserValue(percentLife * 2, speed))
    alpha *= alpha
    setAlpha(alpha)
    spatial.setLocalScale(0.3f + lesserValue(lesserValue(1.5f, 0.02f * speed + 0.1f), alpha))
    spatial.scale(0.65f)
    // is particle expired?
    if (difTime > lifespan) {
      spatial.removeFromParent()
    }
  }

  def lesserValue(a: Float, b: Float) = {
    if (a < b)
      a
    else b
  }

  def setAlpha(alpha: Float) {
    color.set(color.r, color.g, color.b, alpha)
    val spatialNode = spatial.asInstanceOf[Node]
    val pic = spatialNode.getChild(spatialNode.getName()).asInstanceOf[Picture]
    pic.getMaterial().setColor("Color", color)
  }
}
