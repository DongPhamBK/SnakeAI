import java.util.*;
import java.awt.Point;
//đã xong mục này
public class SnakeAI1 {
	
	/*
	Khi trò chơi được thiết lập chỉ có 1 con rắn và 1 thức ăn trên bảng, mình dựa trên các quyết định AI của mình xung quanh ưu tiên có thể luôn luôn
	đạt đến đuôi của chính nó (tự cắn nó). Có thể chạm tới 'đuôi' của chính bạn để đảm bảo bạn không tự mắc bẫy. Chỉ có một con rắn, nếu nó tìm thấy một
	đường dẫn đến thực phẩm 'an toàn' (có thể chạm tới 'đuôi' của chính mình sau đó), sau đó nó có thể cam kết với toàn bộ đường dẫn ngay lập tức.

	Tư duy rắn độc dựa trên việc có thể dự đoán hoàn toàn trạng thái tương lai của hệ thống.
	Với nhiều con rắn (và đôi khi là nhiều thức ăn), AI trở nên phức tạp hơn rất nhiều vì không thể dự đoán một cách đáng tin cậy các chuyển động của rắn khác.
	Dưới đây là một vài ý tưởng / khái niệm cho việc ra quyết định:
			:: khi chạy thuật toán tìm đường, bạn muốn ước tính + mô phỏng các quyết định AI của các con rắn khác cũng như cho mỗi lượt chơi tương đương của đường dẫn không?
			:: Mình nghi ngờ điều này sẽ dẫn đến sự gia tăng khá đáng kể về tỷ lệ thắng của bạn, tuy nhiên đó là sự gia tăng rất đáng kể trong tính toán mỗi lượt.
			:: nếu bạn quyết định mô phỏng đối thủ mỗi lượt dọc theo BFS:
			:: có thể đạt được 'đuôi' của bạn trên mỗi di chuyển vẫn là một ưu tiên? Tôi sẽ nghiêng về có vì sự an toàn bổ sung này giúp duy trì là rất mạnh mẽ.
            :: Làm thế nào AI của bạn sẽ đưa vào đối thủ di chuyển? có lẽ chỉ là một bfs đơn giản (sau đó làm thế nào để họ xử lý không có đường dẫn đến thực phẩm?)
            :: nếu bạn quyết định không mô phỏng mỗi lượt đi dọc BFS, bạn sẽ coi tọa độ rắn là chướng ngại vật đứng yên chăng ?!?
			::
			:: Làm thế nào để bạn quyết định thực phẩm nào để nhắm mục tiêu (ăn miếng nào ?)
			:: trước tiên bạn có thể chạy BFS trên tất cả các con rắn khác để xem khoảng cách ước tính của chúng với từng miếng thức ăn,
			:: sau đó chạy của bạn và xem nếu bạn là con rắn gần nhất với bất kỳ thực phẩm nhất định.
            ::  nếu có, hãy thực hiện 'đề phòng đường dẫn' trước hoặc nhảy ngay vào đó?
			:: nếu không phải là gần nhất với bất kỳ, hãy cố gắng thực hiện các chuyển động an toàn lý tưởng đưa bạn vào một vị trí hoặc là trung tâm hoặc đặc biệt tìm kiếm một chuyển động mang lại
            ::  tỷ lệ cược cao nhất gần với lần sinh sản tiếp theo.
			::
			:: tùy thuộc vào mức độ ưu tiên của thực phẩm, nó có thể mở ra cơ hội cho các kiểu chơi thụ động hơn nhiều trong đó
            :: phần lớn trò chơi của bạn được dành thời gian mua và chờ đợi các điều kiện rất lạc quan phát sinh để giành giật thức ăn.

	Khi con rắn giết thời gian vì không có con đường xứng đáng, thì cách giết thời gian đó có thể dẫn đến nhiều khả năng là con rắn gần nhất
	sẽ dài ra trong tương lai.

	solo_snake_mindset (rắn, thức ăn):
-) tìm đường đi ngắn nhất tới thực phẩm bằng BFS
-) nếu được tìm thấy, 'mô phỏng' đường dẫn đó, sau đó tìm đường dẫn đến vị trí của 'đuôi' của chính nó
-) nếu có thể chạm tới 'đuôi' của chính mình, điều này có nghĩa là bạn không tự nhốt mình để đường dẫn BFS an toàn (không đi vòng)

		** -) nếu không thể đi đến đuôi riêng, tuyến BFS không an toàn nên chúng ta phải tìm tuyến khác
-) mô phỏng từng di chuyển (lên, xuống, trái, phải) hợp lệ cho vị trí hiện tại:
			-) từ những mô phỏng đó, kiểm tra xem đuôi có thể đạt được hay không và nếu có thì tuyến đường đến 'đuôi' là bao lâu?
			-) nếu 'đuôi' có thể đạt được từ ít nhất một trong số các động tác
-) thực hiện di chuyển dẫn đến tuyến đường đuôi BFS hợp lệ dài nhất
-) nếu đuôi không thể đạt được từ bất kỳ di chuyển nào
-) nhắm đến một cái gì đó khác (có thể là các ô không có người khác ở xung quanh đuôi hoặc chỉ là ô có khoảng cách xa nhất được tìm thấy trong tuyến BFS / DFS)
-) thực hiện bước đi dẫn đến con đường dài nhất đến bất cứ điều gì bạn quyết định nhắm đến
-) nếu không tìm thấy tuyến đường BFS đến thực phẩm
-) sử dụng phương thức mua thời gian từ phía trên (dòng được đánh dấu bằng **)

	**Những ý tưởng khác:
	Chạy BFS đến thức ăn gần nhất cho mỗi con rắn để xem xét khả năng 3 lần di chuyển tiếp theo của nó. So sánh di chuyển của tuyến đường của bạn với những di chuyển này để giúp dự đoán nguy hiểm.
			(Nếu con rắn của chúng chạy vào cơ thể bạn, thì đó vẫn là con đường an toàn. Nếu con rắn của bạn chạy vào cơ thể chúng, nó không an toàn.)
	Bạn cũng có thể kiểm tra xem liệu tuyến đường của bạn có bị xâm phạm hay không bằng cách lưu trữ và kiểm tra tất cả các ô trên tuyến đường của bạn để bạn thấy tuyến đường của mình sẽ không bị mở.

	Thay vì chuyển vào toàn bộ mảng rắn cho mỗi tìm kiếm đường dẫn, hãy tạo một mảng đã truy cập một lần được sao chép vào phương thức để sử dụng.

	Làm cho tế bào "chuyển tiếp" của những con rắn khác cũng vượt quá giới hạn để giảm va chạm.
			*/

