import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.util.concurrent.ThreadLocalRandom;

class Game implements ActionListener {

    public boolean allset = false, wall = true, star = true, mine = true, ended = false, player1_turn;
    int walls, mines, stars, margin = 0;
    int bluepoint = 0, redpoint = 0;
    Random random = new Random();
    JFrame frame = new JFrame();
    JPanel title_panel = new JPanel();
    JPanel scores_panel = new JPanel();
    JPanel button_panel = new JPanel();
    JLabel textfield = new JLabel();
    JLabel scoreboard = new JLabel();
    JButton[] buttons = new JButton[setMargins() * setMargins()];


    Game() {
        margin = setMargins();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 800);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        textfield.setBackground(new Color(25, 25, 25));
        textfield.setForeground(new Color(25, 255, 0));
        textfield.setFont(new Font("Ink Free", Font.BOLD, 75));
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Click on a button to start");

        scoreboard.setBackground(new Color(25, 25, 25));
        scoreboard.setForeground(new Color(25, 255, 0));
        scoreboard.setFont(new Font("Ink Free", Font.BOLD, 35));
        scoreboard.setHorizontalAlignment(JLabel.CENTER);
        scoreboard.setText("Red:" + redpoint + "                                          Blue:" + bluepoint);

        textfield.setOpaque(true);
        scoreboard.setOpaque(true);

        title_panel.setLayout(new BorderLayout());
        title_panel.setBounds(0, 0, 800, 100);
        scores_panel.setLayout(new BorderLayout());
        scores_panel.setBounds(0, 0, 400, 100);
        button_panel.setLayout(new GridLayout(margin, margin));
        button_panel.setBackground(new Color(150, 150, 150));

        for (int i = 0; i < margin * margin; i++) {
            buttons[i] = new JButton();
            button_panel.add(buttons[i]);
            buttons[i].setFont(new Font("MV Boli", Font.BOLD, 30));
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
        }

        title_panel.add(textfield);
        scores_panel.add(scoreboard);
        frame.add(title_panel, BorderLayout.NORTH);
        frame.add(scores_panel, BorderLayout.SOUTH);
        frame.add(button_panel);

        buttons[(margin * margin) - margin].setForeground(new Color(0, 0, 255));
        buttons[(margin * margin) - margin].setText("O");

        buttons[(margin * margin) - 1].setForeground(new Color(255, 0, 0));
        buttons[(margin * margin) - 1].setText("X");


