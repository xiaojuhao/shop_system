package com.scalax.tools

import java.util
import java.util.function.Function

object ScalaTools {
  def isNotBlank(str: String): Boolean = str != null && !str.isEmpty;

  def trim(str: String): String = if (str == null) "" else str.trim();

  def collectSet[V, R](list: util.List[V], mapper: Function[V, R]): util.Set[R] = {
    if (list == null || list.size == 0) return new util.HashSet[R]
    val retSet = new util.HashSet[R]
    list.iterator().forEachRemaining(v => {
      val r: R = mapper.apply(v)
      if (r != null) retSet.add(r)
    })
    retSet
  }
}
