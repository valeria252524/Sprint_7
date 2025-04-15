package order;

import java.util.List;

public class Order {

    private List<String> color;

    // Можно добавить и другие поля, если хочешь кастомизацию
    public Order(List<String> color) {
        this.color = color;
    }

    public List<String> getColor() {
        return color;
    }
}
