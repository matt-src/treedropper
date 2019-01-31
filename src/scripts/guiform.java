package scripts;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class guiform extends JFrame {
    private JButton startButton;
    private JPanel fightform;
    private boolean start = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("guiform");
        frame.setContentPane(new guiform().fightform);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public guiform(){
        super("WillowDropper");
        setContentPane(fightform);
        pack();
        setVisible(true);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start = true;
            }
        });
    }

    public boolean getStarted(){
        return start;
    }


}
