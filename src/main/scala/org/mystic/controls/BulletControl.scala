package org.mystic.controls

import com.jme3.math.Vector3f
import com.jme3.renderer.{RenderManager, ViewPort}
import com.jme3.scene.control.AbstractControl
import org.mystic.{ParticleManager, Utils}

class BulletControl(direction: Vector3f, screenWidth: Int, screenHeight: Int, particleManager: ParticleManager) extends AbstractControl {

  def applyGravity(gravity: Vector3f) = {
    direction.addLocal(gravity)
  }

  private val speed = 1000f
  private var rotation: Float = _

  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = {}

  override def controlUpdate(tpf: Float): Unit = {
    //        movement
    spatial.move(direction.mult(speed * tpf))

    //        rotation
    val actualRotation = Utils.getAngleFromVector(direction)
    if (actualRotation != rotation) {
      spatial.rotate(0, 0, actualRotation - rotation)
      rotation = actualRotation
    }

    //        check boundaries
    val loc = spatial.getLocalTranslation()
    if (loc.x > screenWidth ||
      loc.y > screenHeight ||
      loc.x < 0 ||
      loc.y < 0) {
      particleManager.bulletExplosion(loc)
      spatial.removeFromParent()
    }
  }
}