    public static Direction getSingleMove(GameState gs, int snakeID) {
        Snake snakes[] = gs.getSnakes();
        Point food[] = gs.getFood();
        int height = gs.getHeight();
        int width = gs.getWidth();

        Direction food_route_move = closest_or_tail_chase(snakes, snakeID, food, height, width);
        if (food_route_move != null) {
            return food_route_move;
        }
        return null;
    }

    private static void printBoard(Snake[] snakes, Point[] food, int height, int width) {
        char[][] board = new char[height][width];
        for (int i = 0; i < snakes.length; i++) {
            List<Point> coords = snakes[i].getCoords();
            for (int j = 0; j < coords.size(); j++) {
                board[coords.get(j).y][coords.get(j).x] = (char) ((int) 'a' + i);
            }
        }
        for (int i = 0; i < food.length; i++) {
            board[food[i].y][food[i].x] = '$';
        }
        for (int i = 0; i < width + 2; i++) {
            System.out.print("=");
        }
        System.out.println();
        for (int i = 0; i < height; i++) {
            System.out.print("=");
            for (int j = 0; j < width; j++) {
                if (board[i][j] == '\0') {
                    System.out.print(".");
                } else {
                    System.out.print(board[i][j]);
                }
            }
            System.out.println("=");
        }
        for (int i = 0; i < width + 2; i++) {
            System.out.print("=");
        }
        System.out.println();
    }

    private static Node tail_chasing_bfs(Snake[] snakes, int snakeID, Point[] food, int height, int width) {
        return null;
    }

    private static Node oracle_tail_chasing(Snake[] snakes, int snakeID, Point[] food, int height, int width) {
        return null;
    }

