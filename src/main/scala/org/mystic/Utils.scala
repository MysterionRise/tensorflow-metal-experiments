package org.mystic

import com.jme3.math.{FastMath, Vector2f, Vector3f}
import com.jme3.scene.Spatial

object Utils {

  def getAngleFromVector(vec: Vector3f): Float = new Vector2f(vec.x, vec.y).getAngle

  def getVectorFromAngle(angle: Float): Vector3f = new Vector3f(FastMath.cos(angle), FastMath.sin(angle), 0)

  def checkSpatialIsAlive(spatial: Spatial)(tr: Unit)(fal: Unit) = if (spatial.getUserData[Boolean](MyFirstGame.Alive)) tr else fal
}
