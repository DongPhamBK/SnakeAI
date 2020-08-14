//đã xong mục này

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

//lớp tạo hình con rắn điều khiển bởi người chơi, dùng 4 phím A S W D
public class Snake {

    private List<Point> coords;//liên quan đến thư viện
    private Direction direction;
    private List<Direction> moveQ;
    private int prevTailX, prevTailY;
    private int id;
    private Color color;
    private boolean alive;//trạng thái

    public Snake(int id, int x, int y, Direction dir, Color clr) {
        // chọn ngẫu nhiên vị trí
        this.id = id;
        moveQ = new ArrayList<Direction>();
        coords = new ArrayList<Point>();
        coords.add(new Point(x, y));
        prevTailX = x;
        prevTailY = y;
        direction = dir;
        if (dir == Direction.Right) {
            coords.add(new Point(x - 1, y));
            prevTailX = x - 2;
        } else if (dir == Direction.Left) {
            coords.add(new Point(x + 1, y));
            prevTailX = x + 2;
        } else if (dir == Direction.Up) {
            coords.add(new Point(x, y + 1));
            prevTailY = y + 2;
        } else {
            coords.add(new Point(x, y - 1));
            prevTailY = y - 2;
        }
        color = clr;
        alive = true;
    }

    public Snake() {
        moveQ = new ArrayList<Direction>();
        coords = new ArrayList<Point>();
    }

    public Snake clone() {
        // bản sao để con rắn mới này không chia sẻ dữ liệu với con rắn ban đầu
        Snake newSnake = new Snake();
        if (!alive) {
            newSnake.setAliveStatus(false);
            newSnake.setID(id);
            return newSnake;
        }
        newSnake.setAliveStatus(true);
        newSnake.setID(id);
        newSnake.setCoords(coords);
        newSnake.setPrevTail(prevTailX, prevTailY);
        newSnake.setDirection(direction);
        newSnake.setMoveQ(moveQ);
        newSnake.setColor(color);
        return newSnake;
    }

    //các tham số trả về tương ứng
    public Color getColor() {
        return color;
    }

    public void setColor(Color clr) {
        color = clr;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAliveStatus(boolean b) {
        alive = b;
    }

    public int getHeadX() {
        return coords.get(0).x;
    }

    public int getHeadY() {
        return coords.get(0).y;
    }

    public int getTailX() {
        return coords.get(coords.size() - 1).x;
    }

    public int getTailY() {
        return coords.get(coords.size() - 1).y;
    }

    public void setCoords(List<Point> newCoords) {

        coords.clear();
        for (int i = 0; i < newCoords.size(); i++) {
            coords.add(i, new Point(newCoords.get(i).x, newCoords.get(i).y));
        }
    }

    public List<Point> getCoords() {
        return coords;
    }

    public int getPrevTailX() {
        return prevTailX;
    }

    public int getPrevTailY() {
        return prevTailY;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction dir) {
        if (dir == null) {
            return;
        }
        direction = dir;
    }

    public void moveDirection(Direction dir) {
        // moveQ rất hữu ích cho những con rắn được điều khiển bằng bàn phím để có thể đăng ký nhiều hơn 1 lần nhấn phím khi
        // nhiều phím được nhấn trong một khoảng thời gian ngắn (ví dụ: đi lên và đi ngay sau đó xuống ngay lập tức để bạn nhấn phải rồi nhanh chóng xuống)
        // cũng tìm một tuyến sau đó thực hiện tuyến đó hiệu quả hơn nhiều so với tìm tuyến trước mỗi lần di chuyển.
        // tuy nhiên hiệu quả này hoàn toàn không đáng tin cậy khi có nhiều hơn 1 con rắn trên bảng.
        if (moveQ.size() == 0) {
            if (!direction.equals(dir))
                // nên kiểm tra để đảm bảo dir! = ngược lại (hướng)
                // ví dụ: rắn di chuyển lÊN, nhưng nó cố gắng chuyển sang để XUỐNG được bảo đảm không hợp lệ, TỨC LÀ ĐI NHẦM
                moveQ.add(dir);
            return;
        }
        if (!moveQ.get(moveQ.size() - 1).equals(dir) && !moveQ.get(moveQ.size() - 1).equals(opposite(dir))) {
            moveQ.add(dir);
        }
    }

    public List<Direction> getMoveQ() {
        return moveQ;
    }

    public void setMoveQ(List<Direction> directions) {

        if (directions != null) {
            for (int i = 0, n = directions.size(); i < n; i++) {
                moveQ.add(directions.get(i));
            }
        } else {
            moveQ.clear();
        }
    }

    public void setPrevTail(int x, int y) {
        prevTailX = x;
        prevTailY = y;
    }

    private Direction opposite(Direction dir) {
        if (dir.equals(Direction.Right))
            return Direction.Left;
        else if (dir.equals(Direction.Left))
            return Direction.Right;
        else if (dir.equals(Direction.Up))
            return Direction.Down;
        else return Direction.Up;
    }

    public int getLength() {
        return coords.size();
    }

    public void eat() {
        coords.add(new Point(prevTailX, prevTailY));
    }

    public void moveForward() {

        int headX = getHeadX();
        int headY = getHeadY();
        prevTailX = coords.get(coords.size() - 1).x;
        prevTailY = coords.get(coords.size() - 1).y;
        for (int i = coords.size() - 1; i >= 1; i--) {
            coords.set(i, coords.get(i - 1));
        }
        if (moveQ.size() > 0) {
            direction = moveQ.get(0);
            moveQ.remove(0);
        }
        switch (direction) {
            case Up:
                headY--;
                coords.set(0, new Point(headX, headY));
                break;
            case Down:
                headY++;
                coords.set(0, new Point(headX, headY));
                break;
            case Left:
                headX--;
                coords.set(0, new Point(headX, headY));
                break;
            case Right:
                headX++;
                coords.set(0, new Point(headX, headY));
                break;
        }
    }
}

enum Direction {
    Up, Down, Left, Right
}