    private static Direction closest_or_tail_chase(Snake[] snakes, int snakeID, Point[] food, int height, int width) {
        int[] closest_snake = new int[food.length];
        Node[] routes_to_food = closest_snake_to_each_food(snakes, snakeID, food, height, width, closest_snake);
        for (int i = 0; i < food.length; i++) {
            if (routes_to_food[i] != null && closest_snake[i] == snakeID) {
                // con rắn của chúng ta là con rắn gần nhất với thức ăn này
                // mô phỏng tuyến đường BFS đến thức ăn sau đó xem con rắn của chúng ta có còn chạm được đuôi không
                // nếu có thể, hãy sử dụng bước đầu tiên từ tuyến đường
                // nếu không thể, bỏ qua thực phẩm này
                // nếu không có tuyến đường nào được chấp nhận, đuổi theo đuôi để an toàn
                List<Direction> directions = path_ends_safe(routes_to_food[i], snakes, snakeID, height, width);
                if (directions != null && directions.size() > 0) {
                    return directions.get(0);
                }
            }
        }
        Direction dir = long_tail_route(snakes, snakeID, height, width);
        if (dir != null) {
            return dir;
        } else {
            dir = safe_route(snakes, snakeID, height, width);
            if (dir == null) {
                System.out.println(" Không thể tìm thấy một tuyến đường an toàn");
            }
            return dir;
        }
    }

    private static Direction long_tail_route(Snake[] snakes, int snakeID, int height, int width) {
        System.out.println("Định tuyến quay lại đuôi.");
        int snake_index = -1;
        boolean[][] board_move_left = new boolean[height][width];
        boolean[][] board_move_right = new boolean[height][width];
        boolean[][] board_move_up = new boolean[height][width];
        boolean[][] board_move_down = new boolean[height][width];
        Point next_tail = new Point(-1, -1);
        System.out.println("Tìm kiếm ID: " + snakeID);
        for (int i = 0; i < snakes.length; i++) {
            System.out.println("index: " + i + " id: " + snakes[i].getID());
            if (snakeID == snakes[i].getID()) {
                snake_index = i;
                List<Point> coords = snakes[i].getCoords();
                for (int j = 0; j < coords.size() - 1; j++) {
                    board_move_left[coords.get(j).y][coords.get(j).x] = true;
                    board_move_right[coords.get(j).y][coords.get(j).x] = true;
                    board_move_up[coords.get(j).y][coords.get(j).x] = true;
                    board_move_down[coords.get(j).y][coords.get(j).x] = true;
                }
                //System.out.println("Hiển thị coords : "+coords.size());
                next_tail.x = coords.get(coords.size() - 1).x;
                next_tail.y = coords.get(coords.size() - 1).y;
            } else {
                List<Point> coords = snakes[i].getCoords();
                for (int j = 0; j < coords.size() - 1; j++) {
                    board_move_left[coords.get(j).y][coords.get(j).x] = true;
                    board_move_right[coords.get(j).y][coords.get(j).x] = true;
                    board_move_up[coords.get(j).y][coords.get(j).x] = true;
                    board_move_down[coords.get(j).y][coords.get(j).x] = true;
                }
            }
        }
        int headX = snakes[snake_index].getHeadX();
        int headY = snakes[snake_index].getHeadY();
        Direction dir = null;
        Node end_node = null;
        Node route = null;
        if (headX > 0 && board_move_left[headY][headX - 1] == false) {
            route = BFS_Route(board_move_left, new Point(headX - 1, headY), next_tail);
            if (route != null) {
                if (end_node == null) {
                    end_node = route;
                    dir = Direction.Left;
                } else if (route.getDepth() > end_node.getDepth()) {
                    end_node = route;
                    dir = Direction.Left;
                }
            }
        }
        if (headX < width - 1 && board_move_right[headY][headX + 1] == false) {
            route = BFS_Route(board_move_right, new Point(headX + 1, headY), next_tail);
            if (route != null) {
                if (end_node == null) {
                    end_node = route;
                    dir = Direction.Right;
                } else if (route.getDepth() > end_node.getDepth()) {
                    end_node = route;
                    dir = Direction.Right;
                }
            }
        }
        if (headY < height - 1 && board_move_down[headY + 1][headX] == false) {
            route = BFS_Route(board_move_right, new Point(headX, headY + 1), next_tail);
            if (route != null) {
                if (end_node == null) {
                    end_node = route;
                    dir = Direction.Down;
                } else if (route.getDepth() > end_node.getDepth()) {
                    end_node = route;
                    dir = Direction.Down;
                }
            }
        }
        if (headY > 0 && board_move_up[headY - 1][headX] == false) {
            route = BFS_Route(board_move_right, new Point(headX, headY - 1), next_tail);
            if (route != null) {
                if (end_node == null) {
                    end_node = route;
                    dir = Direction.Up;
                } else if (route.getDepth() > end_node.getDepth()) {
                    end_node = route;
                    dir = Direction.Up;
                }
            }
        }
        return dir;
    }

