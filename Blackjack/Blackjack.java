package blackjack;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Blackjack extends JFrame {

    private JPanel mPanel;
    private JButton dealButton;
    private JButton hitButton;
    private JButton standButton;
    private JButton doubleDownButton;
    private JButton surrenderButton;
    private JRadioButton $100RadioButton;
    private JRadioButton $300RadioButton;
    private JRadioButton $200RadioButton;
    private JPanel playerCards;
    private JPanel dealerCards;
    private JTextArea status;
    private JLabel money;

    public Play game;

    public static void main(String[] args) {
        Blackjack frame = new Blackjack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600,600);
        frame.setVisible(true);
        frame.game.play();
    }

    public Blackjack(){

        $100RadioButton.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add($100RadioButton);
        group.add($200RadioButton);
        group.add($300RadioButton);

        game = new Play(dealerCards, playerCards, status, group, money);

        ActionListener dealListener = new DealListener(game);
        dealButton.addActionListener(dealListener);

        ActionListener hitListener = new HitListener(game);
        hitButton.addActionListener(hitListener);

        ActionListener standListener = new StandListener(game);
        standButton.addActionListener(standListener);

        ActionListener doubleDownListener = new DoubleDownListener(game);
        doubleDownButton.addActionListener(doubleDownListener);

        ActionListener surrenderListener = new SurrenderListener(game);
        surrenderButton.addActionListener(surrenderListener);

        setContentPane(mPanel);
    }
}

