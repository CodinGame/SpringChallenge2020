package com.codingame.spring2020.action;

import com.codingame.spring2020.PacmanType;

public class SpeedAction implements Action {
  @Override
  public ActionType getActionType() {
      return ActionType.SPEED;
  }

  @Override
  public PacmanType getType() {
      return null;
  }
}