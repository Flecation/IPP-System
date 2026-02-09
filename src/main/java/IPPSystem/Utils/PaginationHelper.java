package IPPSystem.Utils;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PaginationHelper<T> {

    private List<T> data = new ArrayList<>();
    private final int pageSize;

    private int currentPage = 1;

    // window start (first page number shown in buttons)
    private int windowStart = 1;

    // configurable: how many page buttons to show
    private final int windowSize = 3;

    private Consumer<List<T>> onPageChanged;

    public PaginationHelper(int pageSize) {
        this.pageSize = Math.max(1, pageSize);
    }

    public void setOnPageChanged(Consumer<List<T>> onPageChanged) {
        this.onPageChanged = onPageChanged;
    }

    public void setData(List<T> newData) {
        this.data = (newData == null) ? new ArrayList<>() : new ArrayList<>(newData);
        int total = getTotalPages();
        if (total == 0) {
            currentPage = 1;
            windowStart = 1;
        } else {
            if (currentPage > total) currentPage = total;
            windowStart = clampWindowStart(windowStart);
        }
    }

    public int getTotalPages() {
        if (data == null || data.isEmpty()) return 0;
        return (int) Math.ceil(data.size() / (double) pageSize);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void goToPage(int page) {
        int total = getTotalPages();
        if (total == 0) {
            currentPage = 1;
            windowStart = 1;
            fire();
            return;
        }

        page = Math.max(1, Math.min(page, total));
        currentPage = page;

        // Keep current page visible inside the 3-button window
        if (currentPage < windowStart) {
            windowStart = currentPage;
        } else if (currentPage > windowStart + windowSize - 1) {
            windowStart = currentPage - (windowSize - 1);
        }

        windowStart = clampWindowStart(windowStart);

        fire();
    }

    private int clampWindowStart(int proposed) {
        int total = getTotalPages();
        if (total <= windowSize) return 1;

        int maxStart = total - windowSize + 1;
        return Math.max(1, Math.min(proposed, maxStart));
    }

    private void fire() {
        if (onPageChanged != null) onPageChanged.accept(getPageData(currentPage));
    }

    public List<T> getPageData(int page) {
        int total = getTotalPages();
        if (total == 0) return Collections.emptyList();

        page = Math.max(1, Math.min(page, total));
        int from = (page - 1) * pageSize;
        int to = Math.min(from + pageSize, data.size());
        if (from >= to) return Collections.emptyList();
        return data.subList(from, to);
    }

    /**
     * Build UI: "< 1 2 3 >" (max 3 numbers)
     * If total pages <= 3, it will show only those pages, no shifting.
     */
    public void buildButtons(HBox paginationBox) {
        if (paginationBox == null) return;

        paginationBox.getChildren().clear();
        paginationBox.setAlignment(Pos.CENTER_LEFT);
        paginationBox.setSpacing(8);

        int total = getTotalPages();
        if (total <= 1) return; // no pagination needed

        // Left arrow (move window and/or page backward)
        Button left = new Button("<");
        left.getStyleClass().add("page-btn");
        left.setDisable(currentPage == 1);
        left.setOnAction(e -> {
            // Move one page back
            goToPage(currentPage - 1);
        });

        // Right arrow (move window and/or page forward)
        Button right = new Button(">");
        right.getStyleClass().add("page-btn");
        right.setDisable(currentPage == total);
        right.setOnAction(e -> {
            // Move one page forward
            goToPage(currentPage + 1);
        });

        paginationBox.getChildren().add(left);

        // Decide which 3 page numbers to show
        int start = (total <= windowSize) ? 1 : windowStart;
        int end = Math.min(total, start + windowSize - 1);

        for (int p = start; p <= end; p++) {
            final int pageNum = p;
            Button b = new Button(String.valueOf(pageNum));
            b.getStyleClass().add("page-btn");

            if (pageNum == currentPage) {
                // optional style for active page (use your css)
                b.getStyleClass().add("active-page-btn");
                b.setDisable(true);
            }

            b.setOnAction(e -> goToPage(pageNum));
            paginationBox.getChildren().add(b);
        }

        paginationBox.getChildren().add(right);
    }
}