    private static Direction safe_route(Snake[] snakes, int snakeID, int height, int width) {
        System.out.println("Lộ trình an toàn");
        boolean[][] board = new boolean[height][width];
        boolean[][] board_move_left = new boolean[height][width];
        boolean[][] board_move_right = new boolean[height][width];
        boolean[][] board_move_up = new boolean[height][width];
        boolean[][] board_move_down = new boolean[height][width];
        int snake_index = -1;
        for (int i = 0; i < snakes.length; i++) {
            if (snakes[i].getID() == snakeID) {
                snake_index = i;
            }
            List<Point> coords = snakes[i].getCoords();
            for (int j = 0; j < coords.size(); j++) {
                board[coords.get(j).y][coords.get(j).x] = true;
                if (j == coords.size() - 1) {
                    break;
                }
                board_move_left[coords.get(j).y][coords.get(j).x] = true;
                board_move_right[coords.get(j).y][coords.get(j).x] = true;
                board_move_up[coords.get(j).y][coords.get(j).x] = true;
                board_move_down[coords.get(j).y][coords.get(j).x] = true;
            }
        }
        Node furthest_node = maximum_bfs(board, snakes[snake_index].getHeadX(), snakes[snake_index].getHeadY());
        if (furthest_node == null) {
            System.out.println("Unable to find a furthest node?");
            return null;
        }
        Point target = new Point(furthest_node.getX(), furthest_node.getY());
        int headX = snakes[snake_index].getHeadX();
        int headY = snakes[snake_index].getHeadY();
        Direction dir = null;
        Node end_node = null;
        Node route = null;
        if (headX > 0 && board_move_left[headY][headX - 1] == false) {
            route = BFS_Route(board_move_left, new Point(headX - 1, headY), target);
            if (route != null) {
                if (end_node == null) {
                    end_node = route;
                    dir = Direction.Left;
                } else if (route.getDepth() > end_node.getDepth()) {
                    end_node = route;
                    dir = Direction.Left;
                }
            }
        }
        if (headX < width - 1 && board_move_right[headY][headX + 1] == false) {
            route = BFS_Route(board_move_right, new Point(headX + 1, headY), target);
            if (route != null) {
                if (end_node == null) {
                    end_node = route;
                    dir = Direction.Right;
                } else if (route.getDepth() > end_node.getDepth()) {
                    end_node = route;
                    dir = Direction.Right;
                }
            }
        }
        if (headY < height - 1 && board_move_down[headY + 1][headX] == false) {
            route = BFS_Route(board_move_right, new Point(headX, headY + 1), target);
            if (route != null) {
                if (end_node == null) {
                    end_node = route;
                    dir = Direction.Down;
                } else if (route.getDepth() > end_node.getDepth()) {
                    end_node = route;
                    dir = Direction.Down;
                }
            }
        }
        if (headY > 0 && board_move_up[headY - 1][headX] == false) {
            route = BFS_Route(board_move_right, new Point(headX, headY - 1), target);
            if (route != null) {
                if (end_node == null) {
                    end_node = route;
                    dir = Direction.Up;
                } else if (route.getDepth() > end_node.getDepth()) {
                    end_node = route;
                    dir = Direction.Up;
                }
            }
        }
        return dir;
    }

    private static Node maximum_bfs(boolean[][] visited, int startX, int startY) {
        int height = visited.length;
        int width = visited[0].length;
        Queue<Node> q = new LinkedList<>();
        Node current_node = new Node(startX, startY, null, 0);
        Node furthest_node = current_node;
        q.add(current_node);
        while (q.size() > 0) {
            current_node = q.remove();
            if (current_node.getDepth() > furthest_node.getDepth()) {
                furthest_node = current_node;
            }
            if (current_node.getX() > 0) {
                if (visited[current_node.getY()][current_node.getX() - 1] == false) {
                    visited[current_node.getY()][current_node.getX() - 1] = true;
                    q.add(new Node(current_node.getX() - 1, current_node.getY(), current_node, current_node.getDepth() + 1));
                }
            }
            if (current_node.getX() < width - 1) {
                if (visited[current_node.getY()][current_node.getX() + 1] == false) {
                    visited[current_node.getY()][current_node.getX() + 1] = true;
                    q.add(new Node(current_node.getX() + 1, current_node.getY(), current_node, current_node.getDepth() + 1));
                }
            }
            if (current_node.getY() > 0) {
                if (visited[current_node.getY() - 1][current_node.getX()] == false) {
                    visited[current_node.getY() - 1][current_node.getX()] = true;
                    q.add(new Node(current_node.getX(), current_node.getY() - 1, current_node, current_node.getDepth() + 1));
                }
            }
            if (current_node.getY() < height - 1) {
                if (visited[current_node.getY() + 1][current_node.getX()] == false) {
                    visited[current_node.getY() + 1][current_node.getX()] = true;
                    q.add(new Node(current_node.getX(), current_node.getY() + 1, current_node, current_node.getDepth() + 1));
                }
            }
        }
        return furthest_node;
    }

