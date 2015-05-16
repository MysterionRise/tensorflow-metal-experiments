import com.jme3.math.Vector3f
import com.jme3.renderer.{RenderManager, ViewPort}
import com.jme3.scene.control.AbstractControl

case class BulletControl(aim: Vector3f, screenWidth: Int, screenHeight: Int) extends AbstractControl {
  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = {}

  override def controlUpdate(tpf: Float): Unit = {}
}
