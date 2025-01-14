package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.plugins.tiff.ExifTIFFTagSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.tools.Tool;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class ConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton dowm = new JButton("down");
    private final JButton stop = new JButton("stop");

    public ConcurrentGUI(){
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth()*WIDTH_PERC), (int)(screenSize.getHeight()*HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(dowm);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();
        up.addActionListener(e->agent.CountForwards());
        dowm.addActionListener(e->agent.CountBackwards());
        stop.addActionListener(
            e->{agent.stopCounting();
            stop.setEnabled(false);
            dowm.setEnabled(false);
            up.setEnabled(false);
        });
    }
    private class Agent implements Runnable{
        private volatile boolean stop;
        private volatile boolean direction;
        private int counter;
        @Override
        public void run() {
            while(!this.stop){
            try{
                final var nextText = Integer.toString(this.counter);
                SwingUtilities.invokeAndWait(()->ConcurrentGUI.this.display.setText(nextText));
                if(this.direction){
                    this.counter++;
                }else{
                    this.counter--;
                }
                
                Thread.sleep(100);
            }catch (InvocationTargetException | InterruptedException ex){
                ex.printStackTrace();
            }
            }
        }
        public void CountForwards() {
            this.direction = true;
        }
        public void CountBackwards() {
            this.direction =false;
        }
        public void stopCounting(){
            this.stop = true;
            
        }
    }
}