    private static List<Direction> path_ends_safe(Node start_node, Snake[] snakes, int snakeID, int height, int width) {
        Snake future_snake = snakes[snakeID].clone();
        List<Direction> directions = directions_from_start_node(start_node);
        future_snake.setMoveQ(directions);
        while (future_snake.getMoveQ().size() != 0 && future_snake.getMoveQ().get(0) != null) {
            future_snake.moveForward();
        }
        Node path_back_to_tail = bfs_to_tail(snakes, snakeID, future_snake, height, width, start_node.getDepth());
        if (path_back_to_tail != null) {
            return directions;
        }
        return null;
    }

    private static Node bfs_to_tail(Snake[] snakes, int snakeID, Snake future_snake, int height, int width, int how_far_in_future) {
        // Khoảng cách trong tương lai được sử dụng để loại bỏ một
        // phần đuôi của những con rắn khác vì chúng sẽ di chuyển và những khoảng trống đó sẽ không có gì, có thể đi vào được
        if (how_far_in_future > 3) {
            how_far_in_future = 3;
        }
        Point tail = null;
        Point head = null;
        boolean[][] visited = new boolean[height][width];
        for (int i = 0; i < snakes.length; i++) {
            if (snakes[i].getID() == snakeID) {
                List<Point> coords = future_snake.getCoords();
                for (int j = 0; j < coords.size() - 1; j++) {
                    visited[coords.get(j).y][coords.get(j).x] = true;
                }
                head = coords.get(0);
                tail = new Point(coords.get(coords.size() - 1).x, coords.get(coords.size() - 1).y);
            } else {
                List<Point> coords = snakes[i].getCoords();
                for (int j = 0; j < coords.size() - how_far_in_future; j++) {
                    visited[coords.get(j).y][coords.get(j).x] = true;
                }
            }
        }
        return BFS_Route(visited, head, tail);
    }

    private static Node BFS_Route(boolean[][] visited, Point start, Point end) {
        int height = visited.length;
        int width = visited[0].length;
        Queue<Node> q = new LinkedList<>();
        Node current_node = new Node(start.x, start.y, null, 0);
        q.add(current_node);
        while (q.size() > 0) {
            current_node = q.remove();
            if (current_node.getX() == end.x && current_node.getY() == end.y) {
                return current_node;
            }
            if (current_node.getX() > 0) {
                if (visited[current_node.getY()][current_node.getX() - 1] == false) {
                    visited[current_node.getY()][current_node.getX() - 1] = true;
                    q.add(new Node(current_node.getX() - 1, current_node.getY(), current_node, current_node.getDepth() + 1));
                }
            }
            if (current_node.getX() < width - 1) {
                if (visited[current_node.getY()][current_node.getX() + 1] == false) {
                    visited[current_node.getY()][current_node.getX() + 1] = true;
                    q.add(new Node(current_node.getX() + 1, current_node.getY(), current_node, current_node.getDepth() + 1));
                }
            }
            if (current_node.getY() > 0) {
                if (visited[current_node.getY() - 1][current_node.getX()] == false) {
                    visited[current_node.getY() - 1][current_node.getX()] = true;
                    q.add(new Node(current_node.getX(), current_node.getY() - 1, current_node, current_node.getDepth() + 1));
                }
            }
            if (current_node.getY() < height - 1) {
                if (visited[current_node.getY() + 1][current_node.getX()] == false) {
                    visited[current_node.getY() + 1][current_node.getX()] = true;
                    q.add(new Node(current_node.getX(), current_node.getY() + 1, current_node, current_node.getDepth() + 1));
                }
            }
        }
        return null;
    }

    private static List<Direction> directions_from_start_node(Node node) {
        List<Direction> directions = new ArrayList<Direction>();
        while (node.getPrev() != null) {
            Node next_node = node.getPrev();
            int x = next_node.getX() - node.getX();
            int y = next_node.getY() - node.getY();
            if (x > 0) {
                directions.add(Direction.Right);
            } else if (x < 0) {
                directions.add(Direction.Left);
            } else if (y > 0) {
                directions.add(Direction.Down);
            } else if (y < 0) {
                directions.add(Direction.Up);
            } else {
                System.out.println("Không có sự khác biệt về tọa độ giữa nút hiện tại và nút tiếp.");
            }
            node = next_node;
        }
        return directions;
    }

