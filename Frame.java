//chương trình chính
import java.awt.EventQueue;
import javax.swing.JFrame;

//đã xong mục này
public class Frame extends JFrame {

    /*
     * Ý tưởng có thể nâng cấp:
     * Thêm con rắn thứ hai. Làm cho một con rắn có thể chơi được bởi con người.
     * Thêm chướng ngại vật
     */
    private Board board;//khởi tạo

    public Frame() {
        initFrame();
    }

    public void initFrame() //thiết lập đồ hoạ
    {
        board = new Board();
        add(board);
        board.start();

        setResizable(false);
        setTitle("Snake AI");
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) //chương trình chính
    {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Frame game = new Frame();
                game.setVisible(true);
            }
        });
    }
}
