package com.laine.casimir.tetris.swing.control;

import com.laine.casimir.tetris.base.control.TetrisController;

import java.awt.*;
import java.awt.event.KeyEvent;

public class SwingKeyControls implements KeyEventDispatcher {

    private final TetrisController tetrisController;

    public SwingKeyControls(TetrisController tetrisController) {
        this.tetrisController = tetrisController;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED) {
            return false;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_F1:
                tetrisController.pause();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_NUMPAD4:
                tetrisController.shiftLeft();
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_NUMPAD6:
                tetrisController.shiftRight();
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_X:
            case KeyEvent.VK_NUMPAD1:
            case KeyEvent.VK_NUMPAD5:
            case KeyEvent.VK_NUMPAD9:
                tetrisController.rotateClockwise();
                break;
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_Z:
            case KeyEvent.VK_NUMPAD3:
            case KeyEvent.VK_NUMPAD7:
                tetrisController.rotateCounterclockwise();
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_NUMPAD8:
                tetrisController.hardDrop();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_NUMPAD2:
                tetrisController.softDrop();
                break;
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_C:
            case KeyEvent.VK_NUMPAD0:
                tetrisController.hold();
                break;
        }
        return true;
    }
}
