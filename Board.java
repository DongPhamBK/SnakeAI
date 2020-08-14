
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

import javax.swing.JPanel;

//đã xong mục này
public class Board extends JPanel implements Runnable, KeyListener {
	
	/*
	Thiết lập khả năng yêu cầu nó chơi nhiều trò chơi liên tiếp và giữ điểm cho đến khi kết thúc nơi nó hiển thị.
	 Ý tưởng tốt để so sánh các thuật toán AI
	Đúng ra nên thiết lập khả năng 'tua lại' một vài động tác trong khi trò chơi bị tạm dừng / trò chơi kết thúc để có cái nhìn rõ hơn về những gì đã xảy ra
*/
    // Cài đặt trò chơi
    // Thiết lập mảng được mã hóa chỉ chiếm tối đa 6 con rắn. Nếu muốn bao gồm nhiều hơn, phải thêm giá trị vào.

    private final int num_snakes = 1; // chỉnh số lượng rắn (max = 6).
    private int[] snake_brains = {1, 1, 1, 1, 1, 1}; // thiết lập chơi: 0 là con người điều khiển, 1 là AI mặc định, 2 là để phát triển sau
    // mảng int này phải có độ dài ít nhất bằng số lượng rắn (num_snakes) và chỉ phải chứa trạng thái 0, 1 hoặc 2

    private final int num_food = 1; // có bao nhiêu miếng thức ăn, mặc định chọn 1
    private final int boardW = 20; // bảng sẽ rộng bao nhiêu ô
    private final int boardH = 20; // bảng sẽ dài bao nhiêu ô
    private final int blockSize = 30; // kích thước mỗi ô
    private long updateTime = 60;  //tốc độ chạy cũng như suy nghĩ, chỉ số càng cao càng chậm do mất thời gian nghĩ
    // thời gian chạy ( mili giây) (thấp hơn = nhanh hơn, cao hơn = chậm hơn)
    // phải dành đủ thời gian cho AI của mỗi con rắn để đưa ra quyết định

    private final Color[] colors = {Color.green, Color.yellow, Color.magenta, Color.pink, Color.blue, Color.black};
    // màu sắc cho (tối đa 6) con rắn
    // 3 mảng tiếp theo dành cho vị trí và hướng bắt đầu của rắn khi bắt đầu chơi
    // vị trí hướng về giữa của từng con rắn theo thứ tự này: dưới cùng bên trái, trên cùng bên phải, trên cùng bên trái, dưới cùng bên phải, giữa trên cùng, giữa dưới
    private final int[] startX = {(int) (1.0 * boardW / 4.0), (int) (3.0 * boardW / 4.0), (int) (1.0 * boardW / 4.0), (int) (3.0 * boardW / 4.0), (int) (2.0 * boardW / 4.0), (int) (2.0 * boardW / 4.0)};
    private final int[] startY = {(int) (3.0 * boardH / 4.0), (int) (1.0 * boardH / 4.0), (int) (1.0 * boardH / 4.0), (int) (3.0 * boardH / 4.0), (int) (1.0 * boardH / 4.0), (int) (3.0 * boardH / 4.0)};
    private final Direction[] startDirection = {Direction.Right, Direction.Left, Direction.Right, Direction.Left, Direction.Down, Direction.Up};

    //phân biệt các con rắn, chú ý vị trí thanh dưới của trò chơi khi chạy
    enum Tile {Empty, Food, Snake1, Snake1Head, Snake2, Snake2Head, Snake3, Snake3Head, Snake4, Snake4Head, Snake5, Snake5Head, Snake6, Snake6Head}

    ;
    Tile[] headTiles = {Tile.Snake1Head, Tile.Snake2Head, Tile.Snake3Head, Tile.Snake4Head, Tile.Snake5Head, Tile.Snake6Head};
    Tile[] bodyTiles = {Tile.Snake1, Tile.Snake2, Tile.Snake3, Tile.Snake4, Tile.Snake5, Tile.Snake6};

    //không sửa đổi những mục dưới đây vì sẽ gây lỗi
    private Tile[][] board;
    private boolean gameOn, paused, playable;
    private Snake[] snakes;
    private Thread thread;
    private String statusbar;
    private long time1, time2;
    private Point[] food;
    private int scores[];
    private int snakesAlive;
    private LinkedList<GameState> previous_turns;
    private final int max_turns = 40;//số lượt

    private Direction fetchMove(int ai, GameState gs, int index) {
        if (ai == 1) {
            Direction dir = SnakeAI1.getSingleMove(gs, index);
            return dir;
        }
		/*else if (ai == 2) {
			return SnakeAI2.getSingleMove(gs, index);
		}  //đoạn này dành để nâng cấp sau*/
        return null;
    }

