import java.awt.Point;

//đã xong mục này
public class GameState {
	
	/* Tổng quan:
	Trò chơi được thể hiện bằng một lưới các ô có chiều cao * chiều rộng,
	số lượng rắn thay đổi (mảng tọa độ tạo thành đầu/cơ thể của rắn + ID của nó + trạng thái của nó (còn sống / đã chết))
	một số lượng thực phẩm khác nhau (toạ độ x và y phối hợp cho mỗi thực phẩm).
	Đối với mỗi đánh dấu trò chơi, trò chơi sẽ gói tất cả thông tin này và gửi đến một lớp
	sau đó sẽ gửi thông tin đó đến "bộ não" của mỗi con rắn.
	Bộ não này sẽ đưa vào trạng thái trò chơi và ID của con rắn mà nó cần để quyết định sau đó quay trở lại
	một hướng mà nó quyết định rằng con rắn nên đi.
	Một bộ não có thể là bộ định tuyến mặc định của tôi, một bộ não có thể dành cho trình phát thủ công thực tế được điều khiển bằng bàn phím,
	và một bộ não khác có thể là lớp vỏ để các lập trình viên khác xây dựng AI của riêng họ.
	Thông tin đi kèm phải là bản sao của các biến thực tế của trò chơi để lớp 'não' không thể thay đổi
	Biến 'phía máy chủ'.
	Gamestate được đóng gói có thể là một lớp trong đó getSnakes () là phương thức trả về một đối tượng trong đó 'getId () trả về id,
	 getDirection () trả về hướng hiện tại và getCoordLS () trả về danh sách tọa độ được sắp xếp từ đầu đến đuôi.
	Các phương thức khác trong lớp gamestate có thể bao gồm getHeight (), getWidth (), getFood () (danh sách các đối tượng Point đại diện
	tọa độ của từng miếng thức ăn).
	*/

    private int width;//rộng
    private int height;//dài
    private Snake[] snakes;//rắn
    private Point[] food;//ăn
    private int[] scores;//điểm
    private int snakes_alive;//trạng thái

    public GameState(int width, int height, Snake[] orig_snakes, Point[] orig_food)
    //khung và thức ăn
    {
        snakes = new Snake[orig_snakes.length];
        food = new Point[orig_food.length];
        for (int i = 0, n = snakes.length; i < n; i++) {
            snakes[i] = orig_snakes[i].clone();
        }
        for (int i = 0, n = food.length; i < n; i++) {
            food[i] = new Point(orig_food[i].x, orig_food[i].y);
        }
        this.width = width;
        this.height = height;
    }

    public GameState(int width, int height, Snake[] orig_snakes, Point[] orig_food, int[] scores, int snakes_alive)
    //trạng thái rắn và điểm
    {
        this(width, height, orig_snakes, orig_food);
        this.snakes_alive = snakes_alive;
        this.scores = new int[scores.length];
        for (int i = 0; i < scores.length; i++) {
            this.scores[i] = scores[i];
        }
    }

    //trả về các tham số
    public int getWidth() {
        return width;
    }

    public int getSnakesAlive() {
        return snakes_alive;
    }

    public int[] getScores() {
        return scores;
    }

    public int getHeight() {
        return height;
    }

    public Snake[] getSnakes() {
        return snakes;
    }

    public Point[] getFood() {
        return food;
    }
}