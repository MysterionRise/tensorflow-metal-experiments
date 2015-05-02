import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.material.RenderState.BlendMode
import com.jme3.math.Vector3f
import com.jme3.scene.{Node, Spatial}
import com.jme3.texture.Texture2D
import com.jme3.ui.Picture

object MyFirstGame extends SimpleApplication {

  override def simpleInitApp(): Unit = {
    // set up camera for 2D
    cam.setParallelProjection(true)
    cam.setLocation(new Vector3f(0, 0, 0.5f))
    getFlyByCamera().setEnabled(false)

    // turn off stats view
    setDisplayStatView(false)
    setDisplayFps(false)

    // add player
    val player = getSpatial("Player")
    player.setUserData("alive", true)
    player.move(settings.getWidth() / 2, settings.getHeight() / 2, 0)
    guiNode.attachChild(player)
  }

  // entry point of the game
  def main(args: Array[String]): Unit = {
    MyFirstGame.start()
  }

  private def getSpatial(name: String): Spatial = {
    val node = new Node(name)
    // load picture
    val pic = new Picture(name)
    val tex: Texture2D = assetManager.loadTexture(s"Textures/${name}.png").asInstanceOf[Texture2D]
    pic.setTexture(assetManager, tex, true)

    // adjust picture
    val width = tex.getImage.getWidth
    val height = tex.getImage.getHeight
    pic.setWidth(width)
    pic.setHeight(height)
    // for rotation purposes
    pic.move(-width / 2f, -height / 2f, 0)

    // add a material to the picture
    val pictureMaterial = new Material(assetManager, "Common/MatDefs/Gui/Gui.j3md")
    pictureMaterial.getAdditionalRenderState.setBlendMode(BlendMode.AlphaAdditive)
    node.setMaterial(pictureMaterial)

    // set the radius of the spatial
    // (using width only as a simple approximation)
    node.setUserData("radius", width / 2)

    // attach the picture to the node and return it
    node.attachChild(pic)
    node
  }

}
