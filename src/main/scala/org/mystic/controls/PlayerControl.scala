package org.mystic.controls

import com.jme3.math.{Vector3f, FastMath}
import com.jme3.renderer.{RenderManager, ViewPort}
import com.jme3.scene.control.AbstractControl
import org.mystic.{ParticleManager, MyFirstGame}

class PlayerControl(screenWidth: Int, screenHeight: Int, particleManager: ParticleManager) extends AbstractControl {

  def reset() = {
    up = false
    down = false
    left = false
    right = false
  }

  // is the player currently moving?
  var up, down, left, right: Boolean = _
  // speed of the player
  var speed = 550f
  // lastRotation of the player
  var lastRotation: Float = _

  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = {

  }

  override def controlUpdate(tpf: Float): Unit = {
    if (up) {
      if (spatial.getLocalTranslation().y < screenHeight - spatial.getUserData[Float](MyFirstGame.Radius)) {
        spatial.move(0, tpf * speed, 0)
      }
      spatial.rotate(0, 0, -lastRotation + FastMath.PI / 2);
      lastRotation = FastMath.PI / 2
    } else if (down) {
      if (spatial.getLocalTranslation().y > spatial.getUserData[Float](MyFirstGame.Radius)) {
        spatial.move(0, -tpf * speed, 0)
      }
      spatial.rotate(0, 0, -lastRotation + FastMath.PI * 1.5f);
      lastRotation = FastMath.PI * 1.5f
    } else if (left) {
      if (spatial.getLocalTranslation().x > spatial.getUserData[Float](MyFirstGame.Radius)) {
        spatial.move(-tpf * speed, 0, 0)
      }
      spatial.rotate(0, 0, -lastRotation + FastMath.PI)
      lastRotation = FastMath.PI
    } else if (right) {
      if (spatial.getLocalTranslation().x < screenWidth - spatial.getUserData[Float](MyFirstGame.Radius)) {
        spatial.move(tpf * speed, 0, 0)
      }
      spatial.rotate(0, 0, -lastRotation + 0)
      lastRotation = 0
    }
    if (up || down || right || left) {
      particleManager.makeExhaustFire(spatial.getLocalTranslation(), lastRotation, System.currentTimeMillis())
    }
  }

  def applyGravity(vector3f: Vector3f) = {
    spatial.move(vector3f)
  }
}
