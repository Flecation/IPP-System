package IPPSystem.Utils;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.function.Consumer;

public class PaginationHelper<T> {

    private final int pageSize;
    private int currentPage = 1;
    private int totalPages;

    private List<T> data;

    private Consumer<List<T>> onPageChanged;

    public PaginationHelper(int pageSize) {
        this.pageSize = pageSize;
    }

    // Set data
    public void setData(List<T> data) {
        this.data = data;
        this.totalPages =
                (int) Math.ceil((double) data.size() / pageSize);
        this.currentPage = 1;
    }

    // What to do when page changes
    public void setOnPageChanged(Consumer<List<T>> onPageChanged) {
        this.onPageChanged = onPageChanged;
    }

    public void goToPage(int page) {
        if (page < 1 || page > totalPages) return;

        currentPage = page;
        notifyPageChange();
    }

    public void next() {
        if (currentPage < totalPages) {
            currentPage++;
            notifyPageChange();
        }
    }

    public void prev() {
        if (currentPage > 1) {
            currentPage--;
            notifyPageChange();
        }
    }

    private void notifyPageChange() {

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, data.size());

        List<T> pageData = data.subList(start, end);

        if (onPageChanged != null) {
            onPageChanged.accept(pageData);
        }
    }

    public void buildButtons(HBox box) {

        box.getChildren().clear();

        Button prev = new Button("Prev");
        prev.setDisable(currentPage == 1);
        prev.setOnAction(e -> prev());
        prev.getStyleClass().add("page-btn");
        box.getChildren().add(prev);

        for (int i = 1; i <= totalPages; i++) {
            int page = i;

            Button btn = new Button(String.valueOf(i));

            if (page == currentPage)
                btn.getStyleClass().add("page-btn-active");
                btn.getStyleClass().add("page-btn");

            btn.setOnAction(e -> goToPage(page));

            box.getChildren().add(btn);
        }

        Button next = new Button("Next");
        next.setDisable(currentPage == totalPages);
        next.setOnAction(e -> next());
        next.getStyleClass().add("page-btn");
        box.getChildren().add(next);
    }




}