        String yesORno = JOptionPane.showInputDialog("Do you want to set up your own map?");
        while (true) {
            if (yesORno.equalsIgnoreCase("yes")) {
                setWalls();
                setStars();
                setMines();
                break;
            } else if (yesORno.equalsIgnoreCase("no")) {
                allset = true;
                wall = false;
                star = false;
                mine = false;
                RandomMap();
                break;
            } else {
                yesORno = JOptionPane.showInputDialog("Please answer with yes or no only");
            }
        }

    }

    int counter = 0, j = 0;


    @Override
    public void actionPerformed(ActionEvent e) {
        int bluepos = 0, redpos = 0;
        if (wall) textfield.setText("Click on a button to place a Wall");
        else if (star) textfield.setText("Click on a button to place a Star");
        else if (mine) textfield.setText("Click on a button to place a Mine");
        if (counter == 0) {
            counter++;
            return;
        }

        if (wall) {
            for (int i = 0; i < margin * margin; i++) {
                if (e.getSource() == buttons[i]) {
                    if (buttons[i].getText() == "") {
                        buttons[i].setForeground(new Color(0, 0, 0));
                        buttons[i].setBackground(new Color(0, 0, 0));
                        buttons[i].setText("#");
                    } else {
                        JOptionPane.showMessageDialog(null, "Walls cannot be placed here");
                        return;
                    }
                }
            }
            counter++;
            if (counter == walls + 1) {
                wall = false;
                walls = 0;
                counter = 1;
                if (star) textfield.setText("Click on a button to place a Star");
                else if (mine) textfield.setText("Click on a button to place a Mine");
                else textfield.setText("Click on a button");
            }
            return;
        }

        if (star) {
            for (int i = 0; i < margin * margin; i++) {
                if (e.getSource() == buttons[i]) {
                    if (buttons[i].getText() == "") {
                        int points = setStarPoints();
                        buttons[i].setForeground(new Color(255, 200, 0));
                        buttons[i].setBackground(new Color(255, 255, 100));
                        buttons[i].setText("+" + points);
                    } else {
                        JOptionPane.showMessageDialog(null, "Stars cannot be placed here");
                        return;
                    }
                }
            }
            counter++;
            if (counter == stars + 1) {
                star = false;
                stars = 0;
                counter = 1;
                if (mines > 0) textfield.setText("Click on a button to place a Mine");
                else textfield.setText("Click on a button");
            }
            return;
        }

        if (mine) {
            for (int i = 0; i < margin * margin; i++) {
                if (e.getSource() == buttons[i]) {
                    if (buttons[i].getText() == "") {
                        int points = setMinePoints();
                        buttons[i].setForeground(new Color(110, 0, 0));
                        buttons[i].setBackground(new Color(50, 0, 0));
                        buttons[i].setText("" + points);
                    } else {
                        JOptionPane.showMessageDialog(null, "Mines cannot be placed here");
                        return;
                    }
                }
            }
            counter++;
            if (counter == mines + 1) {
                mine = false;
                mines = 0;
                allset = true;
                firstTurn();
            }
            return;
        } else if (counter == 1) {
            counter = -1;
            allset = true;
            mine = false;
            firstTurn();
            return;
        }

        if (allset) {
            for (int i = 0; i < margin * margin; i++) {
                if (e.getSource() == buttons[i]) {
                    if (buttons[i].getText() != "O" && buttons[i].getText() != "X" && buttons[i].getText() != "#") {
                        if (player1_turn) {
                            redpos = RedPosition();
                            if (i >= redpos - (redpos % margin) && i < redpos + (margin - redpos % margin)) {
                                HorizontalSteps(i, redpos);
                            } else if (i % margin == redpos % margin) {
                                VerticalSteps(i, redpos);
                            } else {
                                JOptionPane.showMessageDialog(null, "you can only move verticaly or horizontaly");
                                return;
                            }
                            scoreboard.setText("Red:" + redpoint + "                                          Blue:" + bluepoint);
                        } else {
                            bluepos = BluePosition();
                            if (i >= bluepos - (bluepos % margin) && i < bluepos + (margin - bluepos % margin)) {
                                HorizontalSteps(i, bluepos);
                            } else if (i % margin == bluepos % margin) {
                                VerticalSteps(i, bluepos);
                            } else {
                                JOptionPane.showMessageDialog(null, "you can only move verticaly or horizontaly");
                                return;
                            }
                            scoreboard.setText("Red:" + redpoint + "                                          Blue:" + bluepoint);
                        }
                        if (!ended) {
                            if (!player1_turn) textfield.setText("Blue turn");
                            else textfield.setText("Red turn");
                        }
                    }
                }
            }
        }
    }


    public void HorizontalSteps(int i, int pos) {
        if (i > pos) {
            for (int x = pos + 1; x <= i; x++) {
                if (buttons[x].getText() == "") {
                    buttons[x - 1].setText("");
                    if (player1_turn) {
                        buttons[x].setForeground(new Color(255, 0, 0));
                        buttons[x].setText("X");
                    } else {
                        buttons[x].setForeground(new Color(0, 0, 255));
                        buttons[x].setText("O");
                    }
                } else if (buttons[x].getText() == "#" || buttons[x].getText() == "X" || buttons[x].getText() == "O") {
                    if (i - pos == 1) {
                        player1_turn = !player1_turn;
                    } else {
                        if (player1_turn) textfield.setText("Blue turnnn");
                        else textfield.setText("Red turnnn");
                    }
                    break;
                } else if (Integer.parseInt(buttons[x].getText()) > -21 && Integer.parseInt(buttons[x].getText()) < 6) {
                    buttons[x - 1].setText("");
                    if (player1_turn) {
                        redpoint += Integer.parseInt(buttons[x].getText());
                        buttons[x].setText("");
                        buttons[x].setBackground(new Color(150, 150, 150));
                        buttons[x].setForeground(new Color(255, 0, 0));
                        buttons[x].setText("X");
                    } else {
                        bluepoint += Integer.parseInt(buttons[x].getText());
                        buttons[x].setText("");
                        buttons[x].setBackground(new Color(150, 150, 150));
                        buttons[x].setForeground(new Color(0, 0, 255));
                        buttons[x].setText("O");
                    }
                    check(redpoint, bluepoint);
                }
            }
        } else if (i < pos) {
            for (int x = pos - 1; x >= i; x--) {
                if (buttons[x].getText() == "") {
                    buttons[x + 1].setText("");
                    if (player1_turn) {
                        buttons[x].setForeground(new Color(255, 0, 0));
                        buttons[x].setText("X");
                    } else {
                        buttons[x].setForeground(new Color(0, 0, 255));
                        buttons[x].setText("O");
                    }
                } else if (buttons[x].getText() == "#" || buttons[x].getText() == "X" || buttons[x].getText() == "O") {
                    if (pos - i == 1) {
                        player1_turn = !player1_turn;
                    } else {
                        if (player1_turn) textfield.setText("Blue turnnn");
                        else textfield.setText("Red turnnn");
                    }
                    break;
                } else if (Integer.parseInt(buttons[x].getText()) > -21 && Integer.parseInt(buttons[x].getText()) < 6) {
                    buttons[x + 1].setText("");
                    if (player1_turn) {
                        redpoint += Integer.parseInt(buttons[x].getText());
                        buttons[x].setText("");
                        buttons[x].setBackground(new Color(150, 150, 150));
                        buttons[x].setForeground(new Color(255, 0, 0));
                        buttons[x].setText("X");
                    } else {
                        bluepoint += Integer.parseInt(buttons[x].getText());
                        buttons[x].setText("");
                        buttons[x].setBackground(new Color(150, 150, 150));
                        buttons[x].setForeground(new Color(0, 0, 255));
                        buttons[x].setText("O");
                    }
                    check(redpoint, bluepoint);
                }
            }
        }
        player1_turn = !player1_turn;
    }

    public void VerticalSteps(int i, int pos) {
        if (i > pos) {
            for (int x = pos + margin; x <= i; x = x + margin) {
                if (buttons[x].getText() == "") {
                    buttons[x - margin].setText("");
                    if (player1_turn) {
                        buttons[x].setForeground(new Color(255, 0, 0));
                        buttons[x].setText("X");
                    } else {
                        buttons[x].setForeground(new Color(0, 0, 255));
                        buttons[x].setText("O");
                    }
                } else if (buttons[x].getText() == "#" || buttons[x].getText() == "X" || buttons[x].getText() == "O") {
                    if (i - pos == margin) {
                        player1_turn = !player1_turn;
                    } else {
                        if (player1_turn) textfield.setText("Blue turnnn");
                        else textfield.setText("Red turnnn");
                    }
                    break;
                } else if (Integer.parseInt(buttons[x].getText()) > -21 && Integer.parseInt(buttons[x].getText()) < 6) {
                    buttons[x - margin].setText("");
                    if (player1_turn) {
                        redpoint += Integer.parseInt(buttons[x].getText());
                        buttons[x].setText("");
                        buttons[x].setBackground(new Color(150, 150, 150));
                        buttons[x].setForeground(new Color(255, 0, 0));
                        buttons[x].setText("X");
                    } else {
                        bluepoint += Integer.parseInt(buttons[x].getText());
                        buttons[x].setText("");
                        buttons[x].setBackground(new Color(150, 150, 150));
                        buttons[x].setForeground(new Color(0, 0, 255));
                        buttons[x].setText("O");
                    }
                    check(redpoint, bluepoint);
                }
            }
        } else if (i < pos) {
            for (int x = pos - margin; x >= i; x = x - margin) {
                if (buttons[x].getText() == "") {
                    buttons[x + margin].setText("");
                    if (player1_turn) {
                        buttons[x].setForeground(new Color(255, 0, 0));
                        buttons[x].setText("X");
                    } else {
                        buttons[x].setForeground(new Color(0, 0, 255));
                        buttons[x].setText("O");
                    }
                } else if (buttons[x].getText() == "#" || buttons[x].getText() == "X" || buttons[x].getText() == "O") {
                    if (pos - i == 9) {
                        player1_turn = !player1_turn;
                    } else {
                        if (player1_turn) textfield.setText("Blue turnnn");
                        else textfield.setText("Red turnnn");
                    }
                    break;
                } else if (Integer.parseInt(buttons[x].getText()) > -21 && Integer.parseInt(buttons[x].getText()) < 6) {
                    buttons[x + margin].setText("");
                    if (player1_turn) {
                        redpoint += Integer.parseInt(buttons[x].getText());
                        buttons[x].setText("");
                        buttons[x].setBackground(new Color(150, 150, 150));
                        buttons[x].setForeground(new Color(255, 0, 0));
                        buttons[x].setText("X");
                    } else {
                        bluepoint += Integer.parseInt(buttons[x].getText());
                        buttons[x].setText("");
                        buttons[x].setBackground(new Color(150, 150, 150));
                        buttons[x].setForeground(new Color(0, 0, 255));
                        buttons[x].setText("O");

                    }
                    check(redpoint, bluepoint);
                }
            }
        }
        player1_turn = !player1_turn;
    }

    public void firstTurn() {

        if (random.nextInt(2) == 0) {
            player1_turn = true;
            textfield.setText("Red turn");
        } else {
            player1_turn = false;
            textfield.setText("Blue turn");
        }
    }

    public void check(int redpoint, int bluepoint) {

        int collector = 0;
        for (int i = 0; i < margin * margin; i++) {
            if (redpoint < 0 || bluepoint < 0) {
                break;
            }
            if (buttons[i].getText().equals("+1") || buttons[i].getText().equals("+2")
                    || buttons[i].getText().equals("+3") || buttons[i].getText().equals("+4") || buttons[i].getText().equals("+5")) {
                collector += Integer.parseInt(buttons[i].getText());
            }
        }
        if (collector >= (Math.max(redpoint, bluepoint) - Math.min(redpoint, bluepoint))) return;
        else ended = true;

        if (ended) {
            if (redpoint > bluepoint) {
                for (int i = 0; i < margin * margin; i++) {
                    buttons[i].setEnabled(false);
                }
                textfield.setText("Red wins");
            } else {
                for (int i = 0; i < margin * margin; i++) {
                    buttons[i].setEnabled(false);
                }
                textfield.setText("Blue wins");
            }
        } else if (redpoint > bluepoint || bluepoint < 0) {
            for (int i = 0; i < margin * margin; i++) {
                buttons[i].setEnabled(false);
            }
            textfield.setText("Red wins");
        } else if (redpoint < bluepoint || redpoint < 0) {
            for (int i = 0; i < margin * margin; i++) {
                buttons[i].setEnabled(false);
            }
            textfield.setText("Blue wins");
        }
    }

    public int BluePosition() {
        int bluepos = 0;
        for (int i = 0; i < margin * margin; i++) {
            if (buttons[i].getText() == "O") {
                bluepos = i;
                break;
            }
        }
        return bluepos;
    }

    public int RedPosition() {
        int redpos = 0;
        for (int i = 0; i < margin * margin; i++) {
            if (buttons[i].getText() == "X") {
                redpos = i;
                break;
            }
        }
        return redpos;
    }

    public void setWalls() {
        walls = Integer.parseInt(JOptionPane.showInputDialog("How many Walls do you want to place?"));
        while (true) {
            if (walls < 0 || walls > margin * margin) {
                walls = Integer.parseInt(JOptionPane.showInputDialog("walls must be between 0 and " + (margin * margin)));
            } else break;
        }
        if (walls == 0) {
            wall = false;
        }
        return;

    }

    public void setStars() {
        stars = Integer.parseInt(JOptionPane.showInputDialog("How many Stars do you want to place?"));
        while (true) {
            if (stars < 0 || stars > (margin * margin) - walls) {
                stars = Integer.parseInt(JOptionPane.showInputDialog("stars must be between 0 and " + ((margin * margin) - walls)));
            } else break;
        }
        if (stars == 0) {
            star = false;
        }
        return;
    }

    public void setMines() {
        mines = Integer.parseInt(JOptionPane.showInputDialog("How many Mines do you want to place?"));
        while (true) {
            if (mines < 0 || mines > ((margin * margin) - walls) - stars) {
                mines = Integer.parseInt(JOptionPane.showInputDialog("Mines must be between 0 and " + (((margin * margin) - walls) - stars)));
            } else break;
        }
        if (mines == 0) {
            mine = false;
        }
        return;
    }

    public int setStarPoints() {
        int points = Integer.parseInt(JOptionPane.showInputDialog("How many points this Star has?"));
        while (true) {
            if (points > 5 || points < 1)
                points = Integer.parseInt(JOptionPane.showInputDialog("Points must be between 1 and 5"));
            else break;
        }
        return points;
    }

    public int setMinePoints() {
        int points = Integer.parseInt(JOptionPane.showInputDialog("How many points this Mine has?"));
        while (true) {
            if (points != -1 && points != -5 && points != -10 && points != -15 && points != -20)
                points = Integer.parseInt(JOptionPane.showInputDialog("Points must be one of this numbers(-1,-5,-10,-15,-20)"));
            else break;
        }
        return points;
    }

    public int setMargins() {
        if (margin == 0) {
            margin = Integer.parseInt(JOptionPane.showInputDialog("Enter a margin"));
            while (true) {
                if (margin < 8 && margin > 20)
                    margin = Integer.parseInt(JOptionPane.showInputDialog("Margin must be between 8 and 20"));
                else break;
            }
        }
        return margin;
    }

    public int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public void RandomMap() {
        int RandomWall = getRandom(0, ((margin * margin) / 3));
        for (int k = 0; k < RandomWall; k++) {
            int RandmWallPlacement = getRandom(0, (margin * margin));
            if (buttons[RandmWallPlacement].getText() == "" && RandmWallPlacement != RedPosition() - 1 && RandmWallPlacement != RedPosition() - margin
                    && RandmWallPlacement != BluePosition() + 1 && RandmWallPlacement != BluePosition() - margin) {
                buttons[RandmWallPlacement].setForeground(new Color(0, 0, 0));
                buttons[RandmWallPlacement].setBackground(new Color(0, 0, 0));
                buttons[RandmWallPlacement].setText("#");
            } else {
                k--;
            }
        }

        int RandomStar = getRandom(1, ((margin * margin) / 2) - RandomWall + 1);
        for (int k = 0; k < RandomStar; k++) {
            int RandmStarPlacement = getRandom(0, (margin * margin));
            if (buttons[RandmStarPlacement].getText() == "") {
                int points = getRandom(1, 6);
                buttons[RandmStarPlacement].setForeground(new Color(255, 200, 0));
                buttons[RandmStarPlacement].setBackground(new Color(255, 255, 100));
                buttons[RandmStarPlacement].setText("+" + points);
            } else {
                k--;
            }
        }
        int RandomMine = getRandom(0, (((margin * margin) / 2) - RandomWall - RandomStar + 1));
        for (int k = 0; k < RandomMine; k++) {
            int RandmMinePlacement = getRandom(0, (margin * margin));
            if (buttons[RandmMinePlacement].getText() == "" && RandmMinePlacement != RedPosition() - 1 && RandmMinePlacement != RedPosition() - margin
                    && RandmMinePlacement != BluePosition() + 1 && RandmMinePlacement != BluePosition() - margin) {
                int points = getRandom(0, 5);
                switch (points) {
                    case 0:
                        points = -1;
                        break;
                    case 1:
                        points = -5;
                        break;
                    case 2:
                        points = -10;
                        break;
                    case 3:
                        points = -15;
                        break;
                    case 4:
                        points = -20;
                        break;
                }
                buttons[RandmMinePlacement].setForeground(new Color(110, 0, 0));
                buttons[RandmMinePlacement].setBackground(new Color(50, 0, 0));
                buttons[RandmMinePlacement].setText("" + points);
            } else {
                k--;
            }
        }
    }
}