    public Board() //tạo mới
    {
        setPreferredSize(new Dimension(boardW * blockSize, boardH * blockSize + 17));
        setFocusable(true);
        addKeyListener(this);
        board = new Tile[boardH][boardW];
        previous_turns = new LinkedList<>();
        initBoard();
        playable = true;
        time1 = System.currentTimeMillis();
    }

    public void initBoard() {
        for (int i = 0; i < boardH; i++) {
            for (int j = 0; j < boardW; j++) {
                board[i][j] = Tile.Empty;
            }
        }
    }

    //các thiết lập phụ
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOn) {
            updateBoard();
            drawBoard(g);
        } else {
            drawBoard(g);
        }
    }

    public void drawBoard(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, getSize().width, getSize().height);
        for (int i = 0; i < boardH; i++) {
            for (int j = 0; j < boardW; j++) {
                Tile cell = board[i][j];
                if (cell == Tile.Empty) {
                    g.setColor(Color.white);
                } else if (cell == Tile.Food) {
                    g.setColor(Color.red);
                } else {
                    int index = indexHeadCell(cell);
                    if (index != -1) {
                        // cung cấp cho các ô đầu rắn một đường viền màu đen cho dễ phân biệt
                        g.setColor(Color.black);
                        g.fillRect((j * blockSize), (i * blockSize), blockSize + 2, blockSize + 2);
                        g.setColor(snakes[index].getColor());
                    } else {
                        index = indexBodyCell(cell);
                        if (index == -1) {
                            System.out.println("Lỗi màu hoặc phông");
                            System.exit(9);//thoát
                        }
                        g.setColor(snakes[index].getColor());
                    }
                }
                g.fillRect((j * blockSize) + 1, (i * blockSize) + 1, blockSize - 1, blockSize - 1);
            }
        }
        g.setColor(Color.black);
        g.drawString(statusbar, 5, getSize().height - 3);
    }

    private int indexBodyCell(Tile cell) {
        for (int i = 0; i < num_snakes; i++) {
            if (cell == bodyTiles[i]) {
                return i;
            }
        }
        return -1;
    }

    private int indexHeadCell(Tile cell) {
        for (int i = 0; i < num_snakes; i++) {
            if (cell == headTiles[i]) {
                return i;
            }
        }
        return -1;
    }

    //cập nhật board
    private void updateBoard() {
        initBoard();
        for (int i = 0; i < snakes.length; i++) {
            // đặt tất cả các xác rắn còn sống lên bảng
            // không nên có va chạm ở đây vì mỗi lượt đầu được kiểm tra va chạm
            if (!snakes[i].isAlive()) {
                continue;
            }
            List<Point> coords = snakes[i].getCoords();

            for (int j = 1; j < coords.size(); j++) {
                int x = coords.get(j).x;
                int y = coords.get(j).y;
                board[y][x] = bodyTiles[i];
            }
        }

        //kiểm tra va chạm
        for (int i = 0; i < snakes.length; i++) {
            // bây giờ thêm đầu trong khi kiểm tra va chạm
            if (!snakes[i].isAlive()) {
                continue;
            }
            int headX = snakes[i].getHeadX();
            int headY = snakes[i].getHeadY();
            if (headX >= boardW || headX < 0 || headY >= boardH || headY < 0) {
                snakesAlive--;
                snakes[i].setAliveStatus(false);
                continue;
            }
            Tile cell = board[headY][headX];
            if (cell == Tile.Empty) {
                board[headY][headX] = headTiles[i];
            } else {
                collision(headX, headY, i);
            }
        }
        for (int i = 0; i < num_food; i++) {
            // đặt thức ăn lên board. thức ăn nên ở trước đầu rắn hoặc trên ô trống
            // nếu nó ở trước đầu rắn, hãy gọi phương thức cho con rắn đó ăn nó + sinh ra một quả táo mới
            int foodX = food[i].x;
            int foodY = food[i].y;
            if (board[foodY][foodX] == Tile.Empty) {
                board[foodY][foodX] = Tile.Food;
            } else {
                switch (board[foodY][foodX]) {
                    case Snake1Head:
                        snakes[0].eat();
                        scores[0]++;
                        break;
                    case Snake2Head:
                        snakes[1].eat();
                        scores[1]++;
                        break;
                    case Snake3Head:
                        snakes[2].eat();
                        scores[2]++;
                        break;
                    case Snake4Head:
                        snakes[3].eat();
                        scores[3]++;
                        break;
                    case Snake5Head:
                        snakes[4].eat();
                        scores[4]++;
                        break;
                    case Snake6Head:
                        snakes[5].eat();
                        scores[5]++;
                        break;
                    default:
                        System.out.println("Lỗi: Thức ăn nằm trên một ô không trắng hoặc không hợp lí.");
                        System.exit(8);//thoát
                }
                spawnFood(i);
            }
        }
        if (snakesAlive <= 0 || (num_snakes > 1 && snakesAlive < 2)) {
            gameOn = false;
        }
        setStatusBar();
    }

    //kiểm tra trạng thái của rắn (sống/chết)
    public void collision(int x, int y, int index) {
        Tile cell = board[y][x];
        int bodyIndex = indexBodyCell(cell);
        if (bodyIndex == -1) {
            int headIndex = indexHeadCell(cell);
            if (snakes[headIndex].getLength() == snakes[index].getLength()) {
                snakes[headIndex].setAliveStatus(false);
                snakes[index].setAliveStatus(false);
                snakesAlive -= 2;
            } else if (snakes[headIndex].getLength() > snakes[index].getLength()) {
                snakes[index].setAliveStatus(false);
                snakesAlive--;
            } else {
                snakes[headIndex].setAliveStatus(false);
                snakesAlive--;
            }
        } else {
            snakes[index].setAliveStatus(false);
            snakesAlive--;
        }
        return;
    }

    public void updateSnakes() {
        GameState gs = new GameState(boardW, boardH, snakes, food, scores, snakesAlive);
        add_turn(gs);
        for (int i = 0; i < num_snakes; i++) {
            if (!snakes[i].isAlive()) {
                continue;
            }
            if (snake_brains[i] == 1 || snake_brains[i] == 2) {
                gs = new GameState(boardW, boardH, snakes, food);
                System.out.println("Di chuyển rắn số " + i);
                Direction dir = fetchMove(snake_brains[i], gs, i);
                System.out.println(dir_to_string(dir));
                if (dir != null) {
                    snakes[i].moveDirection(dir);
                }
            }
        }
        for (int i = 0; i < num_snakes; i++) {
            if (!snakes[i].isAlive()) {
                continue;
            }
            snakes[i].moveForward();
        }
    }

    public String dir_to_string(Direction dir) {
        if (dir == null) {
            return "Null";
        }
        switch (dir) {
            case Up:
                return "Lên";
            case Down:
                return "Xuống";
            case Right:
                return "Phải";
            case Left:
                return "Trái";
        }
        return "Bất ngờ";
    }

    public void restart() {
        board = new Tile[boardH][boardW];
        initBoard();
        time1 = System.currentTimeMillis();
        start();
    }

    public void start() {
        if (num_snakes > 6) {
            // tối đa là 6 vì mã hóa 6 màu khác nhau và vị trí / hướng bắt đầu cho chúng
            System.out.println("num_snakes tối đa là 6, giá trị cài đặt đang lớn hơn.");
            System.exit(7);//thoát
        }
        snakes = new Snake[num_snakes];
        food = new Point[num_food];
        scores = new int[num_snakes];
        for (int i = 0; i < num_snakes; i++) {
            snakes[i] = new Snake(i, startX[i], startY[i], startDirection[i], colors[i]);
        }
        for (int i = 0; i < num_food; i++) {
            spawnFood(i);
        }
        snakesAlive = num_snakes;
        setStatusBar();

        gameOn = true;
        paused = false;
        thread = new Thread(this);
        thread.start();
    }

    private void setStatusBar() {
        statusbar = "Tốc độ: " + updateTime;
        for (int i = 0; i < num_snakes; i++) {
            statusbar += " | s" + (i + 1) + ":" + scores[i];
            if (!snakes[i].isAlive())//rắn đã "lên bảng đếm số"
            {
                statusbar += "(X)";
            }
        }
        return;
    }

    public void spawnFood(int index) {
        Tile[][] temp = new Tile[boardH][boardW];
        for (int i = 0, n = food.length; i < n; i++) {
            if (food[i] == null) {
                continue;
            }
            temp[food[i].y][food[i].x] = Tile.Food;
        }
        List<Integer> shuffledX = new ArrayList<Integer>();
        List<Integer> shuffledY = new ArrayList<Integer>();
        for (int i = 0; i < boardW; i++) {
            shuffledX.add(i);
        }
        for (int i = 0; i < boardH; i++) {
            shuffledY.add(i);
        }
        Collections.shuffle(shuffledX);
        Collections.shuffle(shuffledY);

        for (int i = 0; i < boardH; i++) {
            for (int j = 0; j < boardW; j++) {
                if (temp[shuffledY.get(i)][shuffledX.get(j)] == Tile.Food) {
                    continue;
                }
                if (board[shuffledY.get(i)][shuffledX.get(j)] == Tile.Empty) {
                    food[index] = new Point(shuffledX.get(j), shuffledY.get(i));
                    return;
                }
            }
        }
    }

    public void add_turn(GameState gs) {
        previous_turns.addFirst(gs);
        if (previous_turns.size() > max_turns) {
            gs = previous_turns.removeLast();
        }
    }

    public void previous_turn() {
        if (previous_turns.size() > 0) {
            GameState gs = previous_turns.removeFirst();
            snakes = gs.getSnakes();
            food = gs.getFood();
            scores = gs.getScores();
            snakesAlive = gs.getSnakesAlive();
        }
    }

    public void print_snakes() {
        for (int i = 0; i < snakes.length; i++) {
            List<Point> coords = snakes[i].getCoords();
            System.out.print("Snake " + snakes[i].getID() + ":");
            for (int j = 0; j < coords.size(); j++) {
                System.out.print(" (" + coords.get(j).x + "," + coords.get(j).y + ")");
            }
            System.out.println();
        }
    }

    public void run() {
        while (playable) {
            if (!paused) {
                time2 = System.currentTimeMillis();
                if (time2 - time1 >= updateTime) {
                    if (gameOn) {
                        updateSnakes();
                        //updateBoard();
                    }
                    repaint();
                    time1 = time2;
                }
            }
        }

        playable = true;
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case (KeyEvent.VK_P):
                paused = !paused;
                break;
            case (KeyEvent.VK_SPACE):
                if (!gameOn) {
                    playable = false;
                    restart();
                    break;
                }
            case (KeyEvent.VK_OPEN_BRACKET):
                if (gameOn) {
                    updateTime += 10;
                }
                break;
            case (KeyEvent.VK_CLOSE_BRACKET):
                if (gameOn) {
                    if (updateTime > 19)
                        updateTime -= 10;
                    else updateTime = 10;
                }
                break;
            case (KeyEvent.VK_LEFT):
                if (!gameOn || paused) {
                    gameOn = true;
                    paused = true;
                    previous_turn();
                    repaint();
                }
                break;
            case (KeyEvent.VK_RIGHT):
                if (!gameOn || paused) {
                    gameOn = true;
                    paused = true;
                    updateSnakes();
                    repaint();
                }
        }
    }

    public void keyPressed(KeyEvent e) {
        int WASDindex = -1;
        int arrowIndex = -1;
        for (int i = 0; i < num_snakes; i++) {
            if (snake_brains[i] == 0) {
                if (WASDindex == -1) {
                    WASDindex = i;
                } else if (arrowIndex == -1) {
                    System.out.println("Không thể có nhiều hơn 2 con rắn được điều khiển bằng bàn phím.");
                    System.exit(10);
                } else {
                    arrowIndex = i;
                }
            }
        }
        if (WASDindex != -1) {
            Direction tmp = snakes[WASDindex].getDirection();
            switch (e.getKeyCode()) {
                case (KeyEvent.VK_W):
                    if (tmp != Direction.Down) {
                        snakes[WASDindex].moveDirection(Direction.Up);
                    }
                    break;
                case (KeyEvent.VK_S):
                    if (tmp != Direction.Up) {
                        snakes[WASDindex].moveDirection(Direction.Down);
                    }
                    break;
                case (KeyEvent.VK_D):
                    if (tmp != Direction.Left) {
                        snakes[WASDindex].moveDirection(Direction.Right);
                    }
                    break;
                case (KeyEvent.VK_A):
                    if (tmp != Direction.Right) {
                        snakes[WASDindex].moveDirection(Direction.Left);
                    }
                    break;
            }
        }
        if (arrowIndex != -1) {
            Direction tmp = snakes[arrowIndex].getDirection();
            switch (e.getKeyCode()) {
                case (KeyEvent.VK_UP):
                    if (tmp != Direction.Down) {
                        snakes[arrowIndex].moveDirection(Direction.Up);
                    }
                    break;
                case (KeyEvent.VK_DOWN):
                    if (tmp != Direction.Up) {
                        snakes[arrowIndex].moveDirection(Direction.Down);
                    }
                    break;
                case (KeyEvent.VK_RIGHT):
                    if (tmp != Direction.Left) {
                        snakes[arrowIndex].moveDirection(Direction.Right);
                    }
                    break;
                case (KeyEvent.VK_LEFT):
                    if (tmp != Direction.Right) {
                        snakes[arrowIndex].moveDirection(Direction.Left);
                    }
                    break;
            }
        }
    }
}
