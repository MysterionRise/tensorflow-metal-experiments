package org.mystic

import com.jme3.math.{FastMath, Vector2f, Vector3f}
import com.jme3.scene.Spatial

object Utils {

  def getAngleFromVector(vec: Vector3f): Float = {
    val vec2 = new Vector2f(vec.x, vec.y)
    vec2.getAngle()
  }

  def getVectorFromAngle(angle: Float): Vector3f = {
    new Vector3f(FastMath.cos(angle), FastMath.sin(angle), 0)
  }

  // todo make it high order function or party applied
  def checkSpatialIsAlive(spatial: Spatial): Boolean = spatial.getUserData[Boolean](MyFirstGame.Alive)
}