    private static Node[] closest_snake_to_each_food(Snake[] snakes, int snakeID, Point[] food, int height, int width, int[] closest_snake) {
        // trả về một mảng các Nút chứa các đường đi ngắn nhất từ mỗi miếng thức ăn đến đầu rắn.
        // chính các đối tượng Node sẽ nằm trên đầu rắn, nhưng là phần đầu của danh sách được liên kết chứa toàn bộ đường dẫn đến thực phẩm.
        // nếu một trong các đối tượng Node là null, điều đó có nghĩa là không có tuyến đường nào đến đầu rắn từ thực phẩm đó.
        // ban đầu trả về mảng near_snake đại diện cho con rắn nào gần nhất với từng miếng thức ăn.
        // near_snake [1] = 2 có nghĩa là con rắn gần nhất với thức ăn [1] là con rắn có ID 2
        // bây giờ Recent_snake vẫn được cập nhật, nhưng nó được thực hiện dưới dạng một mảng được truyền dưới dạng đối số.
        int[][] snake_head_matrix = new int[height][width];
        Node[] end_points = new Node[food.length];

        for (int k = 0; k < food.length; k++) {
            Queue<Node> q = new LinkedList<Node>();
            boolean[][] visited = new boolean[height][width];
            for (int i = 0, n = snakes.length; i < n; i++) {
                if (snakes[i].isAlive() == false) {
                    continue;
                }
                List<Point> coords = snakes[i].getCoords();
                snake_head_matrix[coords.get(0).y][coords.get(0).x] = i + 1;
                for (int j = 1, m = coords.size(); j < m; j++) {
                    visited[coords.get(j).y][coords.get(j).x] = true;
                }
            }
            q.add(new Node(food[k].x, food[k].y, null, 0));
            while (q.size() > 0) {
                Node current_node = q.remove();
                if (snake_head_matrix[current_node.getY()][current_node.getX()] != 0) {
                    // đầu rắn gần nhất được tìm thấy để làm thức ăn [k]
                    // lưu trữ nút (với đường dẫn danh sách được liên kết) trong end_point [k], lưu trữ snID trong near_snake []
                    closest_snake[k] = snake_head_matrix[current_node.getY()][current_node.getX()] - 1;
                    end_points[k] = current_node;
                    break;
                }
                if (current_node.getX() > 0) {
                    if (visited[current_node.getY()][current_node.getX() - 1] == false) {
                        visited[current_node.getY()][current_node.getX() - 1] = true;
                        q.add(new Node(current_node.getX() - 1, current_node.getY(), current_node, current_node.getDepth() + 1));
                    }
                }
                if (current_node.getX() < width - 1) {
                    if (visited[current_node.getY()][current_node.getX() + 1] == false) {
                        visited[current_node.getY()][current_node.getX() + 1] = true;
                        q.add(new Node(current_node.getX() + 1, current_node.getY(), current_node, current_node.getDepth() + 1));
                    }
                }
                if (current_node.getY() > 0) {
                    if (visited[current_node.getY() - 1][current_node.getX()] == false) {
                        visited[current_node.getY() - 1][current_node.getX()] = true;
                        q.add(new Node(current_node.getX(), current_node.getY() - 1, current_node, current_node.getDepth() + 1));
                    }
                }
                if (current_node.getY() < height - 1) {
                    if (visited[current_node.getY() + 1][current_node.getX()] == false) {
                        visited[current_node.getY() + 1][current_node.getX()] = true;
                        q.add(new Node(current_node.getX(), current_node.getY() + 1, current_node, current_node.getDepth() + 1));
                    }
                }
            }
        }
        return end_points;
    }

