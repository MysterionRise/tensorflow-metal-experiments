package org.mystic.controls

import com.jme3.renderer.{RenderManager, ViewPort}
import com.jme3.scene.control.AbstractControl

class WandererControl()extends AbstractControl{
  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = {}

  override def controlUpdate(tpf: Float): Unit = {}
}
