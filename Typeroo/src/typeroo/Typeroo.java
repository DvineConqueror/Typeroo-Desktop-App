/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package typeroo;

/**
 *
 * @author DivineConqueror
 */
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.border.Border;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Typeroo extends javax.swing.JFrame {

    /**
     * Creates new form Typeroo
     */
    
    
    int score = 1;
    int newScore;
    int easyTime = 20;
    int averageTime = 15;
    int difficultTime = 10;
    int lives = 3;
    int easyLives = 5;
    int averageLives = 3;  // Lives for average difficulty
    int hints = 0;  // No hints by default
    int index = -1;
    boolean isEasyMode;
    boolean isHardMode;
    private JLabel timerLabel;
    private Timer timer;
    private int countdown;
    private static int highestScore = 0;
    Border panel_border = BorderFactory.createMatteBorder(1,1,1,1, Color.black);
    private int easyHighScore = 0;
    private int averageHighScore = 0;
    private int hardHighScore = 0;
    private String[] words;
    private String randomWord;
    private String userName;
    private String userName2;
    private String userName3;
    

    
    //Displaying the words
    public void displayWords(){
        if (words.length == 0){
            jLabel21.setText("--Word--");
            jLabel37.setText("--Word--");
            jLabel42.setText("--Word--");
            jTextField_Guess.setText("Guess");
            jTextField_Guess1.setText("Guess");
            jTextField_Guess2.setText("Guess");
        }else{
            Random random = new Random();
            randomWord = words[random.nextInt(words.length)];

            int pos = random.nextInt(randomWord.length());
            StringBuilder newText = new StringBuilder(randomWord);

            if (randomWord.length() > 7) {
                int pos1 = random.nextInt(randomWord.length());
                int pos2 = random.nextInt(randomWord.length());
                
                newText.replace(pos1, pos1 + 1, " _ ");
                newText.replace(pos2, pos2 + 1, " _ ");
            } else {
                newText.replace(pos, pos + 1, " _ ");
            }
            
            jLabel21.setText(newText.toString());
            jLabel37.setText(newText.toString());
            jLabel42.setText(newText.toString());
        }
    }
    
    //Display if user correctly guessed the word
    public void checkWords() {
        if (jTextField_Guess.getText().toLowerCase().equals(randomWord) || jTextField_Guess1.getText().toLowerCase().equals(randomWord) || jTextField_Guess2.getText().toLowerCase().equals(randomWord)) {
            timer.stop();
            JOptionPane.showMessageDialog(null, "Correct!!!");
            if (isEasyMode) {
                countdown = easyTime;
            } else if (isHardMode) {
                countdown = difficultTime;
            } else {
                countdown = averageTime;
            }
            score++;
            newScore = score;
            if (newScore > easyHighScore && isEasyMode) {
                easyHighScore = score;
                nameLeaderboard.setText(userName);
            } else if (newScore > averageHighScore && !isEasyMode && !isHardMode) {
                averageHighScore = score;
                nameLeaderboard2.setText(userName2);
            } else if (newScore > hardHighScore && isHardMode) {
                hardHighScore = score;
                nameLeaderboard3.setText(userName3);
            }
            easyTimer1.setText(Integer.toString(countdown));  // Update display immediately
            average_timer.setText(Integer.toString(countdown));
            difficultTimer1.setText(Integer.toString(countdown));
            timer.start();
        } else {
            timer.stop();
            JOptionPane.showMessageDialog(null, "Incorrect!!! The word was: " + randomWord);
            if (isEasyMode) {
                countdown = easyTime;
            } else if (isHardMode) {
                countdown = difficultTime;
            } else {
                countdown = averageTime;
            } // Reset to average time on wrong guess (all difficulties)
            
            if (score > easyHighScore && isEasyMode) {
                easyHighScore = score;
            } else if (score > averageHighScore && !isEasyMode && !isHardMode) {
                averageHighScore = score;
            } else if (score > hardHighScore && isHardMode) {
                hardHighScore = score;
            }
            averageLives--;
            timer.start();
            if (averageLives == 0) {
                gameOverDialog1.setVisible(true);
                easyDifficultyFrame.setVisible(false);
                middleDifficultyFrame.setVisible(false);
                hardDifficultyFrame.setVisible(false);
                averageLives = easyLives;  // Reset lives based on difficulty on game over
                hints = 0;  // Reset hints to 0
                timer.stop();
            }
        }
        displayHUD();
        jTextField_Guess.setText("");
        jTextField_Guess2.setText("");
        jTextField_Guess1.setText("");
    }
    
    public Typeroo() {
        initComponents();
        this.setLocationRelativeTo(null);
        howToFrame.setLocationRelativeTo(null);
        difficultyFrame.setLocationRelativeTo(null);
        easyDifficultyFrame.setLocationRelativeTo(null);
        middleDifficultyFrame.setLocationRelativeTo(null);
        hardDifficultyFrame.setLocationRelativeTo(null);
        gameOverDialog1.setLocationRelativeTo(null);
        leaderBoardDialog.setLocationRelativeTo(null);
        jPanel4.setBorder(panel_border);
        jPanel6.setBorder(panel_border);
        jPanel7.setBorder(panel_border);
        
        if (isEasyMode) {
            words = readWordsFromFile("easy_words.txt", "easy");
        } else if (isHardMode) {
            words = readWordsFromFile("hard_words.txt", "hard");
        } else {
            words = readWordsFromFile("average_words.txt", "average");
        }
        
        displayWords();
        
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isEasyMode = true;
                isHardMode = false;
                countdown = easyTime;
                averageLives = easyLives;  // Set lives for easy mode
                hints = 5;  // Set hints for easy mode
                words = readWordsFromFile("easy_words.txt", "easy");
            }
        });
        
        jButton6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isEasyMode = false;
                countdown = averageTime;
                averageLives = 3;  // Set lives for average mode
                hints = 3;  // Set hints for average mode (optional)
                words = readWordsFromFile("average_words.txt", "average");
            }
        });
        
        jButton7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isEasyMode = false;
                isHardMode = true;
                countdown = difficultTime;
                averageLives = 3;  // Set lives for average mode
                hints = 0;  // Set hints for average mode (optional)
                words = readWordsFromFile("hard_words.txt", "hard");
            }
        });
        
        // Set initial countdown based on the selected difficulty
        if (isEasyMode){
            countdown = easyTime;
        }else {
            countdown = averageTime; // Default to average for other difficulties
        }
        
        easyTimer1.setText(Integer.toString(easyTime));
        average_timer.setText(Integer.toString(averageTime));
        difficultTimer1.setText(Integer.toString(difficultTime));
        
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdown >= 0) {
                    countdown--;
                    easyTimer1.setText(Integer.toString(countdown));
                    average_timer.setText(Integer.toString(countdown));
                    difficultTimer1.setText(Integer.toString(countdown));
                } else {
                    JOptionPane.showMessageDialog(null, "Times up!!!", "Incorrect!", JOptionPane.WARNING_MESSAGE);
                    lives--;
                    displayHUD();
                    displayWords();
                    timer.stop();
                    if (lives > 0) {
                        countdown = averageTime;
                        displayHUD();
                        timer.start();
                    } else {
                        leaderBoardDialog.setVisible(true);
                        middleDifficultyFrame.setVisible(false);
                        lives = easyLives;  // Reset lives based on difficulty on game over
                        hints = 0;  // Reset hints to 0
                        timer.stop();
                    }
                }
            }
        });
        
        leaderBoardDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                timer.stop();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                timer.start();
            }
        });
    }
    
    public void displayHUD() {
        label_score.setText(Integer.toString(score));
        label_score1.setText(Integer.toString(score));
        label_score2.setText(Integer.toString(score));
        scoreLeaderboard.setText(Integer.toString(easyHighScore));
        scoreLeaderboard2.setText(Integer.toString(averageHighScore));
        scoreLeaderboard3.setText(Integer.toString(hardHighScore));
        label_score.setText(Integer.toString(score));
        label_score1.setText(Integer.toString(score));
        label_score2.setText(Integer.toString(score));
        label_lives.setText(Integer.toString(averageLives));
        label_lives1.setText(Integer.toString(averageLives));
        label_lives2.setText(Integer.toString(averageLives));
        jLabel52.setText(Integer.toString(score));
        easyTimer1.setText(Integer.toString(easyTime));
        average_timer.setText(Integer.toString(averageTime));
        difficultTimer1.setText(Integer.toString(difficultTime));
        label_lives.setText(Integer.toString(averageLives));  // Update lives based on difficulty
        label_lives1.setText(Integer.toString(averageLives));
        label_lives2.setText(Integer.toString(averageLives));
        jLabel52.setText(Integer.toString(score));
        jScrollPane1.setBackground(Color.BLACK);
        jScrollPane1.getViewport().setBackground(new Color(247, 194, 2));
        jScrollPane2.setBackground(Color.BLACK);
        jScrollPane2.getViewport().setBackground(new Color(247, 194, 2));
        jScrollPane3.setBackground(Color.BLACK);
        jScrollPane3.getViewport().setBackground(new Color(247, 194, 2));
        // Update hints display if implemented (optional)
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        howToFrame = new javax.swing.JFrame();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        difficultyFrame = new javax.swing.JFrame();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        exitPanel5 = new javax.swing.JLabel();
        backArrow = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jLabel43 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        highScoreLabel1 = new javax.swing.JLabel();
        nameLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        easyDifficultyFrame = new javax.swing.JFrame();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jTextField_Guess1 = new javax.swing.JTextField();
        jButton_submit1 = new javax.swing.JButton();
        hintButton1 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        exitPanel7 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        exitPanel8 = new javax.swing.JLabel();
        label_lives1 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        easyTimer1 = new javax.swing.JLabel();
        label_score1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLabel37 = new javax.swing.JLabel();
        middleDifficultyFrame = new javax.swing.JFrame();
        jPanel6 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jTextField_Guess = new javax.swing.JTextField();
        jButton_submit = new javax.swing.JButton();
        hintButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        exitPanel3 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        exitPanel2 = new javax.swing.JLabel();
        label_lives = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        average_timer = new javax.swing.JLabel();
        label_score = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLabel21 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        hardDifficultyFrame = new javax.swing.JFrame();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jTextField_Guess2 = new javax.swing.JTextField();
        jButton_submit2 = new javax.swing.JButton();
        jPanel24 = new javax.swing.JPanel();
        exitPanel9 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        exitPanel10 = new javax.swing.JLabel();
        label_lives2 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        difficultTimer1 = new javax.swing.JLabel();
        label_score2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jLabel42 = new javax.swing.JLabel();
        leaderBoardDialog = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        nameLeaderboard = new javax.swing.JLabel();
        nameLeaderboard2 = new javax.swing.JLabel();
        nameLeaderboard3 = new javax.swing.JLabel();
        scoreLeaderboard = new javax.swing.JLabel();
        scoreLeaderboard2 = new javax.swing.JLabel();
        scoreLeaderboard3 = new javax.swing.JLabel();
        difficultyLeaderboard1 = new javax.swing.JLabel();
        difficultyLeaderboard2 = new javax.swing.JLabel();
        difficultyLeaderboard3 = new javax.swing.JLabel();
        backArrow1 = new javax.swing.JLabel();
        exitPanel6 = new javax.swing.JLabel();
        gameOverDialog1 = new javax.swing.JDialog();
        jPanel27 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        noButton1 = new javax.swing.JButton();
        playAgain2 = new javax.swing.JButton();
        jLabel52 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        exitPanel1 = new javax.swing.JLabel();
        howtoPlayPanel = new javax.swing.JLabel();

        howToFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        howToFrame.setBackground(new java.awt.Color(71, 71, 71));
        howToFrame.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        howToFrame.setResizable(false);
        howToFrame.setSize(new java.awt.Dimension(800, 600));

        jPanel8.setBackground(new java.awt.Color(37, 35, 35));
        jPanel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel8.setBackground(new java.awt.Color(51, 51, 255));
        jLabel8.setFont(new java.awt.Font("Tw Cen MT", 1, 42)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("How to Play?");

        jLabel4.setBackground(new java.awt.Color(51, 51, 255));
        jLabel4.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("1. The game displays a word on the screen.");

        jLabel9.setBackground(new java.awt.Color(51, 51, 255));
        jLabel9.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("2. The player enters the word into a text field.");

        jLabel10.setBackground(new java.awt.Color(51, 51, 255));
        jLabel10.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("displayed word, a new word is shown.");

        jLabel11.setBackground(new java.awt.Color(51, 51, 255));
        jLabel11.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("3. If the typed word matches the ");

        jLabel12.setBackground(new java.awt.Color(51, 51, 255));
        jLabel12.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("5. The game ends if the player chooses to quit");

        jLabel13.setBackground(new java.awt.Color(51, 51, 255));
        jLabel13.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("4. If the typed word doesn't match, ");

        jLabel14.setBackground(new java.awt.Color(51, 51, 255));
        jLabel14.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("a dialog box asks if the player wants to play again.");

        jLabel15.setBackground(new java.awt.Color(51, 51, 255));
        jLabel15.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("or types an incorrect word three times in a row.");

        jLabel16.setBackground(new java.awt.Color(51, 51, 255));
        jLabel16.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("number of correct words typed. ");

        jLabel17.setBackground(new java.awt.Color(51, 51, 255));
        jLabel17.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("6. The player's score is based on the ");

        jPanel18.setBackground(new java.awt.Color(71, 71, 71));

        jLabel5.setBackground(new java.awt.Color(51, 51, 255));
        jLabel5.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Typeroo Word Game");

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        jLabel32.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel32MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(152, 152, 152))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel32)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(191, 191, 191))
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addGap(12, 12, 12)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addGap(10, 10, 10)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(107, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout howToFrameLayout = new javax.swing.GroupLayout(howToFrame.getContentPane());
        howToFrame.getContentPane().setLayout(howToFrameLayout);
        howToFrameLayout.setHorizontalGroup(
            howToFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        howToFrameLayout.setVerticalGroup(
            howToFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, howToFrameLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        difficultyFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        difficultyFrame.setBackground(new java.awt.Color(37, 35, 35));
        difficultyFrame.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        difficultyFrame.setPreferredSize(new java.awt.Dimension(800, 600));
        difficultyFrame.setResizable(false);
        difficultyFrame.setSize(new java.awt.Dimension(800, 600));

        jPanel12.setBackground(new java.awt.Color(37, 35, 35));
        jPanel12.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel13.setBackground(new java.awt.Color(71, 71, 71));

        jLabel29.setBackground(new java.awt.Color(51, 51, 255));
        jLabel29.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Typeroo Word Game");

        exitPanel5.setFont(new java.awt.Font("Tw Cen MT", 1, 42)); // NOI18N
        exitPanel5.setForeground(new java.awt.Color(255, 255, 255));
        exitPanel5.setText("X");
        exitPanel5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exitPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitPanel5MouseClicked(evt);
            }
        });

        backArrow.setFont(new java.awt.Font("Tw Cen MT", 1, 42)); // NOI18N
        backArrow.setForeground(new java.awt.Color(255, 255, 255));
        backArrow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        backArrow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backArrow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backArrowMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(backArrow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90)
                .addComponent(exitPanel5)
                .addGap(49, 49, 49))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(backArrow))
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(exitPanel5)))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel14.setBackground(new java.awt.Color(26, 25, 25));

        jPanel15.setBackground(new java.awt.Color(37, 35, 35));
        jPanel15.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel15MouseClicked(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(71, 71, 71));
        jButton2.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("EASY");
        jButton2.setBorder(null);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("  Easy mode for beginners");

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Difficulties/difficulty_level1.png"))); // NOI18N
        jLabel27.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel27MouseClicked(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(255, 255, 255));
        jLabel46.setText("and starters");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel27)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel46)
                .addGap(49, 49, 49))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        jPanel16.setBackground(new java.awt.Color(37, 35, 35));
        jPanel16.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel16.setPreferredSize(new java.awt.Dimension(205, 337));
        jPanel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel16MouseClicked(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(71, 71, 71));
        jButton6.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("MODERATE");
        jButton6.setBorder(null);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Difficulties/difficulty_level2.png"))); // NOI18N
        jLabel43.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel43.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel43MouseClicked(evt);
            }
        });

        jLabel47.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(255, 255, 255));
        jLabel47.setText("Moderate mode for");

        jLabel48.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(255, 255, 255));
        jLabel48.setText("accessibility and difficulty ");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel48))
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel47)
                                    .addComponent(jLabel43))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        jPanel17.setBackground(new java.awt.Color(37, 35, 35));
        jPanel17.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel17.setPreferredSize(new java.awt.Dimension(205, 337));
        jPanel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel17MouseClicked(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(71, 71, 71));
        jButton7.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("HARD");
        jButton7.setBorder(null);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Difficulties/difficulty_level3.png"))); // NOI18N
        jLabel45.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel45.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel45MouseClicked(evt);
            }
        });

        jLabel50.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(255, 255, 255));
        jLabel50.setText("experienced and beyond");

        jLabel49.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(255, 255, 255));
        jLabel49.setText("Hard mode for");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addGap(0, 10, Short.MAX_VALUE)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel45)
                                .addGap(40, 40, 40))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addContainerGap())))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel49)
                .addGap(48, 48, 48))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        jLabel44.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setText("CHOOSE A GAME DIFFICULTY!");

        jPanel25.setBackground(new java.awt.Color(26, 25, 25));

        jPanel26.setBackground(new java.awt.Color(26, 25, 25));

        highScoreLabel1.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        highScoreLabel1.setForeground(new java.awt.Color(255, 255, 255));

        nameLabel2.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        nameLabel2.setForeground(new java.awt.Color(255, 255, 255));
        nameLabel2.setText("                ");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(highScoreLabel1))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nameLabel2)
                .addGap(219, 219, 219))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addComponent(highScoreLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameLabel2)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
        );

        jButton3.setText("Leaderboard");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel44)
                .addGap(207, 207, 207))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1263, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 889, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout difficultyFrameLayout = new javax.swing.GroupLayout(difficultyFrame.getContentPane());
        difficultyFrame.getContentPane().setLayout(difficultyFrameLayout);
        difficultyFrameLayout.setHorizontalGroup(
            difficultyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        difficultyFrameLayout.setVerticalGroup(
            difficultyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, difficultyFrameLayout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        easyDifficultyFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        easyDifficultyFrame.setBackground(new java.awt.Color(76, 73, 229));
        easyDifficultyFrame.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        easyDifficultyFrame.setPreferredSize(new java.awt.Dimension(817, 606));
        easyDifficultyFrame.setResizable(false);
        easyDifficultyFrame.setSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1710, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel20.setBackground(new java.awt.Color(37, 35, 35));
        jPanel20.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField_Guess1.setFont(new java.awt.Font("Tw Cen MT", 0, 36)); // NOI18N
        jTextField_Guess1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Guess1.setText("Guess");
        jTextField_Guess1.setBorder(null);
        jTextField_Guess1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField_Guess1MouseClicked(evt);
            }
        });
        jTextField_Guess1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_Guess1ActionPerformed(evt);
            }
        });
        jTextField_Guess1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField_Guess1KeyPressed(evt);
            }
        });

        jButton_submit1.setBackground(new java.awt.Color(38, 85, 201));
        jButton_submit1.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jButton_submit1.setForeground(new java.awt.Color(255, 255, 255));
        jButton_submit1.setText("Enter");
        jButton_submit1.setBorder(null);
        jButton_submit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_submit1ActionPerformed(evt);
            }
        });

        hintButton1.setBackground(new java.awt.Color(95, 38, 201));
        hintButton1.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        hintButton1.setForeground(new java.awt.Color(255, 255, 255));
        hintButton1.setText("Hint");
        hintButton1.setBorder(null);
        hintButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hintButton1ActionPerformed(evt);
            }
        });

        jPanel21.setBackground(new java.awt.Color(71, 71, 71));

        exitPanel7.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        exitPanel7.setForeground(new java.awt.Color(255, 255, 255));
        exitPanel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        exitPanel7.setText(" X");
        exitPanel7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exitPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitPanel7MouseClicked(evt);
            }
        });

        jLabel33.setBackground(new java.awt.Color(51, 51, 255));
        jLabel33.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("Typeroo Word Game");

        exitPanel8.setBackground(new java.awt.Color(112, 112, 112));
        exitPanel8.setFont(new java.awt.Font("Tw Cen MT", 1, 42)); // NOI18N
        exitPanel8.setForeground(new java.awt.Color(255, 255, 255));
        exitPanel8.setText(" X");
        exitPanel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exitPanel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitPanel8MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(exitPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(exitPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(exitPanel7))
                    .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(exitPanel8)))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        label_lives1.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        label_lives1.setForeground(new java.awt.Color(255, 255, 255));
        label_lives1.setText("3");

        jLabel34.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Score.png"))); // NOI18N
        jLabel34.setText("Score: ");

        jLabel35.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Typeroo-Heart.png"))); // NOI18N
        jLabel35.setText("Lives:");

        jLabel36.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Typeroo-Time-.png"))); // NOI18N
        jLabel36.setText("Time: ");

        easyTimer1.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        easyTimer1.setForeground(new java.awt.Color(255, 255, 255));
        easyTimer1.setText("20");

        label_score1.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        label_score1.setForeground(new java.awt.Color(255, 255, 255));
        label_score1.setText("  ");

        jScrollPane2.setBackground(new java.awt.Color(0, 0, 204));
        jScrollPane2.setBorder(null);
        jScrollPane2.setForeground(new java.awt.Color(0, 153, 255));

        jLabel37.setBackground(new java.awt.Color(247, 194, 2));
        jLabel37.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("W O R D");
        jLabel37.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel37.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jScrollPane2.setViewportView(jLabel37);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addComponent(jButton_submit1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(hintButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(290, 290, 290))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(196, 196, 196))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addComponent(jTextField_Guess1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(259, 259, 259))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(0, 0, 0)
                        .addComponent(label_score1)
                        .addGap(109, 109, 109)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(easyTimer1)
                        .addGap(114, 114, 114)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_lives1)
                        .addGap(105, 105, 105))))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(jLabel35)
                    .addComponent(label_score1)
                    .addComponent(label_lives1)
                    .addComponent(easyTimer1))
                .addGap(73, 73, 73)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jTextField_Guess1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hintButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_submit1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(309, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout easyDifficultyFrameLayout = new javax.swing.GroupLayout(easyDifficultyFrame.getContentPane());
        easyDifficultyFrame.getContentPane().setLayout(easyDifficultyFrameLayout);
        easyDifficultyFrameLayout.setHorizontalGroup(
            easyDifficultyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(easyDifficultyFrameLayout.createSequentialGroup()
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        easyDifficultyFrameLayout.setVerticalGroup(
            easyDifficultyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, easyDifficultyFrameLayout.createSequentialGroup()
                .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(820, 820, 820))
            .addGroup(easyDifficultyFrameLayout.createSequentialGroup()
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        middleDifficultyFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        middleDifficultyFrame.setBackground(new java.awt.Color(76, 73, 229));
        middleDifficultyFrame.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        middleDifficultyFrame.setPreferredSize(new java.awt.Dimension(817, 606));
        middleDifficultyFrame.setResizable(false);
        middleDifficultyFrame.setSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1710, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel9.setBackground(new java.awt.Color(37, 35, 35));
        jPanel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField_Guess.setFont(new java.awt.Font("Tw Cen MT", 0, 36)); // NOI18N
        jTextField_Guess.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Guess.setText("Guess");
        jTextField_Guess.setBorder(null);
        jTextField_Guess.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField_GuessMouseClicked(evt);
            }
        });
        jTextField_Guess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_GuessActionPerformed(evt);
            }
        });
        jTextField_Guess.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField_GuessKeyPressed(evt);
            }
        });

        jButton_submit.setBackground(new java.awt.Color(38, 85, 201));
        jButton_submit.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jButton_submit.setForeground(new java.awt.Color(255, 255, 255));
        jButton_submit.setText("Enter");
        jButton_submit.setBorder(null);
        jButton_submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_submitActionPerformed(evt);
            }
        });

        hintButton.setBackground(new java.awt.Color(95, 38, 201));
        hintButton.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        hintButton.setForeground(new java.awt.Color(255, 255, 255));
        hintButton.setText("Hint");
        hintButton.setBorder(null);
        hintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hintButtonActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(71, 71, 71));

        exitPanel3.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        exitPanel3.setForeground(new java.awt.Color(255, 255, 255));
        exitPanel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        exitPanel3.setText(" X");
        exitPanel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exitPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitPanel3MouseClicked(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(51, 51, 255));
        jLabel18.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Typeroo Word Game");

        exitPanel2.setBackground(new java.awt.Color(112, 112, 112));
        exitPanel2.setFont(new java.awt.Font("Tw Cen MT", 1, 42)); // NOI18N
        exitPanel2.setForeground(new java.awt.Color(255, 255, 255));
        exitPanel2.setText(" X");
        exitPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exitPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitPanel2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(exitPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(exitPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(exitPanel3))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(exitPanel2)))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        label_lives.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        label_lives.setForeground(new java.awt.Color(255, 255, 255));
        label_lives.setText("3");

        jLabel19.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Score.png"))); // NOI18N
        jLabel19.setText("Score: ");

        jLabel3.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Typeroo-Heart.png"))); // NOI18N
        jLabel3.setText("Lives:");

        jLabel20.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Typeroo-Time-.png"))); // NOI18N
        jLabel20.setText("Time: ");

        average_timer.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        average_timer.setForeground(new java.awt.Color(255, 255, 255));
        average_timer.setText("15");

        label_score.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        label_score.setForeground(new java.awt.Color(255, 255, 255));
        label_score.setText("  ");

        jScrollPane1.setBackground(new java.awt.Color(0, 0, 204));
        jScrollPane1.setBorder(null);
        jScrollPane1.setForeground(new java.awt.Color(0, 153, 255));

        jLabel21.setBackground(new java.awt.Color(247, 194, 2));
        jLabel21.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("W O R D");
        jLabel21.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jScrollPane1.setViewportView(jLabel21);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jButton_submit, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(hintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(291, 291, 291))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(196, 196, 196))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jTextField_Guess, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(261, 261, 261))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(0, 0, 0)
                        .addComponent(label_score)
                        .addGap(109, 109, 109)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(average_timer)
                        .addGap(114, 114, 114)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_lives)
                        .addGap(105, 105, 105))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel3)
                    .addComponent(label_score)
                    .addComponent(label_lives)
                    .addComponent(average_timer))
                .addGap(73, 73, 73)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField_Guess, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_submit, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(284, Short.MAX_VALUE))
        );

        jTextField2.setBackground(new java.awt.Color(37, 35, 35));
        jTextField2.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(255, 255, 255));
        jTextField2.setText("Name:");
        jTextField2.setBorder(null);
        jTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField2MouseClicked(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout middleDifficultyFrameLayout = new javax.swing.GroupLayout(middleDifficultyFrame.getContentPane());
        middleDifficultyFrame.getContentPane().setLayout(middleDifficultyFrameLayout);
        middleDifficultyFrameLayout.setHorizontalGroup(
            middleDifficultyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(middleDifficultyFrameLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(middleDifficultyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(middleDifficultyFrameLayout.createSequentialGroup()
                    .addGap(1191, 1191, 1191)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(1192, Short.MAX_VALUE)))
        );
        middleDifficultyFrameLayout.setVerticalGroup(
            middleDifficultyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, middleDifficultyFrameLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(820, 820, 820))
            .addGroup(middleDifficultyFrameLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
            .addGroup(middleDifficultyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(middleDifficultyFrameLayout.createSequentialGroup()
                    .addGap(396, 396, 396)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(397, Short.MAX_VALUE)))
        );

        hardDifficultyFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        hardDifficultyFrame.setBackground(new java.awt.Color(76, 73, 229));
        hardDifficultyFrame.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        hardDifficultyFrame.setPreferredSize(new java.awt.Dimension(817, 606));
        hardDifficultyFrame.setResizable(false);
        hardDifficultyFrame.setSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1710, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel23.setBackground(new java.awt.Color(37, 35, 35));
        jPanel23.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextField_Guess2.setFont(new java.awt.Font("Tw Cen MT", 0, 36)); // NOI18N
        jTextField_Guess2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField_Guess2.setText("Guess");
        jTextField_Guess2.setBorder(null);
        jTextField_Guess2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField_Guess2MouseClicked(evt);
            }
        });
        jTextField_Guess2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_Guess2ActionPerformed(evt);
            }
        });
        jTextField_Guess2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField_Guess2KeyPressed(evt);
            }
        });

        jButton_submit2.setBackground(new java.awt.Color(38, 85, 201));
        jButton_submit2.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jButton_submit2.setForeground(new java.awt.Color(255, 255, 255));
        jButton_submit2.setText("Enter");
        jButton_submit2.setBorder(null);
        jButton_submit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_submit2ActionPerformed(evt);
            }
        });

        jPanel24.setBackground(new java.awt.Color(71, 71, 71));

        exitPanel9.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        exitPanel9.setForeground(new java.awt.Color(255, 255, 255));
        exitPanel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        exitPanel9.setText(" X");
        exitPanel9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exitPanel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitPanel9MouseClicked(evt);
            }
        });

        jLabel38.setBackground(new java.awt.Color(51, 51, 255));
        jLabel38.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Typeroo Word Game");

        exitPanel10.setBackground(new java.awt.Color(112, 112, 112));
        exitPanel10.setFont(new java.awt.Font("Tw Cen MT", 1, 42)); // NOI18N
        exitPanel10.setForeground(new java.awt.Color(255, 255, 255));
        exitPanel10.setText(" X");
        exitPanel10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exitPanel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitPanel10MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(exitPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(exitPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(exitPanel9))
                    .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(exitPanel10)))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        label_lives2.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        label_lives2.setForeground(new java.awt.Color(255, 255, 255));
        label_lives2.setText("3");

        jLabel39.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Score.png"))); // NOI18N
        jLabel39.setText("Score: ");

        jLabel40.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Typeroo-Heart.png"))); // NOI18N
        jLabel40.setText("Lives:");

        jLabel41.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Typeroo-Time-.png"))); // NOI18N
        jLabel41.setText("Time: ");

        difficultTimer1.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        difficultTimer1.setForeground(new java.awt.Color(255, 255, 255));
        difficultTimer1.setText("10");

        label_score2.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        label_score2.setForeground(new java.awt.Color(255, 255, 255));
        label_score2.setText("  ");

        jScrollPane3.setBackground(new java.awt.Color(0, 0, 204));
        jScrollPane3.setBorder(null);
        jScrollPane3.setForeground(new java.awt.Color(0, 153, 255));

        jLabel42.setBackground(new java.awt.Color(247, 194, 2));
        jLabel42.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("W O R D");
        jLabel42.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel42.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jScrollPane3.setViewportView(jLabel42);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(196, 196, 196))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                        .addComponent(jTextField_Guess2, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(263, 263, 263))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                        .addComponent(jButton_submit2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(341, 341, 341))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addGap(0, 0, 0)
                        .addComponent(label_score2)
                        .addGap(109, 109, 109)
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(difficultTimer1)
                        .addGap(114, 114, 114)
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_lives2)
                        .addGap(103, 103, 103))))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41)
                    .addComponent(jLabel40)
                    .addComponent(label_score2)
                    .addComponent(label_lives2)
                    .addComponent(difficultTimer1))
                .addGap(73, 73, 73)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField_Guess2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jButton_submit2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(294, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout hardDifficultyFrameLayout = new javax.swing.GroupLayout(hardDifficultyFrame.getContentPane());
        hardDifficultyFrame.getContentPane().setLayout(hardDifficultyFrameLayout);
        hardDifficultyFrameLayout.setHorizontalGroup(
            hardDifficultyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hardDifficultyFrameLayout.createSequentialGroup()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        hardDifficultyFrameLayout.setVerticalGroup(
            hardDifficultyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hardDifficultyFrameLayout.createSequentialGroup()
                .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(820, 820, 820))
            .addGroup(hardDifficultyFrameLayout.createSequentialGroup()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        leaderBoardDialog.setPreferredSize(new java.awt.Dimension(430, 333));
        leaderBoardDialog.setSize(new java.awt.Dimension(430, 333));

        jPanel2.setBackground(new java.awt.Color(71, 71, 71));

        jLabel23.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("GAME OVER");

        jPanel3.setBackground(new java.awt.Color(37, 35, 35));

        jLabel25.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Leaderboard");

        jLabel26.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("   ");

        nameLeaderboard.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        nameLeaderboard.setForeground(new java.awt.Color(255, 255, 255));
        nameLeaderboard.setText("Name");

        nameLeaderboard2.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        nameLeaderboard2.setForeground(new java.awt.Color(255, 255, 255));
        nameLeaderboard2.setText("Name");

        nameLeaderboard3.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        nameLeaderboard3.setForeground(new java.awt.Color(255, 255, 255));
        nameLeaderboard3.setText("Name");

        scoreLeaderboard.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        scoreLeaderboard.setForeground(new java.awt.Color(255, 255, 255));
        scoreLeaderboard.setText("0");

        scoreLeaderboard2.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        scoreLeaderboard2.setForeground(new java.awt.Color(255, 255, 255));
        scoreLeaderboard2.setText("0");

        scoreLeaderboard3.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        scoreLeaderboard3.setForeground(new java.awt.Color(255, 255, 255));
        scoreLeaderboard3.setText("0");

        difficultyLeaderboard1.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        difficultyLeaderboard1.setForeground(new java.awt.Color(255, 255, 255));
        difficultyLeaderboard1.setText("Average");

        difficultyLeaderboard2.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        difficultyLeaderboard2.setForeground(new java.awt.Color(255, 255, 255));
        difficultyLeaderboard2.setText("Hard");

        difficultyLeaderboard3.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        difficultyLeaderboard3.setForeground(new java.awt.Color(255, 255, 255));
        difficultyLeaderboard3.setText("Easy");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(nameLeaderboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(difficultyLeaderboard3)
                                .addGap(78, 78, 78))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel25)))
                        .addGap(30, 30, 30)
                        .addComponent(scoreLeaderboard, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(nameLeaderboard3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(difficultyLeaderboard2)
                                .addGap(107, 107, 107)
                                .addComponent(scoreLeaderboard3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(nameLeaderboard2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(difficultyLeaderboard1)
                                .addGap(68, 68, 68)
                                .addComponent(scoreLeaderboard2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(scoreLeaderboard, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(difficultyLeaderboard3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nameLeaderboard))))
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLeaderboard2)
                    .addComponent(difficultyLeaderboard1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scoreLeaderboard2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLeaderboard3)
                    .addComponent(difficultyLeaderboard2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scoreLeaderboard3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        backArrow1.setFont(new java.awt.Font("Tw Cen MT", 1, 42)); // NOI18N
        backArrow1.setForeground(new java.awt.Color(255, 255, 255));
        backArrow1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        backArrow1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backArrow1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backArrow1MouseClicked(evt);
            }
        });

        exitPanel6.setFont(new java.awt.Font("Tw Cen MT", 1, 42)); // NOI18N
        exitPanel6.setForeground(new java.awt.Color(255, 255, 255));
        exitPanel6.setText("X");
        exitPanel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exitPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitPanel6MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(backArrow1)
                .addGap(32, 32, 32)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(exitPanel6)
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(exitPanel6)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(backArrow1)))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout leaderBoardDialogLayout = new javax.swing.GroupLayout(leaderBoardDialog.getContentPane());
        leaderBoardDialog.getContentPane().setLayout(leaderBoardDialogLayout);
        leaderBoardDialogLayout.setHorizontalGroup(
            leaderBoardDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        leaderBoardDialogLayout.setVerticalGroup(
            leaderBoardDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        gameOverDialog1.setSize(new java.awt.Dimension(430, 333));

        jPanel27.setBackground(new java.awt.Color(71, 71, 71));

        jLabel30.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("GAME OVER");

        jPanel28.setBackground(new java.awt.Color(37, 35, 35));

        jLabel31.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("Play Again?");

        jLabel51.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(255, 255, 255));
        jLabel51.setText("Final Score: ");

        noButton1.setBackground(new java.awt.Color(185, 24, 24));
        noButton1.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        noButton1.setForeground(new java.awt.Color(255, 255, 255));
        noButton1.setText("No");
        noButton1.setBorder(null);
        noButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noButton1ActionPerformed(evt);
            }
        });

        playAgain2.setBackground(new java.awt.Color(24, 185, 58));
        playAgain2.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        playAgain2.setForeground(new java.awt.Color(255, 255, 255));
        playAgain2.setText("Yes");
        playAgain2.setBorder(null);
        playAgain2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playAgain2ActionPerformed(evt);
            }
        });

        jLabel52.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(255, 255, 255));
        jLabel52.setText("   ");

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel28Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addGap(144, 144, 144))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel28Layout.createSequentialGroup()
                        .addComponent(playAgain2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(noButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(114, 114, 114))))
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel52)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel28Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(jLabel52))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jLabel31)
                .addGap(29, 29, 29)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(noButton1)
                    .addComponent(playAgain2))
                .addGap(41, 41, 41))
        );

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap(96, Short.MAX_VALUE)
                .addComponent(jLabel30)
                .addGap(77, 77, 77))
            .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout gameOverDialog1Layout = new javax.swing.GroupLayout(gameOverDialog1.getContentPane());
        gameOverDialog1.getContentPane().setLayout(gameOverDialog1Layout);
        gameOverDialog1Layout.setHorizontalGroup(
            gameOverDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        gameOverDialog1Layout.setVerticalGroup(
            gameOverDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(37, 35, 35));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(800, 600));
        setResizable(false);
        setSize(new java.awt.Dimension(800, 600));

        jPanel5.setBackground(new java.awt.Color(37, 35, 35));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Typeroo-Logoasdasd.png"))); // NOI18N

        jButton1.setBackground(new java.awt.Color(71, 71, 71));
        jButton1.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("PLAY GAME");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Typeroo is a simple yet addictive game that helps ");

        jLabel7.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("you improve your typing skills while having fun!");

        jPanel10.setBackground(new java.awt.Color(71, 71, 71));

        jLabel1.setBackground(new java.awt.Color(51, 51, 255));
        jLabel1.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Typeroo Word Game");

        exitPanel1.setFont(new java.awt.Font("Tw Cen MT", 1, 42)); // NOI18N
        exitPanel1.setForeground(new java.awt.Color(255, 255, 255));
        exitPanel1.setText("X");
        exitPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exitPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitPanel1MouseClicked(evt);
            }
        });

        howtoPlayPanel.setFont(new java.awt.Font("Tw Cen MT", 1, 42)); // NOI18N
        howtoPlayPanel.setForeground(new java.awt.Color(255, 255, 255));
        howtoPlayPanel.setText("?");
        howtoPlayPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        howtoPlayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                howtoPlayPanelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(73, Short.MAX_VALUE)
                .addComponent(howtoPlayPanel)
                .addGap(63, 63, 63)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90)
                .addComponent(exitPanel1)
                .addGap(49, 49, 49))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exitPanel1)
                    .addComponent(howtoPlayPanel))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(254, 254, 254)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(265, 265, 265))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(199, 199, 199))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(189, 189, 189))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(311, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.setVisible(false);
        difficultyFrame.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField_GuessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_GuessActionPerformed

    }//GEN-LAST:event_jTextField_GuessActionPerformed

    private void jButton_submitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_submitActionPerformed
        checkWords();
        if (index<words.length - 1){
            index++;
            displayWords();
        }
    }//GEN-LAST:event_jButton_submitActionPerformed

    private void jTextField_GuessKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_GuessKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButton_submitActionPerformed(null); // Trigger the action performed by the login button
        }  
    }//GEN-LAST:event_jTextField_GuessKeyPressed

    private void jTextField_GuessMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField_GuessMouseClicked
        jTextField_Guess.setText("");
    }//GEN-LAST:event_jTextField_GuessMouseClicked

    private void exitPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel1MouseClicked
        // To Close
        System.exit(0);
    }//GEN-LAST:event_exitPanel1MouseClicked

    private void hintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hintButtonActionPerformed
        timer.stop();
        if (hints > 0) {
        hints--;
        JOptionPane.showMessageDialog(null, "The word is " + randomWord + "\nYour hints are only: " + hints);
        }
        if (hints == 0) {
            JOptionPane.showMessageDialog(null, "All of your hints are used!!!");
        }
        timer.start();
    }//GEN-LAST:event_hintButtonActionPerformed

    private void exitPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel2MouseClicked
        timer.stop();
        JOptionPane.showMessageDialog(null, "Thanks for playing!!!");
        System.exit(0);
    }//GEN-LAST:event_exitPanel2MouseClicked

    private void exitPanel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel3MouseClicked
        this.setVisible(true);
        middleDifficultyFrame.setVisible(false);
        timer.stop();
        score = 0;
    }//GEN-LAST:event_exitPanel3MouseClicked

    private void howtoPlayPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_howtoPlayPanelMouseClicked
        this.setVisible(false);
        howToFrame.setVisible(true);
    }//GEN-LAST:event_howtoPlayPanelMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        userName = JOptionPane.showInputDialog("Please enter you're name: ");
        difficultyFrame.setVisible(false);
        easyDifficultyFrame.setVisible(true);
        String selectedDifficulty = "easy";
        score = 0;
        index = 0;
        lives = 5;
        hints = 5;
        countdown = easyTime;
        timer.start();
        displayHUD();
        displayWords();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void exitPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel5MouseClicked
        JOptionPane.showMessageDialog(null, "Thanks for playing!!!");
        System.exit(0);
    }//GEN-LAST:event_exitPanel5MouseClicked

    private void backArrowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backArrowMouseClicked
        this.setVisible(true);
        difficultyFrame.setVisible(false);
    }//GEN-LAST:event_backArrowMouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        userName2 = JOptionPane.showInputDialog("Please enter you're name: ");
        difficultyFrame.setVisible(false);
        middleDifficultyFrame.setVisible(true);
        String selectedDifficulty = "average";
        score = 0;
        index = 0;
        lives = 3;
        hints = 4;
        countdown = averageTime;
        timer.start();
        displayHUD();
        displayWords();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        userName3 = JOptionPane.showInputDialog("Please enter you're name: ");
        difficultyFrame.setVisible(false);
        hardDifficultyFrame.setVisible(true);
        String selectedDifficulty = "hard";
        score = 0;
        index = 0;
        lives = 3;
        countdown = difficultTime;
        timer.start();
        displayHUD();
        displayWords();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jLabel32MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel32MouseClicked
        howToFrame.setVisible(false);
        this.setVisible(true);
    }//GEN-LAST:event_jLabel32MouseClicked

    private void jTextField_Guess1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField_Guess1MouseClicked
        jTextField_Guess1.setText("");
    }//GEN-LAST:event_jTextField_Guess1MouseClicked

    private void jTextField_Guess1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_Guess1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_Guess1ActionPerformed

    private void jTextField_Guess1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_Guess1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButton_submit1ActionPerformed(null); // Trigger the action performed by the login button
        }
    }//GEN-LAST:event_jTextField_Guess1KeyPressed

    private void jButton_submit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_submit1ActionPerformed
        checkWords();
        if (index<words.length - 1){
            index++;
            displayWords();
        }
    }//GEN-LAST:event_jButton_submit1ActionPerformed

    private void hintButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hintButton1ActionPerformed
        timer.stop();
        if (hints > 0) {
            hints--;
            JOptionPane.showMessageDialog(null, "The word is " + randomWord + "\nYour hints are only: " + hints);
        }
        if (hints == 0) {
            JOptionPane.showMessageDialog(null, "All of your hints are used!!!");
        }
        timer.start();
    }//GEN-LAST:event_hintButton1ActionPerformed

    private void exitPanel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel7MouseClicked
        this.setVisible(true);
        easyDifficultyFrame.setVisible(false);
        timer.stop();
        score = 0;
    }//GEN-LAST:event_exitPanel7MouseClicked

    private void exitPanel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel8MouseClicked
        timer.stop();
        JOptionPane.showMessageDialog(null, "Thanks for playing!!!");
        System.exit(0);
    }//GEN-LAST:event_exitPanel8MouseClicked

    private void jTextField_Guess2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField_Guess2MouseClicked
        jTextField_Guess2.setText("");
    }//GEN-LAST:event_jTextField_Guess2MouseClicked

    private void jTextField_Guess2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_Guess2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_Guess2ActionPerformed

    private void jTextField_Guess2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_Guess2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButton_submit2ActionPerformed(null); // Trigger the action performed by the login button
        }  
    }//GEN-LAST:event_jTextField_Guess2KeyPressed

    private void jButton_submit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_submit2ActionPerformed
        checkWords();
        if (index<words.length - 1){
            index++;
            displayWords();
        }
    }//GEN-LAST:event_jButton_submit2ActionPerformed

    private void exitPanel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel9MouseClicked
        this.setVisible(true);
        hardDifficultyFrame.setVisible(false);
        timer.stop();
        score = 0;
    }//GEN-LAST:event_exitPanel9MouseClicked

    private void exitPanel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel10MouseClicked
        timer.stop();
        JOptionPane.showMessageDialog(null, "Thanks for playing!!!");
        System.exit(0);
    }//GEN-LAST:event_exitPanel10MouseClicked

    private void jTextField2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField2MouseClicked
        jTextField2.setText("");
    }//GEN-LAST:event_jTextField2MouseClicked

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed

    }//GEN-LAST:event_jTextField2KeyPressed

    private void jPanel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel16MouseClicked
        jButton6ActionPerformed(null);
    }//GEN-LAST:event_jPanel16MouseClicked

    private void jLabel43MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel43MouseClicked
        jButton6ActionPerformed(null);
    }//GEN-LAST:event_jLabel43MouseClicked

    private void jLabel27MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel27MouseClicked
        jButton2ActionPerformed(null);
    }//GEN-LAST:event_jLabel27MouseClicked

    private void jPanel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel15MouseClicked
        jButton2ActionPerformed(null);
    }//GEN-LAST:event_jPanel15MouseClicked

    private void jLabel45MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel45MouseClicked
        jButton7ActionPerformed(null);
    }//GEN-LAST:event_jLabel45MouseClicked

    private void jPanel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel17MouseClicked
        jButton7ActionPerformed(null);
    }//GEN-LAST:event_jPanel17MouseClicked

    private void noButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noButton1ActionPerformed
        JOptionPane.showMessageDialog(null, "Thanks for Playing!");
        System.exit(0);
    }//GEN-LAST:event_noButton1ActionPerformed

    private void playAgain2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playAgain2ActionPerformed
        gameOverDialog1.setVisible(false);
        difficultyFrame.setVisible(true);
    }//GEN-LAST:event_playAgain2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        difficultyFrame.setVisible(false);
        leaderBoardDialog.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void backArrow1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backArrow1MouseClicked
        leaderBoardDialog.setVisible(false);
        difficultyFrame.setVisible(true);
    }//GEN-LAST:event_backArrow1MouseClicked

    private void exitPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel6MouseClicked
        JOptionPane.showMessageDialog(null, "Thanks for playing!!!");
        System.exit(0);
    }//GEN-LAST:event_exitPanel6MouseClicked

    
    
    /**
     * @param args the command line arguments
     */
    
    public static String[] readWordsFromFile(String fileName, String difficultyLevel) {
        List<String> wordList = new ArrayList<>();
        String filePath = "";
        
            switch (difficultyLevel) {
            case "easy":
                filePath = "easy_words.txt";
                break;
            case "average":
                filePath = "average_words.txt";
                break;
            case "hard":
                filePath = "hard_words.txt";
                break;
            default:
                System.err.println("Invalid difficulty level: " + difficultyLevel);
                return new String[0];
        }
            
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into individual words and add them to the list
                String[] wordsInLine = line.trim().split("\\s+");
                for (String word : wordsInLine) {
                    wordList.add(word.toLowerCase());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }

        // Convert the list of words to a String array
        return wordList.toArray(new String[0]);
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Typeroo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Typeroo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Typeroo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Typeroo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    UIManager.setLookAndFeel("com.jtattoo.plaf.aluminumLookAndFeel");
                }catch(Exception ee){
                }
                new Typeroo().setVisible(true);
            }
        });
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel average_timer;
    private javax.swing.JLabel backArrow;
    private javax.swing.JLabel backArrow1;
    private javax.swing.JLabel difficultTimer1;
    private javax.swing.JFrame difficultyFrame;
    private javax.swing.JLabel difficultyLeaderboard1;
    private javax.swing.JLabel difficultyLeaderboard2;
    private javax.swing.JLabel difficultyLeaderboard3;
    private javax.swing.JFrame easyDifficultyFrame;
    private javax.swing.JLabel easyTimer1;
    private javax.swing.JLabel exitPanel1;
    private javax.swing.JLabel exitPanel10;
    private javax.swing.JLabel exitPanel2;
    private javax.swing.JLabel exitPanel3;
    private javax.swing.JLabel exitPanel5;
    private javax.swing.JLabel exitPanel6;
    private javax.swing.JLabel exitPanel7;
    private javax.swing.JLabel exitPanel8;
    private javax.swing.JLabel exitPanel9;
    private javax.swing.JDialog gameOverDialog1;
    private javax.swing.JFrame hardDifficultyFrame;
    private javax.swing.JLabel highScoreLabel1;
    private javax.swing.JButton hintButton;
    private javax.swing.JButton hintButton1;
    private javax.swing.JFrame howToFrame;
    private javax.swing.JLabel howtoPlayPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton_submit;
    private javax.swing.JButton jButton_submit1;
    private javax.swing.JButton jButton_submit2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField_Guess;
    private javax.swing.JTextField jTextField_Guess1;
    private javax.swing.JTextField jTextField_Guess2;
    private javax.swing.JLabel label_lives;
    private javax.swing.JLabel label_lives1;
    private javax.swing.JLabel label_lives2;
    private javax.swing.JLabel label_score;
    private javax.swing.JLabel label_score1;
    private javax.swing.JLabel label_score2;
    private javax.swing.JDialog leaderBoardDialog;
    private javax.swing.JFrame middleDifficultyFrame;
    private javax.swing.JLabel nameLabel2;
    private javax.swing.JLabel nameLeaderboard;
    private javax.swing.JLabel nameLeaderboard2;
    private javax.swing.JLabel nameLeaderboard3;
    private javax.swing.JButton noButton1;
    private javax.swing.JButton playAgain2;
    private javax.swing.JLabel scoreLeaderboard;
    private javax.swing.JLabel scoreLeaderboard2;
    private javax.swing.JLabel scoreLeaderboard3;
    // End of variables declaration//GEN-END:variables
}
