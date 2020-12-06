package com.laine.casimir.tetris.swing.view.component.panel;

import com.laine.casimir.tetris.base.api.TetrisConstants;
import com.laine.casimir.tetris.base.api.TetrisController;
import com.laine.casimir.tetris.base.model.Position;
import com.laine.casimir.tetris.base.model.Square;
import com.laine.casimir.tetris.base.model.Tetromino;
import com.laine.casimir.tetris.swing.SwingTetrisConstants;
import com.laine.casimir.tetris.swing.SwingTetrisSettings;
import com.laine.casimir.tetris.swing.control.SwingKeyControls;
import com.laine.casimir.tetris.swing.view.component.JTetrisGrid;
import com.laine.casimir.tetris.swing.view.component.JTetrisSquare;
import com.laine.casimir.tetris.swing.view.component.fragment.JHoldBoxFragment;
import com.laine.casimir.tetris.swing.view.component.fragment.JInfoFragment;
import com.laine.casimir.tetris.swing.view.component.fragment.JNextTetrominoFragment;
import com.laine.casimir.tetris.swing.view.layout.TetrisGridLayout;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

final class JTetrisGamePanel extends JPanel {

    private final Color transparent = new Color(0, 0, 0, 0);
    private final SwingTetrisSettings settings = new SwingTetrisSettings();

    private final SwingKeyControls gameControls = new SwingKeyControls();
    private final Timer timer;

    private final JFrame frame;
    private final JPauseMenuPanel pauseMenuPanel;

    private final List<JTetrisSquare> tetrisSquares = new ArrayList<>();
    private final JTetrisGrid tetrisGrid = new JTetrisGrid(TetrisConstants.WIDTH, TetrisConstants.VISIBLE_HEIGHT);
    private final JHoldBoxFragment holdBoxFragment = new JHoldBoxFragment();
    private final JNextTetrominoFragment nextTetrominoFragment = new JNextTetrominoFragment();
    private final JInfoFragment infoFragment = new JInfoFragment();

    private final TetrisController tetrisController;

    public JTetrisGamePanel(JFrame frame) {
        this.frame = frame;
        this.pauseMenuPanel = new JPauseMenuPanel(frame, this);
        this.tetrisController = new TetrisController();
        timer = new Timer(0, e -> update());
        timer.setDelay(0);
        timer.setRepeats(true);
        init();
    }

    private void init() {
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (event.getAncestor() == JTetrisGamePanel.this) {
                    gameControls.setTetrisController(tetrisController);
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(gameControls);
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (event.getAncestor() == JTetrisGamePanel.this) {
                    gameControls.setTetrisController(null);
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(gameControls);
                }
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
        final JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(holdBoxFragment);
        leftPanel.add(infoFragment);
        tetrisGrid.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                final int cellSize = tetrisGrid.getCellSize();
                holdBoxFragment.setCellSize(cellSize);
                nextTetrominoFragment.setCellSize(cellSize);
                leftPanel.setPreferredSize(new Dimension(cellSize * 5, getHeight()));
                nextTetrominoFragment.setPreferredSize(new Dimension(cellSize * 5, getHeight()));
            }
        });
        tetrisGrid.setBackground(SwingTetrisConstants.BACKGROUND_COLOR);
        tetrisGrid.setForeground(SwingTetrisConstants.GRID_COLOR);
        tetrisGrid.setLayout(new TetrisGridLayout(tetrisGrid.getColCount(), tetrisGrid.getRowCount()));
        for (int y = 0; y < tetrisGrid.getRowCount(); y++) {
            for (int x = 0; x < tetrisGrid.getColCount(); x++) {
                final JTetrisSquare tetrisSquare = new JTetrisSquare();
                tetrisSquare.setBackground(new Color(0, 0, 0, 0));
                tetrisSquare.setForeground(SwingTetrisConstants.BACKGROUND_COLOR);
                tetrisGrid.add(tetrisSquare, new Point(x, y));
                tetrisSquares.add(tetrisSquare);
            }
        }
        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.WEST);
        add(tetrisGrid, BorderLayout.CENTER);
        add(nextTetrominoFragment, BorderLayout.EAST);
        tetrisController.start();
        resume();
    }

    public void resume() {
        tetrisController.resume();
        frame.setContentPane(this);
        timer.start();
    }

    private void update() {
        if (tetrisController.isPaused()) {
            timer.stop();
            frame.setContentPane(pauseMenuPanel);
            return;
        }
        tetrisController.update();
        render();
    }

    private void render() {
        for (int index = 0; index < tetrisSquares.size(); index++) {
            tetrisSquares.get(index).setBackground(transparent);
        }
        final List<Square> allSquares = tetrisController.getAllSquares();
        for (int index = 0; index < allSquares.size(); index++) {
            final Square square = allSquares.get(index);
            if (square != null) {
                final Position position = square.getPosition();
                final int uiSquareIndex = tetrisGrid.getColCount() * position.getY() + position.getX();
                if (uiSquareIndex >= 0 && uiSquareIndex < tetrisSquares.size()) {
                    tetrisSquares.get(uiSquareIndex).setBackground(Color.decode(square.getColorHex()));
                }
            }
        }
        tetrisGrid.revalidate();
        tetrisGrid.repaint();
        holdBoxFragment.setTetromino(tetrisController.getHeldTetromino());
        nextTetrominoFragment.clear();
        final Tetromino[] previewTetrominos = tetrisController.getPreviewTetrominos(settings.getNextQueueCount());
        for (final Tetromino tetromino : previewTetrominos) {
            nextTetrominoFragment.addTetromino(tetromino);
        }
        infoFragment.setScore(tetrisController.getScore());
        infoFragment.setLevel(tetrisController.getLevel());
        infoFragment.setLines(tetrisController.getClearedRows());
    }
}
