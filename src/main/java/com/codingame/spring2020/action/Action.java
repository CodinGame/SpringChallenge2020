package com.codingame.spring2020.action;

import com.codingame.spring2020.PacmanType;

public interface Action {

    Action NO_ACTION = new Action() {

        @Override
        public PacmanType getType() {
            return null;
        }

        @Override
        public ActionType getActionType() {
            return ActionType.WAIT;
        }
    };

    public PacmanType getType();
    public ActionType getActionType();
}
