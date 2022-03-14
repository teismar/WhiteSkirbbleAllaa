import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;


public class STTestMain extends JFrame {
    boolean empfangen=false;
    Socket s = new Socket("localhost", 2222);
    OutputStream out = s.getOutputStream();
    STDrawingArea drawingArea = new STDrawingArea();
    public STTestMain() throws IOException {
        //JFrame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Gartic");
        setResizable(false);
        setVisible(true);
        setJMenuBar(createMenuBar());


        //Panel of buttons
        JPanel buttonContainer = new JPanel();
        if(empfangen) {
            JButton btnRedPen = new JButton("Fertig");
            JButton btnGreenPen = new JButton("Green Pen");
            JButton btnClear = new JButton("Clear");
            buttonContainer.add(btnRedPen);
            buttonContainer.add(btnGreenPen);
            buttonContainer.add(btnClear);
            //Drawing Area instantiation
            //button listener
            btnRedPen.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    BufferedImage image = STDrawingArea.getImage();
                    try {
                        ImageIO.write(image, "PNG", out);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            btnGreenPen.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    drawingArea.setCurrentColor(Color.GREEN);
                }
            });

            btnClear.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    drawingArea.clearDrawings();
                }
            });
        }else{
            JTextField ergebnis = new JTextField("Was siehst du?", 30);
            JButton btnRedPen = new JButton("Fertig");
            btnRedPen.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                    
                }
            });
        }
        //Adding things to JFrame
        getContentPane().add(drawingArea);
        getContentPane().add(buttonContainer,BorderLayout.PAGE_END);
        pack();



    }

    public static void main(String args[]) throws IOException {
        STTestMain test = new STTestMain();
    }
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createColorMenu());
        return menuBar;
    }
    private JMenu createColorMenu() {
        JMenu editMenu = new JMenu("Color");
        JMenuItem re = new JMenuItem("Red");
        re.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                drawingArea.setCurrentColor(Color.RED);
            }});
        editMenu.add(re);
        JMenuItem cus = new JMenuItem("Custom");
        cus.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                drawingArea.setCurrentColor(JColorChooser.showDialog(null,
                        "Farbauswahl", null));
                System.out.println("cus");
            }});

        editMenu.add(cus);
        return editMenu;
    }

    public void receive() throws Exception{
        ServerSocket serverSocket = new ServerSocket(13085);
        Socket socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();

        System.out.println("Reading: " + System.currentTimeMillis());

        byte[] sizeAr = new byte[4];
        inputStream.read(sizeAr);
        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

        byte[] imageAr = new byte[size];
        inputStream.read(imageAr);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

        System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
        drawingArea.clearDrawings();
        drawingArea.add(new JLabel(new ImageIcon(image)));
        serverSocket.close();
    }

}