package org.mystic

import com.jme3.math.{ColorRGBA, FastMath, Vector2f, Vector3f}
import com.jme3.scene.Spatial

object Utils {

  def hsvToColor(h: Float, s: Float, v: Float): ColorRGBA = {
    if (h == 0 && s == 0) {
      return new ColorRGBA(v, v, v, 1)
    }
    val c = s * v
    val x = c * (1 - Math.abs(h % 2 - 1))
    val m = v - c

    if (h < 1) {
      return new ColorRGBA(c + m, x + m, m, 1)
    } else if (h < 2) {
      return new ColorRGBA(x + m, c + m, m, 1)
    } else if (h < 3) {
      return new ColorRGBA(m, c + m, x + m, 1)
    } else if (h < 4) {
      return new ColorRGBA(m, x + m, c + m, 1)
    } else if (h < 5) {
      return new ColorRGBA(x + m, m, c + m, 1)
    } else {
      return new ColorRGBA(c + m, m, x + m, 1)
    }
  }

  def getAngleFromVector(vec: Vector3f): Float = new Vector2f(vec.x, vec.y).getAngle

  def getVectorFromAngle(angle: Float): Vector3f = new Vector3f(FastMath.cos(angle), FastMath.sin(angle), 0)

  def checkSpatialIsAlive(spatial: Spatial, tr: () => Unit, fal: () => Unit) = if (spatial.getUserData[Boolean](MyFirstGame.Alive)) tr.apply else fal.apply
}
