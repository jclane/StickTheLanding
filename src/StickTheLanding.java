import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.concurrent.ThreadLocalRandom;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class StickTheLanding extends JPanel implements ActionListener {

  private Image jumpMan;
  private Image jumpMan_Standing;
  private Image jumpMan_Left;
  private Image jumpMan_Left2;
  private Image jumpMan_Right;
  private Image jumpMan_Right2;
  private Image jumpMan_Jump;
  private Image jumpMan_JumpRight;
  private Image jumpMan_JumpLeft;
  private Image spike;
  private final Image BACKGROUND = new ImageIcon("src/assets/background/background.png").getImage();

  private final int GROUND = 245;
  private int bk_x;
  private int bk_y;
  private final int BK_X_MAX = 0;
  private final int BK_X_MIN = -1500;
  private int x = 200;
  private int y = GROUND;
  private boolean leftDirection;
  private boolean rightDirection;
  private boolean upDirection;
  private boolean downDirection;
  private final int DELAY = 10;
  private Timer timer;
  private boolean gameOver;

  private List<Integer> spikes;

  public StickTheLanding() {
    addKeyListener(new TAdapter());
    setFocusable(true);
    setPreferredSize(new Dimension(500, 350));
    loadImages();
    jumpMan = jumpMan_Standing;
    initGame();
  }

  private void loadImages() {
    jumpMan_Standing = new ImageIcon("src/assets/sprites/jumpman_Standing.png").getImage();
    jumpMan_Left = new ImageIcon("src/assets/sprites/jumpman_MoveLeft.png").getImage();
    jumpMan_Left2 = new ImageIcon("src/assets/sprites/jumpman_MoveLeft2.png").getImage();
    jumpMan_Right = new ImageIcon("src/assets/sprites/jumpman_MoveRight.png").getImage();
    jumpMan_Right2 = new ImageIcon("src/assets/sprites/jumpman_MoveRight2.png").getImage();
    jumpMan_Jump = new ImageIcon("src/assets/sprites/jumpman_Jump.png").getImage();
    jumpMan_JumpLeft = new ImageIcon("src/assets/sprites/jumpman_JumpLeft.png").getImage();
    jumpMan_JumpRight = new ImageIcon("src/assets/sprites/jumpman_JumpRight.png").getImage();
    spike = new ImageIcon("src/assets/sprites/spike.png").getImage();
  }

  private void randomizeSpikes() {
    spikes = new ArrayList<Integer>();
    int totalSpikes = ThreadLocalRandom.current().nextInt(2, 5 + 1);
    int randomNum;
    int previousNum = 2;
    
    for (int i = 0; i < totalSpikes; i++) {
      randomNum = ThreadLocalRandom.current().nextInt(previousNum + 250, 1500);
      previousNum = (randomNum > 1250 ? 2 : randomNum);
      spikes.add(randomNum);
    }
  }

  private void initGame() {
    randomizeSpikes();
    gameOver = false;
    timer = new Timer(DELAY, this);
    timer.start();
  }

  private void gameOver(Graphics g) {
    String msg = "You DIED!";
    Font small = new Font("Helvetica", Font.BOLD, 14);
    FontMetrics metr = getFontMetrics(small);

    g.setColor(Color.red);
    setBackground(Color.black);
    g.setFont(small);
    g.drawString(msg, (500 - metr.stringWidth(msg)) / 2, 350 / 2);
  }

  private void move() {

    if (leftDirection) {
      if (bk_x < BK_X_MAX && x == 200) {
        bk_x += 8;
        for (int i = 0; i < spikes.size(); i++) {
          spikes.set(i, spikes.get(i) + 8);
        }
      } else if (x > -8) {
        x -= 8;
      }

      if (jumpMan == jumpMan_Left) {
        jumpMan = jumpMan_Left2;
      } else {
        jumpMan = jumpMan_Left;
      }
    }

    if (rightDirection) {
      if (bk_x > BK_X_MIN && x == 200) {
        bk_x -= 8;
        for (int i = 0; i < spikes.size(); i++) {
          spikes.set(i, spikes.get(i) - 8);
        }
      } else if (x < BK_X_MAX + 440) {
        x += 8;
      }

      if (jumpMan == jumpMan_Right) {
        jumpMan = jumpMan_Right2;
      } else {
        jumpMan = jumpMan_Right;
      }
    }

    if (upDirection) {
      y -= 5;

      if (leftDirection) {
        jumpMan = jumpMan_JumpLeft;
      } else if (rightDirection) {
        jumpMan = jumpMan_JumpRight;
      } else {
        jumpMan = jumpMan_Jump;
      }

      if (y < 95) {
        upDirection = false;
        downDirection = true;
      }
    }

    if (downDirection) {
      y += 5;

      if (leftDirection) {
        jumpMan = jumpMan_JumpLeft;
      } else if (rightDirection) {
        jumpMan = jumpMan_JumpRight;
      } else {
        jumpMan = jumpMan_Jump;
      }

      if (y >= GROUND) {
        y = GROUND;
        downDirection = false;
        leftDirection = false;
        rightDirection = false;
        jumpMan = jumpMan_Standing;
      }
    }

  }

  private void checkCollision() {
    for (int spike : spikes) {
      if (x > spike - 50 && x < spike + 50 || bk_x  > spike - 50 && bk_x < spike + 50) {
        if (y == GROUND) {
          gameOver = true;
        }
      }
    }

    if (gameOver) {
      timer.stop();
    }
  }

  private void doDrawing(Graphics g) {
    if (!gameOver) {
      g.drawImage(BACKGROUND, bk_x, bk_y, this);
      g.drawImage(jumpMan, x, y, this);

      for (int i = 0; i < spikes.size(); i++) {
        g.drawImage(spike, spikes.get(i), GROUND + 25, this);
      }

      Toolkit.getDefaultToolkit().sync();

    } else {
      gameOver(g);
    }
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    doDrawing(g);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    checkCollision();
    move();
    repaint();
  }

  private class TAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();

      if (key == KeyEvent.VK_LEFT && 
          !upDirection && !downDirection) {
          leftDirection = true;
          rightDirection = false;
      }

      if (key == KeyEvent.VK_RIGHT && 
          !upDirection && !downDirection) {
          rightDirection = true;
          leftDirection = false;
      }

      if (key == KeyEvent.VK_UP || 
          key == KeyEvent.VK_SPACE && 
          !downDirection) {
          upDirection = true;
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
      int key = e.getKeyCode();

      if (key == KeyEvent.VK_LEFT && 
          !upDirection && 
          !downDirection) {
        leftDirection = false;
        jumpMan = jumpMan_Standing;
      }

      if (key == KeyEvent.VK_RIGHT && 
          !upDirection && 
          !downDirection) {
        rightDirection = false;
        jumpMan = jumpMan_Standing;
      }
    }
  }
}