    private static Node aStarRoute(Snake[] snakes, int snakeID, Point[] food, int height, int width) {
        // Nút được trả về là điểm cuối của tuyến và có chuỗi con trỏ dẫn đến điểm bắt đầu (đại diện cho toàn bộ đường dẫn)

// thuật toán tìm đường này dựa trên tìm kiếm A*
// nó tương tự như BFS tuy nhiên Hàng đợi sẽ được sắp xếp dựa trên khoảng cách ước tính của mỗi nút đến thực phẩm
// A* có nghĩa là hiệu quả hơn vì bạn có nhiều khả năng nhìn vào các nút đang trên đường đến đích của bạn
// tuy nhiên, nó buộc chúng ta phải tính khoảng cách ước tính trước khi mỗi nút được thêm vào hàng đợi
// mất một chút thời gian, mà cũng phức tạp kinh !!!
// có thể tìm ra miếng thức ăn gần nhất ngay từ đầu và chỉ tập trung vào miếng đó khi tính khoảng cách
// hoặc có thể nhìn vào từng miếng thức ăn trên mỗi nút bổ sung để luôn tìm thấy miếng thức ăn gần nhất với nút đó
// với nhiều miếng thức ăn và nhiều con rắn trên bảng, các con đường tiềm năng sẽ không mở và do đó khoảng cách ước tính sẽ không chính xác
// vì vậy mình không nghĩ A* sẽ thực sự tốt hơn BFS cho bối cảnh cụ thể này, quá nhiều rắn !!!
        PriorityQueue<Node> pq = new PriorityQueue<Node>(new Comparator<Node>() {
            public int compare(Node n1, Node n2) {
                if (n1.getDistance() < n2.getDistance()) {
                    return -1;
                } else if (n1.getDistance() > n2.getDistance()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        boolean[][] visited = new boolean[height][width];
        updateVisited(visited, snakes);
        boolean[][] foodMatrix = new boolean[height][width];
        for (int i = 0, n = food.length; i < n; i++) {
            foodMatrix[food[i].y][food[i].x] = true;
        }
        for (int i = 0, n = snakes.length; i < n; i++) {
            if (snakes[i].getID() == snakeID) {
                pq.offer(new Node(snakes[i].getHeadX(), snakes[i].getHeadY(), null, 0, 0));
                break;
            }
        }

        while (pq.peek() != null) {
            Node current_node = pq.poll();
            if (foodMatrix[current_node.getY()][current_node.getX()]) {
                return current_node;
            }
            if (current_node.getX() > 0) {
                if (visited[current_node.getY()][current_node.getX() - 1] == false) {
                    // ước tính khoảng cách đến miếng thức ăn gần nhất
                    // khoảng cách này sẽ được sử dụng làm khóa trong hàng đợi ưu tiên của chúng ta
                    double min_distance = 0;
                    for (int j = 0; j < food.length; j++) {
                        int x_dist = food[j].x - current_node.getX() - 1;
                        int y_dist = food[j].y - current_node.getY();
                        double this_dist = Math.sqrt((x_dist * x_dist) + (y_dist * y_dist));
                        if (j == 0 || this_dist < min_distance) {
                            min_distance = this_dist;
                        }
                    }
                    visited[current_node.getY()][current_node.getX() - 1] = true;
                    pq.offer(new Node(current_node.getX() - 1, current_node.getY(), current_node, current_node.getDepth() + 1, min_distance));
                }
            }
            if (current_node.getX() < width - 1) {
                if (visited[current_node.getY()][current_node.getX() + 1] == false) {
                    double min_distance = 0;
                    for (int j = 0; j < food.length; j++) {
                        int x_dist = food[j].x - current_node.getX() + 1;
                        int y_dist = food[j].y - current_node.getY();
                        double this_dist = Math.sqrt((x_dist * x_dist) + (y_dist * y_dist));
                        if (j == 0 || this_dist < min_distance) {
                            min_distance = this_dist;
                        }
                    }
                    visited[current_node.getY()][current_node.getX() + 1] = true;
                    pq.offer(new Node(current_node.getX() + 1, current_node.getY(), current_node, current_node.getDepth() + 1, min_distance));
                }
            }
            if (current_node.getY() > 0) {
                if (visited[current_node.getY() - 1][current_node.getX()] == false) {
                    double min_distance = 0;
                    for (int j = 0; j < food.length; j++) {
                        int x_dist = food[j].x - current_node.getX();
                        int y_dist = food[j].y - current_node.getY() - 1;
                        double this_dist = Math.sqrt((x_dist * x_dist) + (y_dist * y_dist));
                        if (j == 0 || this_dist < min_distance) {
                            min_distance = this_dist;
                        }
                    }
                    visited[current_node.getY() - 1][current_node.getX()] = true;
                    pq.offer(new Node(current_node.getX(), current_node.getY() - 1, current_node, current_node.getDepth() + 1, min_distance));
                }
            }
            if (current_node.getY() < height - 1) {
                if (visited[current_node.getY() + 1][current_node.getX()] == false) {
                    double min_distance = 0;
                    for (int j = 0; j < food.length; j++) {
                        int x_dist = food[j].x - current_node.getX();
                        int y_dist = food[j].y - current_node.getY() + 1;
                        double this_dist = Math.sqrt((x_dist * x_dist) + (y_dist * y_dist));
                        if (j == 0 || this_dist < min_distance) {
                            min_distance = this_dist;
                        }
                    }
                    visited[current_node.getY() + 1][current_node.getX()] = true;
                    pq.offer(new Node(current_node.getX(), current_node.getY() + 1, current_node, current_node.getDepth() + 1, min_distance));
                }
            }
        }
        return null;
    }

    private static Node bfsRoute(Snake[] snakes, int snakeID, Point[] food, int height, int width) {
        // Nút được trả về là điểm cuối của tuyến và có chuỗi con trỏ dẫn đến điểm bắt đầu (đại diện cho toàn bộ đường dẫn)
        Queue<Node> q = new LinkedList<Node>();
        boolean[][] visited = new boolean[height][width];
        updateVisited(visited, snakes);
        boolean[][] foodMatrix = new boolean[height][width];
        for (int i = 0, n = food.length; i < n; i++) {
            foodMatrix[food[i].y][food[i].x] = true;
        }
        for (int i = 0, n = snakes.length; i < n; i++) {
            if (snakes[i].getID() == snakeID) {
                q.add(new Node(snakes[i].getHeadX(), snakes[i].getHeadY(), null, 0));
                break;
            }
        }

        while (q.size() > 0) {
            Node current_node = q.remove();
            if (foodMatrix[current_node.getY()][current_node.getX()]) {
                return current_node;
            }
            if (current_node.getX() > 0) {
                if (visited[current_node.getY()][current_node.getX() - 1] == false) {
                    visited[current_node.getY()][current_node.getX() - 1] = true;
                    q.add(new Node(current_node.getX() - 1, current_node.getY(), current_node, current_node.getDepth() + 1));
                }
            }
            if (current_node.getX() < width - 1) {
                if (visited[current_node.getY()][current_node.getX() + 1] == false) {
                    visited[current_node.getY()][current_node.getX() + 1] = true;
                    q.add(new Node(current_node.getX() + 1, current_node.getY(), current_node, current_node.getDepth() + 1));
                }
            }
            if (current_node.getY() > 0) {
                if (visited[current_node.getY() - 1][current_node.getX()] == false) {
                    visited[current_node.getY() - 1][current_node.getX()] = true;
                    q.add(new Node(current_node.getX(), current_node.getY() - 1, current_node, current_node.getDepth() + 1));
                }
            }
            if (current_node.getY() < height - 1) {
                if (visited[current_node.getY() + 1][current_node.getX()] == false) {
                    visited[current_node.getY() + 1][current_node.getX()] = true;
                    q.add(new Node(current_node.getX(), current_node.getY() + 1, current_node, current_node.getDepth() + 1));
                }
            }
        }
        return null;
    }

    private static Direction first_move_from_node_list(Node end_node) {
        // đi xuống con đường dẫn đến end_node để xác định hướng di chuyển đầu tiên là gì !
        Node future_node = end_node;
        Node past_node = end_node.getPrev();
        while (past_node.getPrev() != null) {
            future_node = past_node;
            past_node = past_node.getPrev();
        }
        int past_x = past_node.getX();
        int past_y = past_node.getY();
        int future_x = future_node.getX();
        int future_y = future_node.getY();
        if (past_x != future_x && past_y != future_y) {
            System.out.println("Lỗi. Future_node và past_node cách nhau hơn 1 lần di chuyển.");
            System.exit(12);
        }
        if (future_x > past_x) {
            return Direction.Right;
        } else if (future_x < past_x) {
            return Direction.Left;
        } else if (future_y > past_y) {
            return Direction.Down;
        } else {
            return Direction.Up;
        }
    }

    private static void updateVisited(boolean[][] visited, Snake[] snakes) {
        for (int i = 0, n = snakes.length; i < n; i++) {
            if (snakes[i].isAlive() == false) {
                continue;
            }
            List<Point> coords = snakes[i].getCoords();
            for (int j = 0, m = coords.size(); j < m; j++) {
                visited[coords.get(j).y][coords.get(j).x] = true;
            }
        }
        return;
    }

    public static ArrayList<Direction> getMultiMove(GameState gs, int snakeID) {
        return null;
    }

    private static class Snake_State {
        Snake[] snakes;

        public Snake_State(Snake[] original_snakes) {
            snakes = new Snake[original_snakes.length];
            for (int i = 0, n = snakes.length; i < n; i++) {
                snakes[i] = original_snakes[i].clone();
            }
        }
    }
}