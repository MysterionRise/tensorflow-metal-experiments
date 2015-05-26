package org.mystic.controls

import com.jme3.math.{ColorRGBA, FastMath, Vector3f}
import com.jme3.renderer.{RenderManager, ViewPort}
import com.jme3.scene.{Node, Spatial}
import com.jme3.scene.control.AbstractControl
import com.jme3.ui.Picture
import org.mystic.MyFirstGame
import org.mystic.Utils._

class SeekerControl(player: Spatial) extends AbstractControl {

  private val spawnTime = System.currentTimeMillis
  private val velocity = new Vector3f(0, 0, 0)

  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = {}

  override def controlUpdate(tpf: Float): Unit = {
    checkSpatialIsAlive(spatial) {
      //translate the seeker
      val playerDirection = player.getLocalTranslation().subtract(spatial.getLocalTranslation())
      playerDirection.normalizeLocal()
      playerDirection.multLocal(1000f)
      velocity.addLocal(playerDirection)
      velocity.multLocal(0.8f)
      spatial.move(velocity.mult(tpf * 0.1f))

      // rotate the seeker
      if (velocity != Vector3f.ZERO) {
        spatial.rotateUpTo(velocity.normalize())
        spatial.rotate(0, 0, FastMath.PI / 2f)
      }
    } {
      // handle the "active"-status
      val dif = System.currentTimeMillis() - spawnTime
      if (dif >= 1000f) {
        spatial.setUserData(MyFirstGame.Alive, true)
      }

      val color = new ColorRGBA(1, 1, 1, dif / 1000f)
      val spatialNode = spatial.asInstanceOf[Node]
      val pic = spatialNode.getChild("Seeker").asInstanceOf[Picture]
      pic.getMaterial().setColor("Color", color)
    }
  }
}
