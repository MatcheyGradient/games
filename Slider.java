import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Slider extends JFrame {
    public static Panel p = new Panel();

    public Slider() {
        this.add(p);
        this.setTitle("drawer");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Slider();
    }

    public static class Panel extends JPanel implements ActionListener {

        static final int WIDTH = 900;
        static final int HEIGHT = 900;

        static int size = 5;

        static int[] current = new int[]{-1, -1};

        static boolean hover = false;

        static float wid = (700f/size);

        static int[][] map = new int[size][size];

        static List<int[]> around = new ArrayList<>();

        static int moves = 0;

        public Panel() {
            this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            this.setBackground(Color.BLACK);
            this.setFocusable(true);
            this.addMouseMotionListener(new DragListener());
            this.addMouseListener(new ClickListener());
            this.addKeyListener(new KeyListener());

            reset();
            scramble();
            getSquares();
        }

        public static void redef(){
            map = new int[size][size];
            wid = (700f/size);
        }

        public static void scramble(){
            Random r = new Random();
            for(int i = 0; i < 10000; i++){
                getSquares();
                int[] chosen = around.get(r.nextInt(around.size()));
                map[findEmpty()[0]][findEmpty()[1]] = map[chosen[0]][chosen[1]];
                map[chosen[0]][chosen[1]] = -1;
            }
        }

        public static void reset(){
            moves = 0;
            for(int i = 0; i < size*size; i++){
                if(i != size*size - 1){
                    map[(int) Math.floor((float) i / size)][i % size] = i + 1;
                } else {
                    map[size - 1][size - 1] = -1;
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            g.setColor(Color.white);

            g.setFont(new Font("Arial", Font.PLAIN, 40));

            g.drawString("Moves: " + moves, 25, 60);
            g.drawString("R: Reset, ↑ ↓: Size, S: Scramble", 25, 870);

            g.setColor(new Color(0, 17, 65));
            g.fillRect((int) (100 + findEmpty()[1] * wid), (int) (100 + findEmpty()[0] * wid), (int) wid, (int) wid);

            paintSelection(g);

            g.setColor(Color.white);

            g.drawRect(100, 100, 700, 700);
            for(int i = 0 ; i < size; i++){
                g.drawLine(100, (int) (100 + i * wid), 800, (int) (100 + i * wid));
                g.drawLine((int) (100 + i * wid), 100, (int) (100 + i * wid), 800);
            }

            g.setFont(new Font("Arial", Font.BOLD, (int) wid/2));

            for(int i = 0; i < size; i++){
                for(int k = 0; k < size; k++){
                    if(map[k][i] != -1){
                        int width = g.getFontMetrics().stringWidth(Integer.toString(map[k][i]));
                        int height = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
                        g.drawString(Integer.toString(map[k][i]), (int) ((100 + i * wid) + (wid / 2f) - (width / 2f)), (int) ((100 + k * wid) + height + (wid - height) / 2f));
                    }
                }
            }
        }

        public static int[] findEmpty(){
            for(int i = 0; i < size; i++){
                for(int k = 0; k < size; k++){
                    if(map[k][i] == -1){
                        return new int[]{k, i};
                    }
                }
            }
            return new int[]{-99, -99};
        }

        public static void getSquares(){
            int[] e = findEmpty();

            around.clear();
            around.add(new int[]{e[0] - 1, e[1]});
            around.add(new int[]{e[0] + 1, e[1]});
            around.add(new int[]{e[0], e[1] - 1});
            around.add(new int[]{e[0], e[1] + 1});

            around.removeIf(i -> (i[1] < 0 || i[1] > size - 1) || (i[0] < 0 || i[0] > size - 1));
        }

        public boolean containsSelection(int[] selection){
            boolean choice = false;

            for(int[] i : around){
                if (i[0] == selection[0] && i[1] == selection[1]) {
                    choice = true;
                    break;
                }
            }

            return choice;
        }

        public void paintSelection(Graphics g){
            getSquares();

            g.setColor(new Color(46, 46, 46));

            int x = (int) Math.floor((DragListener.mX - 100) / wid);
            int y = (int) Math.floor((DragListener.mY - 100) / wid);

            if(x > -1 && x < size && y > -1 && y < size){
                if(!(x == findEmpty()[1] && y == findEmpty()[0])){
                    g.fillRect((int) (100 + x * wid), (int) (100 + y * wid), (int) wid, (int) wid);
                }
            }

            if(current[0] != -1){
                g.setColor(new Color(127, 0, 0));
                if(containsSelection(current)) g.setColor(new Color(6, 86, 0));
                g.fillRect((int) (100 + current[1] * wid), (int) (100 + current[0] * wid), (int) wid, (int) wid);
            }

            g.setColor(new Color(88, 70, 0));
            if(hover){
                g.fillRect((int) (100 + findEmpty()[1] * wid), (int) (100 + findEmpty()[0] * wid), (int) wid, (int) wid);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {}

        public static class KeyListener implements java.awt.event.KeyListener {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_R -> {
                        reset();
                        p.repaint();
                    }
                    case KeyEvent.VK_UP -> {
                        size++;
                        redef();
                        reset();
                        p.repaint();
                    }
                    case KeyEvent.VK_DOWN -> {
                        if(size > 2){
                            size--;
                            redef();
                            reset();
                            p.repaint();
                        }
                    }
                    case KeyEvent.VK_S -> {
                        scramble();
                        moves = 0;
                        p.repaint();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        }

        public static class DragListener implements MouseMotionListener {
            public static int mX = 0;
            public static int mY = 0;

            @Override
            public void mouseDragged(MouseEvent e) {
                if(p.containsSelection(current)) {
                    int x = (int) Math.floor((e.getX() - 100) / wid);
                    int y = (int) Math.floor((e.getY() - 100) / wid);
                    hover = Arrays.equals(findEmpty(), new int[]{y, x});
                }
                p.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mX = e.getX();
                mY = e.getY();
                p.repaint();
            }
        }

        public static class ClickListener implements MouseListener {
            @Override
            public void mousePressed(MouseEvent e) {
                if(!((int) Math.floor((e.getY() - 100) / wid) > size - 1 || (int) Math.floor((e.getX() - 100) / wid) < 0 || (int) Math.floor((e.getX() - 100) / wid) > size - 1)){
                    current = new int[]{(int) Math.floor((e.getY() - 100) / wid), (int) Math.floor((e.getX() - 100) / wid)};
                }
                p.repaint();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(hover){
                    map[findEmpty()[0]][findEmpty()[1]] = map[current[0]][current[1]];
                    map[current[0]][current[1]] = -1;
                    moves++;
                }

                current = new int[]{-1, -1};

                hover = false;
                p.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        }
    }
}
