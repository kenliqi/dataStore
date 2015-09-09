package ib.service

import ib.Env

/**
 * Created by qili on 08/09/2015.
 */
trait IService {

  def run(implicit env: Env.Value)

}
