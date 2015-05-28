package org.mystic.controls

import com.jme3.math.ColorRGBA
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.Node
import com.jme3.scene.control.AbstractControl
import com.jme3.ui.Picture
import org.mystic.MyFirstGame
import org.mystic.Utils._

class ExtraLifeControl extends AbstractControl {

  private val spawnTime = System.currentTimeMillis

  override def controlUpdate(tpf: Float): Unit = {
    checkSpatialIsAlive(spatial, () => {},
      () => {
        val diff = System.currentTimeMillis() - spawnTime
        if (diff >= 1000f) {
          spatial.setUserData(MyFirstGame.Alive, true)
        }
        val color = new ColorRGBA(1, 1, 1, diff / 1000f)
        val spatialNode = spatial.asInstanceOf[Node]
        val picture = spatialNode.getChild("ExtraLife").asInstanceOf[Picture]
        picture.getMaterial.setColor("Color", color)
      })
  }

  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = {}
}
