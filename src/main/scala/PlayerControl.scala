import com.jme3.math.FastMath
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.control.AbstractControl

case class PlayerControl(screenWidth: Int, screenHeight: Int) extends AbstractControl {

  //    is the player currently moving?
  var up, down, left, right: Boolean = _
  //    speed of the player
  var speed = 250f
  //    lastRotation of the player
  var lastRotation: Float = _

  override def controlRender(rm: RenderManager, vp: ViewPort): Unit = {

  }

  override def controlUpdate(tpf: Float): Unit = {
    if (up) {
      if (spatial.getLocalTranslation().y < screenHeight - spatial.getUserData[Int]("radius")) {
        spatial.move(0, tpf * speed, 0)
      }
      spatial.rotate(0, 0, -lastRotation + FastMath.PI / 2);
      lastRotation = FastMath.PI / 2
    } else if (down) {
      if (spatial.getLocalTranslation().y > spatial.getUserData[Int]("radius")) {
        spatial.move(0, -tpf * speed, 0)
      }
      spatial.rotate(0, 0, -lastRotation + FastMath.PI * 1.5f);
      lastRotation = FastMath.PI * 1.5f
    } else if (left) {
      if (spatial.getLocalTranslation().x > spatial.getUserData[Int]("radius")) {
        spatial.move(-tpf * speed, 0, 0)
      }
      spatial.rotate(0, 0, -lastRotation + FastMath.PI)
      lastRotation = FastMath.PI
    } else if (right) {
      if (spatial.getLocalTranslation().x < screenWidth - spatial.getUserData[Int]("radius")) {
        spatial.move(tpf * speed, 0, 0)
      }
      spatial.rotate(0, 0, -lastRotation + 0)
      lastRotation = 0
    }
  }
}
