package ib.service

import ib.Env

/**
 * Created by qili on 08/09/2015.
 */
trait IGroupServices extends IService {
  val services: Seq[IService]

  def run(implicit env: Env.Value) = for (s <- services) s.run
}
