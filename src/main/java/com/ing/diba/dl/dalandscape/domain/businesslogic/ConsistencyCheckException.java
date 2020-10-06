package com.ing.diba.dl.dalandscape.domain.businesslogic;

public class ConsistencyCheckException extends Exception {
  public ConsistencyCheckException(String errorMessage) {
    super(errorMessage);
  }
}
