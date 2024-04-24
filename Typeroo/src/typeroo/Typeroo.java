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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class Typeroo extends javax.swing.JFrame {

    //Initialization of all the variables
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
    private Timer timer;
    private float currentVolume = 1.0f;
    private int countdown;
    private int easyHighScore = 0;
    private int averageHighScore = 0;
    private int hardHighScore = 0;
    private String[] words;
    private String userName;
    private String userName2;
    private String userName3;
    private boolean isMuted = false;
    private String currentWord;
    static Clip clip;
    static Clip clip2;
    static Clip clip3;
    static Clip clip4;
    static Clip clip5;
    static Clip gameOver;
    static boolean isOriginalMusicPlaying = false;
    Border panel_border = BorderFactory.createMatteBorder(1,1,1,1, Color.black);
    
    
    //Displaying the words
    private void displayWords() {
        if (words == null || words.length == 0) {
            displayEmptyWordLabels();
        } else {
            if (currentWord == null) {
                currentWord = getRandomWord(); // Generate the random word only if it hasn't been set yet
            }
            if (currentWord != null) {
                String[] parts = currentWord.split(" - ");
                if (parts.length > 0) {
                    String word = parts[0];
                    String obfuscatedWord = obfuscateWord(word);
                    displayWordLabels(obfuscatedWord);
                } else {
                    // Handle case where currentWord does not contain the expected format
                    System.out.println("Error: currentWord does not contain expected format");
                }
            } else {
                // Handle case where currentWord is null
                System.out.println("Error: currentWord is null");
            }
        }
    }

    //This is to display the label for the words invisible
    private void displayEmptyWordLabels() {
        String emptyWord = "--Word--";
        String guessText = "Guess";
        jLabel21.setText(emptyWord);
        jLabel37.setText(emptyWord);
        jLabel42.setText(emptyWord);
        jTextField_Guess.setText(guessText);
        jTextField_Guess1.setText(guessText);
        jTextField_Guess2.setText(guessText);
    }

    private String getRandomWord() {
        Random random = new Random();
        return words[random.nextInt(words.length)];
    }
    
    private String getCurrentWord() {
        String[] parts = currentWord.split(" - ");
        if (parts.length > 0) {
            return parts[0]; // Extract only the word part not the description of the word.
        }
        return null;
    }

    //Obfuscates the word for the challenge
    private String obfuscateWord(String word) {
        StringBuilder newText = new StringBuilder(word);
        int numUnderscores = getNumUnderscores(word);
        Random random = new Random();

        for (int i = 0; i < numUnderscores; i++) {
            int underscorePos = getValidUnderscorePosition(random, word, newText);
            newText.replace(underscorePos, underscorePos + 1, " _ ");
        }

        return newText.toString();
    }

    //Used to calculate how many underscores for the word to be obfuscated or hidden
    private int getNumUnderscores(String word) {
        if (isHardMode) {
            return 3;
        } else if (word.length() > 8) {
            return 2;
        } else {
            return 1;
        }
    }

    //This is how to calculate the position of the underscore
    private int getValidUnderscorePosition(Random random, String word, StringBuilder newText) {
        int underscorePos;
        do {
            underscorePos = random.nextInt(word.length());
        } while (newText.charAt(underscorePos) == ' ');
        return underscorePos;
    }

    //This is to display the label for the hidden or obfuscated words
    private void displayWordLabels(String obfuscatedWord) {
        jLabel21.setText(obfuscatedWord);
        jLabel37.setText(obfuscatedWord);
        jLabel42.setText(obfuscatedWord);
    }

    //Calculate the checking of the words
    public void checkWords() {
        String userGuess = getUserGuess().toLowerCase();
        String[] parts = currentWord.split(" - ");
        String word = parts[0].toLowerCase(); // Extract only the word part

        if (userGuess.equals(word)) {
            handleCorrectGuess();
            currentWord = null; // Reset currentWord after correct guess
        } else {
            handleIncorrectGuess();
            currentWord = null; // Reset currentWord after incorrect guess
        }

        clearGuessFields();
        displayHUD();
    }
    
    //Gets the input of the user through the text field
    private String getUserGuess() {
        String guess1 = jTextField_Guess.getText().toLowerCase();
        String guess2 = jTextField_Guess1.getText().toLowerCase();
        String guess3 = jTextField_Guess2.getText().toLowerCase();

        return Stream.of(guess1, guess2, guess3)
                     .filter(guess -> !guess.isEmpty())
                     .findFirst()
                     .orElse("");
    }

    //Calculate if user's answer is correct
    private void handleCorrectGuess() {
        timer.stop();
        JOptionPane.showMessageDialog(null, "Correct!!!");
        resetCountdownForDifficulty();
        incrementScore();
        updateHighScoreAndLeaderboard();
        displayUpdatedCountdown();
        timer.start();
    }

    //This resets the countdown
    private void resetCountdownForDifficulty() {
        if (isEasyMode) {
            countdown = easyTime;
        } else if (isHardMode) {
            countdown = difficultTime;
        } else {
            countdown = averageTime;
        }
    }

    private void incrementScore() {
        score++;
        newScore = score;
    }

    //Calculates the score for the local leaderboard
    private void updateHighScoreAndLeaderboard() {
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
    }

    private void displayUpdatedCountdown() {
        easyTimer1.setText(Integer.toString(countdown));
        average_timer.setText(Integer.toString(countdown));
        difficultTimer1.setText(Integer.toString(countdown));
    }

    private void handleIncorrectGuess() {
        timer.stop();
        JOptionPane.showMessageDialog(null, "Incorrect Word!", "Swift Typers", JOptionPane.ERROR_MESSAGE);
        resetCountdownForDifficulty();
        updateHighScore();
        decrementLives();
        timer.start();

        if (averageLives == 0) {
            gameOver();
        }
    }
    
    private void handleTimeExpired(){
        timer.stop();
        handleRunOutOfTime();
    }
    
    private void handleRunOutOfTime(){
        JOptionPane.showMessageDialog(null, "You ran out of time!", "Swift Typers", JOptionPane.ERROR_MESSAGE);
        resetCountdownForDifficulty();
        currentWord = null; // Reset currentWord after incorrect guess
        updateHighScore();
        decrementLives();
        timer.start();

        if (averageLives == 0) {
            gameOver();
        }
    }

    private void updateHighScore() {
        if (score > easyHighScore && isEasyMode) {
            easyHighScore = score;
        } else if (score > averageHighScore && !isEasyMode && !isHardMode) {
            averageHighScore = score;
        } else if (score > hardHighScore && isHardMode) {
            hardHighScore = score;
        }
    }

    private void decrementLives() {
        averageLives--;
    }

    private void clearGuessFields() {
        jTextField_Guess.setText("");
        jTextField_Guess2.setText("");
        jTextField_Guess1.setText("");
    }
    
    public Typeroo() {
        initComponents();
        setWindowLocations();
        setBorders();
        setDifficultyListeners();
    }

    private void setWindowLocations() {
        this.setLocationRelativeTo(null);
        howToPlay1.setLocationRelativeTo(null);
        howToPlay2.setLocationRelativeTo(null);
        howToPlay3.setLocationRelativeTo(null);
        howToPlay4.setLocationRelativeTo(null);
        howToPlay5.setLocationRelativeTo(null);
        howToPlay6.setLocationRelativeTo(null);
        howToPlay7.setLocationRelativeTo(null);
        difficultyFrame.setLocationRelativeTo(null);
        easyDifficultyFrame.setLocationRelativeTo(null);
        middleDifficultyFrame.setLocationRelativeTo(null);
        hardDifficultyFrame.setLocationRelativeTo(null);
        gameOverDialog1.setLocationRelativeTo(null);
        leaderBoardDialog.setLocationRelativeTo(null);
        musicSettings.setLocationRelativeTo(null);
    }

    private void setBorders() {
        Border panelBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK);
        jPanel4.setBorder(panelBorder);
        jPanel6.setBorder(panelBorder);
    }

    private void setDifficultyListeners() {
        jButton2.addActionListener(new EasyDifficultyListener());
        jButton6.addActionListener(new AverageDifficultyListener());
        jButton7.addActionListener(new HardDifficultyListener());
    }

    private class EasyDifficultyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setEasyDifficulty();
            startTimer();
        }

        private void setEasyDifficulty() {
            isEasyMode = true;
            isHardMode = false;
            countdown = easyTime;
            averageLives = easyLives;
            hints = 5;
            words = readWordsFromJSONFile("easy_words.json", "easy");
        }
    }

    private class AverageDifficultyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setAverageDifficulty();
            startTimer();
        }

        private void setAverageDifficulty() {
            isEasyMode = false;
            isHardMode = false;
            countdown = averageTime;
            averageLives = 3;
            hints = 3;
            words = readWordsFromJSONFile("average_words.json", "average");
        }
    }

    private class HardDifficultyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setHardDifficulty();
            startTimer();
        }

        private void setHardDifficulty() {
            isEasyMode = false;
            isHardMode = true;
            countdown = difficultTime;
            averageLives = 3;
            hints = 0;
            words = readWordsFromJSONFile("hard_words.json", "hard");
        }
    }

    // Method to start the timer
    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdown > 0) {
                    countdown--;
                    easyTimer1.setText(Integer.toString(countdown));
                    average_timer.setText(Integer.toString(countdown));
                    difficultTimer1.setText(Integer.toString(countdown));
                } else {
                    // Handle timer expiration
                    handleTimeExpired();
                    handleTimerExpiration();
                }
            }
        });
        timer.start();
    }

    // Method to handle timer expiration
    private void handleTimerExpiration() {
        if (gameOverDialog1.isVisible()) {
            timer.stop();
            return; // Exit the actionPerformed method
        }
        resetTimerCountdown();
        lives--;
        displayHUD();
        displayWords();
        timer.stop();
        if (lives > 0) {
            displayHUD();
            timer.start();
        } else {
            // Game over
            gameOver();
        }
    }

    // Method to reset timer countdown based on difficulty
    private void resetTimerCountdown() {
        if (isEasyMode) {
            countdown = easyTime;
        } else if (isHardMode) {
            countdown = difficultTime;
        } else {
            countdown = averageTime;
        }
    }

    // Method to handle game over
    private void gameOver() {
        timer.stop();
        easyDifficultyFrame.dispose();
        middleDifficultyFrame.dispose();
        hardDifficultyFrame.dispose();
        stopAllMusicClips();
        gameOverDialog1.setVisible(true);
        lives = easyLives;  // Reset lives based on difficulty on game over
        hints = 0;  // Reset hints to 0
        try {
            // Play game over sound
            gameOverMusic();
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("typeroo/resources/Music/Game_Over_Sound.wav");
            AudioInputStream newAudioStream = AudioSystem.getAudioInputStream(inputStream);
            Clip clip = AudioSystem.getClip();
            clip.open(newAudioStream);
            applyVolumeToAllModes();
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle any exceptions that might occur during loading/playing the new music
        }
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
        jButton3 = new javax.swing.JButton();
        menuIcon2 = new javax.swing.JLabel();
        easyDifficultyFrame = new javax.swing.JFrame();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jTextField_Guess1 = new javax.swing.JTextField();
        jButton_submit1 = new javax.swing.JButton();
        hintButton1 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        homePanel1 = new javax.swing.JLabel();
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
        menuIcon1 = new javax.swing.JLabel();
        middleDifficultyFrame = new javax.swing.JFrame();
        jPanel6 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jTextField_Guess = new javax.swing.JTextField();
        jButton_submit = new javax.swing.JButton();
        hintButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        homePanel2 = new javax.swing.JLabel();
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
        menuIcon3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        hardDifficultyFrame = new javax.swing.JFrame();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jTextField_Guess2 = new javax.swing.JTextField();
        jButton_submit2 = new javax.swing.JButton();
        jPanel24 = new javax.swing.JPanel();
        homePanel3 = new javax.swing.JLabel();
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
        menuIcon4 = new javax.swing.JLabel();
        leaderBoardDialog = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        nameLeaderboard1 = new javax.swing.JLabel();
        nameLeaderboard = new javax.swing.JLabel();
        nameLeaderboard2 = new javax.swing.JLabel();
        nameLeaderboard3 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        nameLeaderboard5 = new javax.swing.JLabel();
        scoreLeaderboard = new javax.swing.JLabel();
        scoreLeaderboard2 = new javax.swing.JLabel();
        scoreLeaderboard3 = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        nameLeaderboard4 = new javax.swing.JLabel();
        difficultyLeaderboard3 = new javax.swing.JLabel();
        difficultyLeaderboard1 = new javax.swing.JLabel();
        difficultyLeaderboard2 = new javax.swing.JLabel();
        backArrow1 = new javax.swing.JLabel();
        exitPanel6 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        gameOverDialog1 = new javax.swing.JDialog();
        jPanel27 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        noButton1 = new javax.swing.JButton();
        playAgain2 = new javax.swing.JButton();
        jLabel52 = new javax.swing.JLabel();
        musicSettings = new javax.swing.JDialog();
        jPanel32 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jPanel33 = new javax.swing.JPanel();
        musicLabel = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        howToPlay1 = new javax.swing.JFrame();
        jPanel53 = new javax.swing.JPanel();
        jPanel54 = new javax.swing.JPanel();
        jPanel55 = new javax.swing.JPanel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        howToPlay2 = new javax.swing.JFrame();
        jPanel25 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        howToPlay3 = new javax.swing.JFrame();
        jPanel38 = new javax.swing.JPanel();
        jPanel39 = new javax.swing.JPanel();
        jPanel40 = new javax.swing.JPanel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        howToPlay4 = new javax.swing.JFrame();
        jPanel41 = new javax.swing.JPanel();
        jPanel42 = new javax.swing.JPanel();
        jPanel43 = new javax.swing.JPanel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        howToPlay5 = new javax.swing.JFrame();
        jPanel44 = new javax.swing.JPanel();
        jPanel45 = new javax.swing.JPanel();
        jPanel46 = new javax.swing.JPanel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        howToPlay6 = new javax.swing.JFrame();
        jPanel47 = new javax.swing.JPanel();
        jPanel48 = new javax.swing.JPanel();
        jPanel49 = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        howToPlay7 = new javax.swing.JFrame();
        jPanel50 = new javax.swing.JPanel();
        jPanel51 = new javax.swing.JPanel();
        jPanel52 = new javax.swing.JPanel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
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
        menuIcon = new javax.swing.JLabel();

        difficultyFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        difficultyFrame.setBackground(new java.awt.Color(37, 35, 35));
        difficultyFrame.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        difficultyFrame.setUndecorated(true);
        difficultyFrame.setResizable(false);
        difficultyFrame.setSize(new java.awt.Dimension(800, 600));

        jPanel12.setBackground(new java.awt.Color(37, 35, 35));
        jPanel12.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel13.setBackground(new java.awt.Color(71, 71, 71));

        jLabel29.setBackground(new java.awt.Color(51, 51, 255));
        jLabel29.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Swift Typers Game");

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
                .addGap(67, 67, 67)
                .addComponent(exitPanel5)
                .addGap(57, 57, 57))
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
        jPanel15.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
        jLabel27.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel27)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel46)
                .addGap(57, 57, 57))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel27)
                .addGap(18, 18, 18)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel16.setBackground(new java.awt.Color(37, 35, 35));
        jPanel16.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
        jLabel43.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel43))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel48)))
                .addContainerGap(8, Short.MAX_VALUE))
            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel43)
                .addGap(18, 18, 18)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel17.setBackground(new java.awt.Color(37, 35, 35));
        jPanel17.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
        jLabel45.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel45)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addGap(47, 47, 47))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel50)
                        .addContainerGap())))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel45)
                .addGap(18, 18, 18)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel44.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setText("CHOOSE A GAME DIFFICULTY!");

        jButton3.setBackground(new java.awt.Color(37, 35, 35));
        jButton3.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Leaderboard");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        menuIcon2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Menu_Icon.png"))); // NOI18N
        menuIcon2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menuIcon2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuIcon2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(282, 282, 282))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(menuIcon2)
                        .addGap(129, 129, 129)
                        .addComponent(jLabel44)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44)
                    .addComponent(menuIcon2))
                .addGap(11, 11, 11)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addContainerGap(1410, Short.MAX_VALUE))
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
        easyDifficultyFrame.setUndecorated(true);
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

        homePanel1.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        homePanel1.setForeground(new java.awt.Color(255, 255, 255));
        homePanel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        homePanel1.setText(" X");
        homePanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        homePanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homePanel1MouseClicked(evt);
            }
        });

        jLabel33.setBackground(new java.awt.Color(51, 51, 255));
        jLabel33.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("Swift Typers Game");

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
                .addComponent(homePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(exitPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(exitPanel8)))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(homePanel1)))
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

        menuIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Menu_Icon.png"))); // NOI18N
        menuIcon1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menuIcon1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuIcon1MouseClicked(evt);
            }
        });

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
                        .addGap(259, 259, 259))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(menuIcon1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGap(105, 105, 105))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36)
                            .addComponent(jLabel35)
                            .addComponent(label_score1)
                            .addComponent(label_lives1)
                            .addComponent(easyTimer1)))
                    .addComponent(menuIcon1))
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
        middleDifficultyFrame.setUndecorated(true);
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

        homePanel2.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        homePanel2.setForeground(new java.awt.Color(255, 255, 255));
        homePanel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        homePanel2.setText(" X");
        homePanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        homePanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homePanel2MouseClicked(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(51, 51, 255));
        jLabel18.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Swift Typers Game");

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
                .addComponent(homePanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(homePanel2))
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

        menuIcon3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Menu_Icon.png"))); // NOI18N
        menuIcon3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menuIcon3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuIcon3MouseClicked(evt);
            }
        });

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
                        .addGap(261, 261, 261))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(menuIcon3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGap(105, 105, 105))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20)
                            .addComponent(jLabel3)
                            .addComponent(label_score)
                            .addComponent(label_lives)
                            .addComponent(average_timer)))
                    .addComponent(menuIcon3))
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
        hardDifficultyFrame.setUndecorated(true);
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

        homePanel3.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        homePanel3.setForeground(new java.awt.Color(255, 255, 255));
        homePanel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        homePanel3.setText(" X");
        homePanel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        homePanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homePanel3MouseClicked(evt);
            }
        });

        jLabel38.setBackground(new java.awt.Color(51, 51, 255));
        jLabel38.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Swift Typers Game");

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
                .addComponent(homePanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(homePanel3))
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

        menuIcon4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Menu_Icon.png"))); // NOI18N
        menuIcon4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menuIcon4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuIcon4MouseClicked(evt);
            }
        });

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
                        .addGap(341, 341, 341))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(menuIcon4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGap(103, 103, 103))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41)
                            .addComponent(jLabel40)
                            .addComponent(label_score2)
                            .addComponent(label_lives2)
                            .addComponent(difficultTimer1)))
                    .addComponent(menuIcon4))
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

        leaderBoardDialog.setUndecorated(true);
        leaderBoardDialog.setResizable(false);
        leaderBoardDialog.setSize(new java.awt.Dimension(800, 400));

        jPanel2.setBackground(new java.awt.Color(71, 71, 71));

        jPanel3.setBackground(new java.awt.Color(37, 35, 35));

        jLabel26.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("   ");

        jPanel29.setBackground(new java.awt.Color(37, 35, 35));

        nameLeaderboard1.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        nameLeaderboard1.setForeground(new java.awt.Color(255, 255, 255));
        nameLeaderboard1.setText("Top Player");

        nameLeaderboard.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        nameLeaderboard.setForeground(new java.awt.Color(255, 255, 255));
        nameLeaderboard.setText("Name");

        nameLeaderboard2.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        nameLeaderboard2.setForeground(new java.awt.Color(255, 255, 255));
        nameLeaderboard2.setText("Name");

        nameLeaderboard3.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        nameLeaderboard3.setForeground(new java.awt.Color(255, 255, 255));
        nameLeaderboard3.setText("Name");

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel29Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(nameLeaderboard3, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                    .addComponent(nameLeaderboard, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nameLeaderboard2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nameLeaderboard1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(493, 493, 493))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(nameLeaderboard1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(nameLeaderboard)
                .addGap(27, 27, 27)
                .addComponent(nameLeaderboard2)
                .addGap(31, 31, 31)
                .addComponent(nameLeaderboard3)
                .addGap(24, 24, 24))
        );

        jPanel31.setBackground(new java.awt.Color(37, 35, 35));

        nameLeaderboard5.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        nameLeaderboard5.setForeground(new java.awt.Color(255, 255, 255));
        nameLeaderboard5.setText("Score");

        scoreLeaderboard.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        scoreLeaderboard.setForeground(new java.awt.Color(255, 255, 255));
        scoreLeaderboard.setText("0");

        scoreLeaderboard2.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        scoreLeaderboard2.setForeground(new java.awt.Color(255, 255, 255));
        scoreLeaderboard2.setText("0");
        scoreLeaderboard2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        scoreLeaderboard3.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        scoreLeaderboard3.setForeground(new java.awt.Color(255, 255, 255));
        scoreLeaderboard3.setText("0");

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel31Layout.createSequentialGroup()
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scoreLeaderboard, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scoreLeaderboard2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scoreLeaderboard3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26))
                    .addComponent(nameLeaderboard5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel31Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(nameLeaderboard5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addComponent(scoreLeaderboard, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(scoreLeaderboard2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(scoreLeaderboard3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );

        jPanel30.setBackground(new java.awt.Color(37, 35, 35));

        nameLeaderboard4.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        nameLeaderboard4.setForeground(new java.awt.Color(255, 255, 255));
        nameLeaderboard4.setText("Difficulty");

        difficultyLeaderboard3.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        difficultyLeaderboard3.setForeground(new java.awt.Color(255, 255, 255));
        difficultyLeaderboard3.setText("Easy");

        difficultyLeaderboard1.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        difficultyLeaderboard1.setForeground(new java.awt.Color(255, 255, 255));
        difficultyLeaderboard1.setText("Moderate");

        difficultyLeaderboard2.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        difficultyLeaderboard2.setForeground(new java.awt.Color(255, 255, 255));
        difficultyLeaderboard2.setText("Hard");

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameLeaderboard4, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(difficultyLeaderboard1)
                        .addGroup(jPanel30Layout.createSequentialGroup()
                            .addComponent(difficultyLeaderboard3)
                            .addGap(28, 28, 28))
                        .addGroup(jPanel30Layout.createSequentialGroup()
                            .addComponent(difficultyLeaderboard2)
                            .addGap(27, 27, 27))))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nameLeaderboard4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(difficultyLeaderboard3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(difficultyLeaderboard1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(difficultyLeaderboard2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83)
                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(56, 56, 56)
                            .addComponent(jLabel26))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
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

        jLabel25.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Leaderboard");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(backArrow1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel25)
                .addGap(216, 216, 216)
                .addComponent(exitPanel6)
                .addGap(89, 89, 89))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(exitPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(backArrow1)))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
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

        gameOverDialog1.setUndecorated(true);
        gameOverDialog1.setResizable(false);
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

        musicSettings.setUndecorated(true);
        musicSettings.setSize(new java.awt.Dimension(400, 300));

        jPanel32.setBackground(new java.awt.Color(71, 71, 71));

        jLabel53.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(255, 255, 255));
        jLabel53.setText("Music");

        jPanel33.setBackground(new java.awt.Color(37, 35, 35));

        musicLabel.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        musicLabel.setForeground(new java.awt.Color(255, 255, 255));
        musicLabel.setText("Music Volume:");

        jLabel56.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(255, 255, 255));
        jLabel56.setText("   ");

        jSlider1.setValue(100);
        jSlider1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("100");

        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/volume.png"))); // NOI18N
        jLabel23.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel23MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel33Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(32, 32, 32)
                        .addComponent(musicLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel22)))
                .addGap(101, 101, 101))
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel56)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(musicLabel)
                        .addComponent(jLabel22))
                    .addComponent(jLabel23))
                .addGap(26, 26, 26)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        jLabel54.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel54.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel54MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel54)
                .addGap(60, 60, 60)
                .addComponent(jLabel53)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel53))
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel54)))
                .addGap(18, 18, 18)
                .addComponent(jPanel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout musicSettingsLayout = new javax.swing.GroupLayout(musicSettings.getContentPane());
        musicSettings.getContentPane().setLayout(musicSettingsLayout);
        musicSettingsLayout.setHorizontalGroup(
            musicSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        musicSettingsLayout.setVerticalGroup(
            musicSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        howToPlay1.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        howToPlay1.setBackground(new java.awt.Color(71, 71, 71));
        howToPlay1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        howToPlay1.setUndecorated(true);
        howToPlay1.setResizable(false);
        howToPlay1.setSize(new java.awt.Dimension(800, 600));

        jPanel54.setBackground(new java.awt.Color(37, 35, 35));
        jPanel54.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel55.setBackground(new java.awt.Color(71, 71, 71));

        jLabel103.setBackground(new java.awt.Color(51, 51, 255));
        jLabel103.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel103.setForeground(new java.awt.Color(255, 255, 255));
        jLabel103.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel103.setText("Swift Typers Game");

        jLabel104.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        jLabel104.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel104.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel104MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel55Layout = new javax.swing.GroupLayout(jPanel55);
        jPanel55.setLayout(jPanel55Layout);
        jPanel55Layout.setHorizontalGroup(
            jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel55Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel104)
                .addGap(122, 122, 122)
                .addComponent(jLabel103, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(158, Short.MAX_VALUE))
        );
        jPanel55Layout.setVerticalGroup(
            jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel55Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel55Layout.createSequentialGroup()
                        .addComponent(jLabel104)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel55Layout.createSequentialGroup()
                        .addComponent(jLabel103, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
        );

        jLabel105.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HowToPlay/How To Play 0.png"))); // NOI18N

        jLabel108.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel108.setForeground(new java.awt.Color(255, 255, 255));
        jLabel108.setText("1. Enter the game and select the difficulty of your preference.");

        jLabel109.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Forward Arrow.png"))); // NOI18N
        jLabel109.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel109.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel109MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel54Layout = new javax.swing.GroupLayout(jPanel54);
        jPanel54.setLayout(jPanel54Layout);
        jPanel54Layout.setHorizontalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel54Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel54Layout.createSequentialGroup()
                        .addComponent(jLabel105)
                        .addGap(56, 56, 56)
                        .addComponent(jLabel109)
                        .addGap(90, 90, 90))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel54Layout.createSequentialGroup()
                        .addComponent(jLabel108, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49))))
        );
        jPanel54Layout.setVerticalGroup(
            jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel54Layout.createSequentialGroup()
                .addComponent(jPanel55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel54Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel105)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel54Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel109)
                        .addGap(167, 167, 167)))
                .addComponent(jLabel108)
                .addGap(57, 57, 57))
        );

        javax.swing.GroupLayout jPanel53Layout = new javax.swing.GroupLayout(jPanel53);
        jPanel53.setLayout(jPanel53Layout);
        jPanel53Layout.setHorizontalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel53Layout.setVerticalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout howToPlay1Layout = new javax.swing.GroupLayout(howToPlay1.getContentPane());
        howToPlay1.getContentPane().setLayout(howToPlay1Layout);
        howToPlay1Layout.setHorizontalGroup(
            howToPlay1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        howToPlay1Layout.setVerticalGroup(
            howToPlay1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, howToPlay1Layout.createSequentialGroup()
                .addComponent(jPanel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        howToPlay2.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        howToPlay2.setBackground(new java.awt.Color(71, 71, 71));
        howToPlay2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        howToPlay2.setUndecorated(true);
        howToPlay2.setResizable(false);
        howToPlay2.setSize(new java.awt.Dimension(800, 600));

        jPanel26.setBackground(new java.awt.Color(37, 35, 35));
        jPanel26.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel34.setBackground(new java.awt.Color(71, 71, 71));

        jLabel66.setBackground(new java.awt.Color(51, 51, 255));
        jLabel66.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(255, 255, 255));
        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel66.setText("Swift Typers Game");

        jLabel67.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        jLabel67.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel67.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel67MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel34Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel67)
                .addGap(122, 122, 122)
                .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel34Layout.createSequentialGroup()
                        .addComponent(jLabel67)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel34Layout.createSequentialGroup()
                        .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
        );

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HowToPlay/How To Play 2.png"))); // NOI18N

        jLabel55.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(255, 255, 255));
        jLabel55.setText("2. Once you have chosen the difficult");

        jLabel72.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        jLabel72.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel72.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel72MouseClicked(evt);
            }
        });

        jLabel59.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(255, 255, 255));
        jLabel59.setText("the game will show a word on the screen.");

        jLabel100.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Forward Arrow.png"))); // NOI18N
        jLabel100.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel100.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel100MouseClicked(evt);
            }
        });
        jLabel100.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLabel100KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel72)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel26Layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel26Layout.createSequentialGroup()
                                .addGap(516, 516, 516)
                                .addComponent(jLabel100)))
                        .addGap(91, 91, 91))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(182, 182, 182))))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jPanel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel26Layout.createSequentialGroup()
                                .addGap(214, 214, 214)
                                .addComponent(jLabel72)
                                .addGap(186, 186, 186))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)
                                .addGap(38, 38, 38)
                                .addComponent(jLabel55)
                                .addGap(12, 12, 12))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel100)
                        .addGap(183, 183, 183)))
                .addComponent(jLabel59)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout howToPlay2Layout = new javax.swing.GroupLayout(howToPlay2.getContentPane());
        howToPlay2.getContentPane().setLayout(howToPlay2Layout);
        howToPlay2Layout.setHorizontalGroup(
            howToPlay2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        howToPlay2Layout.setVerticalGroup(
            howToPlay2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, howToPlay2Layout.createSequentialGroup()
                .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        howToPlay3.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        howToPlay3.setBackground(new java.awt.Color(71, 71, 71));
        howToPlay3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        howToPlay3.setUndecorated(true);
        howToPlay3.setResizable(false);
        howToPlay3.setSize(new java.awt.Dimension(800, 600));

        jPanel39.setBackground(new java.awt.Color(37, 35, 35));
        jPanel39.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel40.setBackground(new java.awt.Color(71, 71, 71));

        jLabel73.setBackground(new java.awt.Color(51, 51, 255));
        jLabel73.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(255, 255, 255));
        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel73.setText("Swift Typers Game");

        jLabel74.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        jLabel74.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel74.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel74MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel40Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel74)
                .addGap(122, 122, 122)
                .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel40Layout.createSequentialGroup()
                        .addComponent(jLabel74)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel40Layout.createSequentialGroup()
                        .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
        );

        jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HowToPlay/How To Play 1.png"))); // NOI18N

        jLabel61.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(255, 255, 255));
        jLabel61.setText("3. Players need to type the ");

        jLabel75.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Forward Arrow.png"))); // NOI18N
        jLabel75.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel75.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel75MouseClicked(evt);
            }
        });

        jLabel76.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        jLabel76.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel76.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel76MouseClicked(evt);
            }
        });

        jLabel62.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(255, 255, 255));
        jLabel62.setText("displayed word into a text field.");
        jLabel62.setToolTipText("");

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                        .addComponent(jLabel76)
                        .addGap(63, 63, 63)
                        .addComponent(jLabel60)
                        .addGap(61, 61, 61)
                        .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(74, 74, 74))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                        .addComponent(jLabel61)
                        .addGap(253, 253, 253))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                        .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(211, 211, 211))))
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel39Layout.createSequentialGroup()
                        .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel39Layout.createSequentialGroup()
                                .addGap(202, 202, 202)
                                .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(118, 118, 118))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel60)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addComponent(jLabel61))
                    .addGroup(jPanel39Layout.createSequentialGroup()
                        .addGap(214, 214, 214)
                        .addComponent(jLabel76)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel62)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout howToPlay3Layout = new javax.swing.GroupLayout(howToPlay3.getContentPane());
        howToPlay3.getContentPane().setLayout(howToPlay3Layout);
        howToPlay3Layout.setHorizontalGroup(
            howToPlay3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        howToPlay3Layout.setVerticalGroup(
            howToPlay3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, howToPlay3Layout.createSequentialGroup()
                .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        howToPlay4.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        howToPlay4.setBackground(new java.awt.Color(71, 71, 71));
        howToPlay4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        howToPlay4.setUndecorated(true);
        howToPlay4.setResizable(false);
        howToPlay4.setSize(new java.awt.Dimension(800, 600));

        jPanel42.setBackground(new java.awt.Color(37, 35, 35));
        jPanel42.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel43.setBackground(new java.awt.Color(71, 71, 71));

        jLabel77.setBackground(new java.awt.Color(51, 51, 255));
        jLabel77.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel77.setForeground(new java.awt.Color(255, 255, 255));
        jLabel77.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel77.setText("Swift Typers Game");

        jLabel78.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        jLabel78.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel78.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel78MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel43Layout = new javax.swing.GroupLayout(jPanel43);
        jPanel43.setLayout(jPanel43Layout);
        jPanel43Layout.setHorizontalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel43Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel78)
                .addGap(122, 122, 122)
                .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel43Layout.setVerticalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel43Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel43Layout.createSequentialGroup()
                        .addComponent(jLabel78)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel43Layout.createSequentialGroup()
                        .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
        );

        jLabel63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HowToPlay/How To Play 3.png"))); // NOI18N

        jLabel64.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(255, 255, 255));
        jLabel64.setText("4. If the typed word matches the ");

        jLabel79.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Forward Arrow.png"))); // NOI18N
        jLabel79.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel79.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel79MouseClicked(evt);
            }
        });

        jLabel80.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        jLabel80.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel80.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel80MouseClicked(evt);
            }
        });

        jLabel65.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(255, 255, 255));
        jLabel65.setText("displayed word, a new word will appear.");
        jLabel65.setToolTipText("");

        javax.swing.GroupLayout jPanel42Layout = new javax.swing.GroupLayout(jPanel42);
        jPanel42.setLayout(jPanel42Layout);
        jPanel42Layout.setHorizontalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel42Layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel42Layout.createSequentialGroup()
                        .addComponent(jLabel80)
                        .addGap(80, 80, 80)
                        .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel42Layout.createSequentialGroup()
                                .addComponent(jLabel63)
                                .addGap(51, 51, 51)
                                .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(94, 94, 94))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel42Layout.createSequentialGroup()
                        .addComponent(jLabel64)
                        .addGap(225, 225, 225))))
        );
        jPanel42Layout.setVerticalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel42Layout.createSequentialGroup()
                .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel42Layout.createSequentialGroup()
                        .addComponent(jPanel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel42Layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(jLabel63))
                            .addGroup(jPanel42Layout.createSequentialGroup()
                                .addGap(214, 214, 214)
                                .addComponent(jLabel80)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel42Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(113, 113, 113)))
                .addComponent(jLabel64)
                .addGap(12, 12, 12)
                .addComponent(jLabel65)
                .addContainerGap(69, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel41Layout = new javax.swing.GroupLayout(jPanel41);
        jPanel41.setLayout(jPanel41Layout);
        jPanel41Layout.setHorizontalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel41Layout.setVerticalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout howToPlay4Layout = new javax.swing.GroupLayout(howToPlay4.getContentPane());
        howToPlay4.getContentPane().setLayout(howToPlay4Layout);
        howToPlay4Layout.setHorizontalGroup(
            howToPlay4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        howToPlay4Layout.setVerticalGroup(
            howToPlay4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, howToPlay4Layout.createSequentialGroup()
                .addComponent(jPanel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        howToPlay5.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        howToPlay5.setBackground(new java.awt.Color(71, 71, 71));
        howToPlay5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        howToPlay5.setUndecorated(true);
        howToPlay5.setResizable(false);
        howToPlay5.setSize(new java.awt.Dimension(800, 600));

        jPanel45.setBackground(new java.awt.Color(37, 35, 35));
        jPanel45.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel46.setBackground(new java.awt.Color(71, 71, 71));

        jLabel81.setBackground(new java.awt.Color(51, 51, 255));
        jLabel81.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(255, 255, 255));
        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel81.setText("Swift Typers Game");

        jLabel82.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        jLabel82.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel82.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel82MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel46Layout = new javax.swing.GroupLayout(jPanel46);
        jPanel46.setLayout(jPanel46Layout);
        jPanel46Layout.setHorizontalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel46Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel82)
                .addGap(122, 122, 122)
                .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel46Layout.setVerticalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel46Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel46Layout.createSequentialGroup()
                        .addComponent(jLabel82)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel46Layout.createSequentialGroup()
                        .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
        );

        jLabel83.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HowToPlay/How To Play 4.png"))); // NOI18N

        jLabel84.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel84.setForeground(new java.awt.Color(255, 255, 255));
        jLabel84.setText("5. If the typed word is incorrect,");

        jLabel85.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Forward Arrow.png"))); // NOI18N
        jLabel85.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel85.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel85MouseClicked(evt);
            }
        });

        jLabel86.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        jLabel86.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel86.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel86MouseClicked(evt);
            }
        });

        jLabel87.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel87.setForeground(new java.awt.Color(255, 255, 255));
        jLabel87.setText("it will cost one player life.");
        jLabel87.setToolTipText("");

        javax.swing.GroupLayout jPanel45Layout = new javax.swing.GroupLayout(jPanel45);
        jPanel45.setLayout(jPanel45Layout);
        jPanel45Layout.setHorizontalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel45Layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addGroup(jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel45Layout.createSequentialGroup()
                        .addComponent(jLabel86)
                        .addGap(80, 80, 80)
                        .addComponent(jLabel83)
                        .addGap(51, 51, 51)
                        .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(72, 72, 72))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel45Layout.createSequentialGroup()
                        .addComponent(jLabel84)
                        .addGap(231, 231, 231))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel45Layout.createSequentialGroup()
                        .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(250, 250, 250))))
        );
        jPanel45Layout.setVerticalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel45Layout.createSequentialGroup()
                .addGroup(jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel45Layout.createSequentialGroup()
                        .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel45Layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(jLabel83))
                            .addGroup(jPanel45Layout.createSequentialGroup()
                                .addGap(214, 214, 214)
                                .addComponent(jLabel86)))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel45Layout.createSequentialGroup()
                        .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(115, 115, 115)))
                .addComponent(jLabel84)
                .addGap(12, 12, 12)
                .addComponent(jLabel87)
                .addContainerGap(71, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel44Layout = new javax.swing.GroupLayout(jPanel44);
        jPanel44.setLayout(jPanel44Layout);
        jPanel44Layout.setHorizontalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel44Layout.setVerticalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout howToPlay5Layout = new javax.swing.GroupLayout(howToPlay5.getContentPane());
        howToPlay5.getContentPane().setLayout(howToPlay5Layout);
        howToPlay5Layout.setHorizontalGroup(
            howToPlay5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        howToPlay5Layout.setVerticalGroup(
            howToPlay5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, howToPlay5Layout.createSequentialGroup()
                .addComponent(jPanel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        howToPlay6.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        howToPlay6.setBackground(new java.awt.Color(71, 71, 71));
        howToPlay6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        howToPlay6.setUndecorated(true);
        howToPlay6.setResizable(false);
        howToPlay6.setSize(new java.awt.Dimension(800, 600));

        jPanel48.setBackground(new java.awt.Color(37, 35, 35));
        jPanel48.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel49.setBackground(new java.awt.Color(71, 71, 71));

        jLabel88.setBackground(new java.awt.Color(51, 51, 255));
        jLabel88.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(255, 255, 255));
        jLabel88.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel88.setText("Swift Typers Game");

        jLabel89.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        jLabel89.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel89.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel89MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel49Layout = new javax.swing.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel49Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel89)
                .addGap(122, 122, 122)
                .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel49Layout.setVerticalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel49Layout.createSequentialGroup()
                        .addComponent(jLabel89)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel49Layout.createSequentialGroup()
                        .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
        );

        jLabel90.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HowToPlay/How To Play 5.png"))); // NOI18N

        jLabel91.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel91.setForeground(new java.awt.Color(255, 255, 255));
        jLabel91.setText("6. If all the player lives are lost,");

        jLabel92.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Forward Arrow.png"))); // NOI18N
        jLabel92.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel92.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel92MouseClicked(evt);
            }
        });

        jLabel93.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        jLabel93.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel93.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel93MouseClicked(evt);
            }
        });

        jLabel94.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel94.setForeground(new java.awt.Color(255, 255, 255));
        jLabel94.setText("the game will ask if the player wants to play again.");
        jLabel94.setToolTipText("");

        javax.swing.GroupLayout jPanel48Layout = new javax.swing.GroupLayout(jPanel48);
        jPanel48.setLayout(jPanel48Layout);
        jPanel48Layout.setHorizontalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel48Layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel48Layout.createSequentialGroup()
                        .addComponent(jLabel93)
                        .addGap(80, 80, 80)
                        .addComponent(jLabel90)
                        .addGap(51, 51, 51)
                        .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel48Layout.createSequentialGroup()
                        .addComponent(jLabel91)
                        .addGap(231, 231, 231))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel48Layout.createSequentialGroup()
                        .addComponent(jLabel94)
                        .addGap(137, 137, 137))))
        );
        jPanel48Layout.setVerticalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel48Layout.createSequentialGroup()
                .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel48Layout.createSequentialGroup()
                        .addComponent(jPanel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel48Layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(jLabel90))
                            .addGroup(jPanel48Layout.createSequentialGroup()
                                .addGap(214, 214, 214)
                                .addComponent(jLabel93)))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel48Layout.createSequentialGroup()
                        .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)))
                .addComponent(jLabel91)
                .addGap(12, 12, 12)
                .addComponent(jLabel94)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel47Layout = new javax.swing.GroupLayout(jPanel47);
        jPanel47.setLayout(jPanel47Layout);
        jPanel47Layout.setHorizontalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel47Layout.setVerticalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout howToPlay6Layout = new javax.swing.GroupLayout(howToPlay6.getContentPane());
        howToPlay6.getContentPane().setLayout(howToPlay6Layout);
        howToPlay6Layout.setHorizontalGroup(
            howToPlay6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        howToPlay6Layout.setVerticalGroup(
            howToPlay6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        howToPlay7.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        howToPlay7.setBackground(new java.awt.Color(71, 71, 71));
        howToPlay7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        howToPlay7.setUndecorated(true);
        howToPlay7.setResizable(false);
        howToPlay7.setSize(new java.awt.Dimension(800, 600));

        jPanel51.setBackground(new java.awt.Color(37, 35, 35));
        jPanel51.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel52.setBackground(new java.awt.Color(71, 71, 71));

        jLabel95.setBackground(new java.awt.Color(51, 51, 255));
        jLabel95.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel95.setForeground(new java.awt.Color(255, 255, 255));
        jLabel95.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel95.setText("Swift Typers Game");

        jLabel96.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HomeButton--.png"))); // NOI18N
        jLabel96.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel96.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel96MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel52Layout = new javax.swing.GroupLayout(jPanel52);
        jPanel52.setLayout(jPanel52Layout);
        jPanel52Layout.setHorizontalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel52Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel96)
                .addGap(122, 122, 122)
                .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel52Layout.setVerticalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel52Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel52Layout.createSequentialGroup()
                        .addComponent(jLabel96)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel52Layout.createSequentialGroup()
                        .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))))
        );

        jLabel97.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        jLabel97.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel97.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel97MouseClicked(evt);
            }
        });

        jLabel98.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel98.setForeground(new java.awt.Color(255, 255, 255));
        jLabel98.setText("7. The player’s score is based on the number of correct words typed.");

        jLabel101.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jLabel101.setForeground(new java.awt.Color(255, 255, 255));
        jLabel101.setText("The player can check the highscore of others who played");
        jLabel101.setToolTipText("");

        jLabel102.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/HowToPlay/How To Play 6.png"))); // NOI18N

        javax.swing.GroupLayout jPanel51Layout = new javax.swing.GroupLayout(jPanel51);
        jPanel51.setLayout(jPanel51Layout);
        jPanel51Layout.setHorizontalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                .addContainerGap(80, Short.MAX_VALUE)
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                        .addComponent(jLabel101)
                        .addGap(129, 129, 129))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                        .addComponent(jLabel97)
                        .addGap(67, 67, 67)
                        .addComponent(jLabel102)
                        .addGap(193, 193, 193))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                        .addComponent(jLabel98)
                        .addGap(72, 72, 72))))
        );
        jPanel51Layout.setVerticalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel51Layout.createSequentialGroup()
                .addComponent(jPanel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel102)
                        .addGap(37, 37, 37))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel97, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103)))
                .addComponent(jLabel98)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel101)
                .addGap(61, 61, 61))
        );

        javax.swing.GroupLayout jPanel50Layout = new javax.swing.GroupLayout(jPanel50);
        jPanel50.setLayout(jPanel50Layout);
        jPanel50Layout.setHorizontalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel50Layout.setVerticalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel106.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/BackArrow.png"))); // NOI18N
        jLabel106.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel107.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Forward Arrow.png"))); // NOI18N
        jLabel107.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout howToPlay7Layout = new javax.swing.GroupLayout(howToPlay7.getContentPane());
        howToPlay7.getContentPane().setLayout(howToPlay7Layout);
        howToPlay7Layout.setHorizontalGroup(
            howToPlay7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(howToPlay7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(howToPlay7Layout.createSequentialGroup()
                    .addGap(91, 91, 91)
                    .addComponent(jLabel106)
                    .addGap(531, 531, 531)
                    .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(92, Short.MAX_VALUE)))
        );
        howToPlay7Layout.setVerticalGroup(
            howToPlay7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, howToPlay7Layout.createSequentialGroup()
                .addComponent(jPanel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(howToPlay7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(howToPlay7Layout.createSequentialGroup()
                    .addGap(252, 252, 252)
                    .addGroup(howToPlay7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(howToPlay7Layout.createSequentialGroup()
                            .addGap(22, 22, 22)
                            .addComponent(jLabel106))
                        .addComponent(jLabel107, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(254, Short.MAX_VALUE)))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(37, 35, 35));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(800, 600));
        setResizable(false);
        setSize(new java.awt.Dimension(800, 600));

        jPanel5.setBackground(new java.awt.Color(37, 35, 35));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/SwiftTypers.png"))); // NOI18N

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
        jLabel6.setText("Swift Typers is a simple yet addictive game that helps ");

        jLabel7.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("you improve your typing skills while having fun!");

        jPanel10.setBackground(new java.awt.Color(71, 71, 71));

        jLabel1.setBackground(new java.awt.Color(51, 51, 255));
        jLabel1.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Swift Typers Game");

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
                .addGap(80, 80, 80)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73)
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

        menuIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeroo/resources/Menu_Icon.png"))); // NOI18N
        menuIcon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menuIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuIconMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(menuIcon)
                .addGap(114, 114, 114)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(213, 213, 213))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(276, 276, 276))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menuIcon))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(269, Short.MAX_VALUE))
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

    private void exitGame() {
        stopAllMusicClips();
        JOptionPane.showMessageDialog(null, "Thanks for playing!!!");
        System.exit(0);
    }
    
    private void exitPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel1MouseClicked
        // To Close
        exitGame();
    }//GEN-LAST:event_exitPanel1MouseClicked

    private void hintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hintButtonActionPerformed
        getHint();
    }//GEN-LAST:event_hintButtonActionPerformed

    private void exitPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel2MouseClicked
        timer.stop();
        exitGame();
    }//GEN-LAST:event_exitPanel2MouseClicked

    private void homePanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homePanel2MouseClicked
        JOptionPane.showMessageDialog(null, "Thanks for Playing!");
        if (clip != null) {
            clip.stop();
        }
        if (clip2 != null) {
            clip2.stop();
        }
        if (clip3 != null) {
            clip3.stop();
        }
        if (clip4 != null) {
            clip4.stop();
        }
        
        this.setVisible(true);
        middleDifficultyFrame.setVisible(false);
        timer.stop();
        score = 0;
        try {
            playOriginalMusic();
            applyVolumeToAllModes();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_homePanel2MouseClicked

    private void howtoPlayPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_howtoPlayPanelMouseClicked
        this.dispose();
        howToPlay1.setVisible(true);
    }//GEN-LAST:event_howtoPlayPanelMouseClicked

    private void stopAllMusicClips() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
        if (clip2 != null && clip2.isRunning()) {
            clip2.stop();
        }
        if (clip3 != null && clip3.isRunning()) {
            clip3.stop();
        }
        if (clip4 != null && clip4.isRunning()) {
            clip4.stop();
        }
        if (clip5 != null && clip5.isRunning()) {
            clip5.stop();
        }
    }
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        userName = JOptionPane.showInputDialog("Please enter you're name: ");
        if (userName != null && !userName.trim().isEmpty()){
            stopAllMusicClips();
            difficultyFrame.dispose();
            easyDifficultyFrame.setVisible(true);
            score = 0;
            index = 0;
            lives = 5;
            hints = 5;
            countdown = easyTime;
            timer.start();
            displayHUD();
            displayWords();
            playEasyModeMusic();
            applyVolumeToAllModes();
        }else{
            resetGameState();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void exitPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel5MouseClicked
        exitGame();
    }//GEN-LAST:event_exitPanel5MouseClicked

    private void backArrowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backArrowMouseClicked
        this.setVisible(true);
        difficultyFrame.setVisible(false);
    }//GEN-LAST:event_backArrowMouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        userName2 = JOptionPane.showInputDialog("Please enter you're name: ");
        if (userName2 != null && !userName2.trim().isEmpty()){
            stopAllMusicClips();
            difficultyFrame.dispose();
            middleDifficultyFrame.setVisible(true);
            score = 0;
            index = 0;
            lives = 3;
            hints = 4;
            countdown = averageTime;
            timer.start();
            displayHUD();
            displayWords();
            playModerateModeMusic();
            applyVolumeToAllModes();
        }else{
            resetGameState();
            try {
                if (clip != null && clip.isRunning()){
                    clip.start();
                }else{
                    playOriginalMusic();
                }
                applyVolumeToAllModes();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        userName3 = JOptionPane.showInputDialog("Please enter you're name: ");
        if (userName3 != null && !userName3.trim().isEmpty()){
            stopAllMusicClips();
            difficultyFrame.dispose();
            hardDifficultyFrame.setVisible(true);
            score = 0;
            index = 0;
            lives = 3;
            hints = 4;
            countdown = difficultTime;
            timer.start();
            displayHUD();
            displayWords();
            playHardModeMusic();
            applyVolumeToAllModes();
        }else{
            resetGameState();
            try {
                if (clip != null && clip.isRunning()){
                    clip.start();
                }else{
                    playOriginalMusic();
                }
                applyVolumeToAllModes();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void goHome(JLabel label, JFrame frameToHide){
        frameToHide.setVisible(false);
        this.setVisible(true);
    }
    
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

    private void getHint(){
        timer.stop();
        if (hints > 0) {
            hints--;
            String currentWord = getCurrentWord(); // Get the current word being displayed
            String description = getWordDescription(currentWord);
            JOptionPane.showMessageDialog(null, "Hint: " + description + "\nYour hints are only: " + hints);
        }
        if (hints == 0) {
            JOptionPane.showMessageDialog(null, "All of your hints are used!!!");
        }
        timer.start();
    }
    
    private void hintButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hintButton1ActionPerformed
       getHint();
    }//GEN-LAST:event_hintButton1ActionPerformed

    private void homePanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homePanel1MouseClicked
        JOptionPane.showMessageDialog(null, "Thanks for Playing!");
        if (clip != null) {
            clip.stop();
        }
        if (clip2 != null) {
            clip2.stop();
        }
        if (clip3 != null) {
            clip3.stop();
        }
        if (clip4 != null) {
            clip4.stop();
        }
        this.setVisible(true);
        easyDifficultyFrame.setVisible(false);
        timer.stop();
        score = 0;
        try {
            playOriginalMusic();
            applyVolumeToAllModes();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_homePanel1MouseClicked

    private void exitPanel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel8MouseClicked
        timer.stop();
        exitGame();
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

    private void homePanel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homePanel3MouseClicked
        JOptionPane.showMessageDialog(null, "Thanks for Playing!");
        if (clip != null) {
            clip.stop();
        }
        if (clip2 != null) {
            clip2.stop();
        }
        if (clip3 != null) {
            clip3.stop();
        }
        if (clip4 != null) {
            clip4.stop();
        }
        
        this.setVisible(true);
        hardDifficultyFrame.dispose();
        timer.stop();
        score = 0;
        try {
            playOriginalMusic();
            applyVolumeToAllModes();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_homePanel3MouseClicked

    private void exitPanel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel10MouseClicked
        timer.stop();
        exitGame();
    }//GEN-LAST:event_exitPanel10MouseClicked

    private void jTextField2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField2MouseClicked
        jTextField2.setText("");
    }//GEN-LAST:event_jTextField2MouseClicked

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed

    }//GEN-LAST:event_jTextField2KeyPressed

    private void jPanel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel16MouseClicked

    }//GEN-LAST:event_jPanel16MouseClicked

    private void jLabel43MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel43MouseClicked

    }//GEN-LAST:event_jLabel43MouseClicked

    private void jLabel27MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel27MouseClicked

    }//GEN-LAST:event_jLabel27MouseClicked

    private void jPanel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel15MouseClicked

    }//GEN-LAST:event_jPanel15MouseClicked

    private void jLabel45MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel45MouseClicked

    }//GEN-LAST:event_jLabel45MouseClicked

    private void jPanel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel17MouseClicked

    }//GEN-LAST:event_jPanel17MouseClicked

    private void noButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noButton1ActionPerformed
        JOptionPane.showMessageDialog(null, "Thanks for Playing!");
        System.exit(0);
    }//GEN-LAST:event_noButton1ActionPerformed

    private void playAgain2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playAgain2ActionPerformed
        gameOverDialog1.dispose();
        difficultyFrame.setVisible(true);
        resetGameState();
        try {
            playOriginalMusic();
            applyVolumeToAllModes();
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
            Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_playAgain2ActionPerformed

    private void resetGameState() {
        // Reset timer
        if (timer != null) {
            timer.stop();
        }

        // Reset countdown based on selected difficulty
        if (isEasyMode) {
            countdown = easyTime;
        } else if (isHardMode) {
            countdown = difficultTime;
        } else {
            countdown = averageTime;
        }

        // Reset score, lives, and other game-related variables
        score = 0;
        lives = easyLives;
        hints = 0;

        // Reset text fields
        jTextField_Guess.setText("");
        jTextField_Guess2.setText("");
        jTextField_Guess1.setText("");

        // Reset HUD display
        displayHUD();

        // Stop all music clips
        if (!difficultyFrame.isVisible()) {
            try {
                playOriginalMusic();
                applyVolumeToAllModes();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                Logger.getLogger(Typeroo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        difficultyFrame.setVisible(false);
        leaderBoardDialog.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void backArrow1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backArrow1MouseClicked
        leaderBoardDialog.setVisible(false);
        difficultyFrame.setVisible(true);
    }//GEN-LAST:event_backArrow1MouseClicked

    private void exitPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitPanel6MouseClicked
        exitGame();
    }//GEN-LAST:event_exitPanel6MouseClicked

    public int sliderValue; //For the volume value of the slider
    private float previousVolume;
    
    private void updateSliderValue(int value) {
        sliderValue = value;
        jLabel22.setText("" + sliderValue); // Update label with current slider value
    }
    
    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        JSlider source = (JSlider)evt.getSource();
        updateSliderValue(source.getValue());
        currentVolume = sliderValue / 100.0f; // Update currentVolume
        applyVolumeToAllModes();

        if (sliderValue == 100) {
            setVolume(clip, 1.0f); // Set to default volume
        } else {
            float limitedVolume = Math.min(sliderValue, 30) / 100.0f;
            setVolume(clip, limitedVolume);
        }
        updateSliderValue(jSlider1.getValue());
    }//GEN-LAST:event_jSlider1StateChanged

    private void applyVolumeToAllModes(){
        setVolume(clip, currentVolume);
        setVolume(clip2, currentVolume);
        setVolume(clip3, currentVolume);
        setVolume(clip4, currentVolume);
        setVolume(clip5, currentVolume);
        setVolume(gameOver, 0.04f);
    }
    
    private void jLabel54MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel54MouseClicked
        musicSettings.setVisible(false);
    }//GEN-LAST:event_jLabel54MouseClicked

    private void menuIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuIconMouseClicked
        musicSettings.setVisible(true);
    }//GEN-LAST:event_menuIconMouseClicked

    private void menuIcon2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuIcon2MouseClicked
        musicSettings.setVisible(true);
    }//GEN-LAST:event_menuIcon2MouseClicked

    private void menuIcon1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuIcon1MouseClicked

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                musicSettings.setVisible(true);
            }
        });
    }//GEN-LAST:event_menuIcon1MouseClicked

    private void menuIcon3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuIcon3MouseClicked

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                musicSettings.setVisible(true);
            }
        });
    }//GEN-LAST:event_menuIcon3MouseClicked

    private void menuIcon4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuIcon4MouseClicked

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                musicSettings.setVisible(true);
            }
        });
    }//GEN-LAST:event_menuIcon4MouseClicked

    private void jLabel23MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel23MouseClicked
        sliderValue = jSlider1.getValue();
        if (isMuted) {
            // Restore the previous volume
            setVolume(clip, previousVolume);
            isMuted = false;
            // Update the slider value with the previous volume
            updateSliderValue((int) (previousVolume * 100));
        } else {
            // Store the current volume before muting
            previousVolume = currentVolume;
            setVolume(clip, 0.0f);
            isMuted = true;
            // Update jLabel22 text to 0
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jLabel22.setText("0");
                }
            });
        }
    }//GEN-LAST:event_jLabel23MouseClicked

    private void jLabel67MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel67MouseClicked
        goHome(jLabel67, howToPlay2);
    }//GEN-LAST:event_jLabel67MouseClicked

    private void jLabel74MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel74MouseClicked
        goHome(jLabel74, howToPlay3);
    }//GEN-LAST:event_jLabel74MouseClicked

    private void jLabel78MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel78MouseClicked
        goHome(jLabel78, howToPlay4);
    }//GEN-LAST:event_jLabel78MouseClicked

    private void jLabel82MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel82MouseClicked
        goHome(jLabel89, howToPlay5);
    }//GEN-LAST:event_jLabel82MouseClicked

    private void jLabel89MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel89MouseClicked
        goHome(jLabel67, howToPlay6);
    }//GEN-LAST:event_jLabel89MouseClicked

    private void jLabel96MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel96MouseClicked
        goHome(jLabel96, howToPlay7);
    }//GEN-LAST:event_jLabel96MouseClicked

    private void jLabel104MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel104MouseClicked
        goHome(jLabel104, howToPlay1);
    }//GEN-LAST:event_jLabel104MouseClicked

    private void jLabel97MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel97MouseClicked
        howToPlay7.dispose();
        howToPlay6.setVisible(true);
    }//GEN-LAST:event_jLabel97MouseClicked

    private void jLabel92MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel92MouseClicked
        howToPlay6.dispose();
        howToPlay7.setVisible(true);
    }//GEN-LAST:event_jLabel92MouseClicked

    private void jLabel93MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel93MouseClicked
        howToPlay6.dispose();
        howToPlay5.setVisible(true);
    }//GEN-LAST:event_jLabel93MouseClicked

    private void jLabel85MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel85MouseClicked
        howToPlay5.dispose();
        howToPlay6.setVisible(true);
    }//GEN-LAST:event_jLabel85MouseClicked

    private void jLabel86MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel86MouseClicked
        howToPlay5.dispose();
        howToPlay4.setVisible(true);
    }//GEN-LAST:event_jLabel86MouseClicked

    private void jLabel79MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel79MouseClicked
        howToPlay4.dispose();
        howToPlay5.setVisible(true);
    }//GEN-LAST:event_jLabel79MouseClicked

    private void jLabel80MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel80MouseClicked
        howToPlay4.dispose();
        howToPlay3.setVisible(true);
    }//GEN-LAST:event_jLabel80MouseClicked

    private void jLabel75MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel75MouseClicked
        howToPlay3.dispose();
        howToPlay4.setVisible(true);
    }//GEN-LAST:event_jLabel75MouseClicked

    private void jLabel76MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel76MouseClicked
        howToPlay3.dispose();
        howToPlay2.setVisible(true);
    }//GEN-LAST:event_jLabel76MouseClicked

    private void jLabel100KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLabel100KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel100KeyPressed

    private void jLabel100MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel100MouseClicked
        howToPlay2.dispose();
        howToPlay3.setVisible(true);
    }//GEN-LAST:event_jLabel100MouseClicked

    private void jLabel72MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel72MouseClicked
        howToPlay2.dispose();
        howToPlay1.setVisible(true);
    }//GEN-LAST:event_jLabel72MouseClicked

    private void jLabel109MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel109MouseClicked
        howToPlay1.dispose();
        howToPlay2.setVisible(true);
    }//GEN-LAST:event_jLabel109MouseClicked
    
    public void setVolume(Clip clip, float volume){ // Corrected parameter list
        if (clip != null) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    
    private String getWordDescription(String word) {
        // Extracting description from the word list
        for (String wordEntry : words) {
            String[] parts = wordEntry.split(" - ");
            if (parts.length == 2 && parts[0].equalsIgnoreCase(word)) {
                return parts[1];
            }
        }
        return "Description not found";
    }
    
    public static String[] readWordsFromJSONFile(String fileName, String difficultyLevel) {
        List<String> wordList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            String jsonString = jsonContent.toString();
            jsonString = jsonString.trim();
            if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
                jsonString = jsonString.substring(1, jsonString.length() - 1);
            }

            String[] keyValuePairs = jsonString.split(",");
            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) { // Check if the array has two elements
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    wordList.add(key.toLowerCase() + " - " + value);
                } else {
                    // Handle the case where the pair does not contain a colon separator
                    System.err.println("Invalid key-value pair: " + pair);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            e.printStackTrace();
        }

        return wordList.toArray(new String[0]);
    }
    
    public static void gameOverMusic(){
        try {
                InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("typeroo/resources/Music/game_over.wav");
                AudioInputStream newAudioStream = AudioSystem.getAudioInputStream(inputStream);
                gameOver = AudioSystem.getClip(); // Assuming 'clip' is a class-level variable
                gameOver.open(newAudioStream);
                gameOver.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                // Handle any exceptions that might occur during loading/playing the new music
            }
    }
    
    public static void playOriginalMusic() throws LineUnavailableException, IOException, UnsupportedAudioFileException{
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("typeroo/resources/Music/Main_Menu_Sound.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        isOriginalMusicPlaying = true;
    }
    
    public static void playEasyModeMusic(){
        try {
                InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("typeroo/resources/Music/Easy_Mode_Sound.wav");
                AudioInputStream newAudioStream = AudioSystem.getAudioInputStream(inputStream);
                clip2 = AudioSystem.getClip(); // Assuming 'clip' is a class-level variable
                clip2.open(newAudioStream);
                clip2.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception ex) {
                ex.printStackTrace();
                // Handle any exceptions that might occur during loading/playing the new music
            }
    }
    
    public static void playModerateModeMusic(){
        try {
                InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("typeroo/resources/Music/Moderate_Mode_Sound.wav");
                AudioInputStream newAudioStream = AudioSystem.getAudioInputStream(inputStream);
                clip3 = AudioSystem.getClip(); // Assuming 'clip' is a class-level variable
                clip3.open(newAudioStream);
                clip3.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception ex) {
                ex.printStackTrace();
                // Handle any exceptions that might occur during loading/playing the new music
            }
    }
    
    public static void playHardModeMusic(){
        try {
                InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("typeroo/resources/Music/Hard_Mode_Sound.wav");
                AudioInputStream newAudioStream = AudioSystem.getAudioInputStream(inputStream);
                clip4 = AudioSystem.getClip(); // Assuming 'clip' is a class-level variable
                clip4.open(newAudioStream);
                clip4.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception ex) {
                ex.printStackTrace();
                // Handle any exceptions that might occur during loading/playing the new music
            }
    }
    
    public static void main(String args[]) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
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
        playOriginalMusic();
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
    private javax.swing.JLabel exitPanel5;
    private javax.swing.JLabel exitPanel6;
    private javax.swing.JLabel exitPanel8;
    private javax.swing.JDialog gameOverDialog1;
    private javax.swing.JFrame hardDifficultyFrame;
    private javax.swing.JButton hintButton;
    private javax.swing.JButton hintButton1;
    private javax.swing.JLabel homePanel1;
    private javax.swing.JLabel homePanel2;
    private javax.swing.JLabel homePanel3;
    private javax.swing.JFrame howToPlay1;
    private javax.swing.JFrame howToPlay2;
    private javax.swing.JFrame howToPlay3;
    private javax.swing.JFrame howToPlay4;
    private javax.swing.JFrame howToPlay5;
    private javax.swing.JFrame howToPlay6;
    private javax.swing.JFrame howToPlay7;
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
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
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
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
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
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel51;
    private javax.swing.JPanel jPanel52;
    private javax.swing.JPanel jPanel53;
    private javax.swing.JPanel jPanel54;
    private javax.swing.JPanel jPanel55;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSlider jSlider1;
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
    private javax.swing.JLabel menuIcon;
    private javax.swing.JLabel menuIcon1;
    private javax.swing.JLabel menuIcon2;
    private javax.swing.JLabel menuIcon3;
    private javax.swing.JLabel menuIcon4;
    private javax.swing.JFrame middleDifficultyFrame;
    private javax.swing.JLabel musicLabel;
    private javax.swing.JDialog musicSettings;
    private javax.swing.JLabel nameLeaderboard;
    private javax.swing.JLabel nameLeaderboard1;
    private javax.swing.JLabel nameLeaderboard2;
    private javax.swing.JLabel nameLeaderboard3;
    private javax.swing.JLabel nameLeaderboard4;
    private javax.swing.JLabel nameLeaderboard5;
    private javax.swing.JButton noButton1;
    private javax.swing.JButton playAgain2;
    private javax.swing.JLabel scoreLeaderboard;
    private javax.swing.JLabel scoreLeaderboard2;
    private javax.swing.JLabel scoreLeaderboard3;
    // End of variables declaration//GEN-END:variables
}
