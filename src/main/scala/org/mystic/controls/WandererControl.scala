package org.mystic.controls

import com.jme3.math.{ColorRGBA, Vector3f, FastMath}
import com.jme3.renderer.{RenderManager, ViewPort}
import com.jme3.scene.Node
import com.jme3.scene.control.AbstractControl
import com.jme3.ui.Picture
import org.mystic.MyFirstGame
import org.mystic.Utils._

import scala.util.Random

class WandererControl(width: Int, height: Int) extends AbstractControl {

  private val velocity = new Vector3f()
  private var directionAngle = new Random().nextFloat() * FastMath.PI * 2f
  private val spawnTime = System.currentTimeMillis()

  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = {}

  override def controlUpdate(tpf: Float): Unit = {
    if (checkSpatialIsAlive(spatial)) {
      // translate the wanderer

      // change the directionAngle a bit
      directionAngle += (new Random().nextFloat() * 20f - 10f) * tpf
      System.out.println(directionAngle)
      val directionVector = getVectorFromAngle(directionAngle)
      directionVector.multLocal(1000f)
      velocity.addLocal(directionVector)

      // decrease the velocity a bit and move the wanderer
      velocity.multLocal(0.8f)
      spatial.move(velocity.mult(tpf * 0.1f))

      // make the wanderer bounce off the screen borders
      val loc = spatial.getLocalTranslation()
      if (loc.x > width || loc.y > height || loc.x < 0 || loc.y < 0) {
        val newDirectionVector = new Vector3f(width / 2, height / 2, 0).subtract(loc)
        directionAngle = getAngleFromVector(newDirectionVector)
      }

      // rotate the wanderer
      spatial.rotate(0, 0, tpf * 2)
    } else {
      // handle the "active"-status
      val dif = System.currentTimeMillis() - spawnTime
      if (dif >= 1000f) {
        spatial.setUserData(MyFirstGame.Alive, true)
      }

      val color = new ColorRGBA(1, 1, 1, dif / 1000f)
      val spatialNode = spatial.asInstanceOf[Node]
      val pic = spatialNode.getChild("Wanderer").asInstanceOf[Picture]
      pic.getMaterial().setColor("Color", color)
    }
  }
}
