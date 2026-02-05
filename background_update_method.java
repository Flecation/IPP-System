// Add this method to currentProjectDashboardController.java
private void updateLocationInfoBackground(String selected) {
    if (selected == null)
        return;

    // Calculate progress in background thread
    double doneTasks = 0;
    double totalTasks = 0;
    double earnedValue = 0;
    double plannedValue = 0;
    double remainingDays = 0;
    double totalDuration = 0;

    if (selected.equals("Project Overview")) {
        //for project overview
        for (projects p : projectMap.values()) {
            doneTasks += p.getDoneTasks();
            totalTasks += p.getTotalTasks();
            earnedValue += p.getEarnedValue();
            plannedValue += p.getPlannedValue();
            remainingDays += p.getRemainingDays();
            totalDuration += p.getTotalDuration();
        }
    } else {
        //for active project
        projects p = projectMap.values().stream().findFirst().orElse(null);
        if (p != null) {
            doneTasks = p.getDoneTasks();
            totalTasks = p.getTotalTasks();
            earnedValue = p.getEarnedValue();
            plannedValue = p.getPlannedValue();
            remainingDays = p.getRemainingDays();
            totalDuration = p.getTotalDuration();
        }
    }

    // Update UI on JavaFX Application Thread
    Platform.runLater(() -> {
        // Update location info
        if (selected.equals("Project Overview")) {
            lblProjectName.setText("All Projects Summary");
            lbLocation.setText("Across All Active Sites");
        } else {
            projects activeP = projectMap.values().stream().findFirst().orElse(null);
            if (activeP != null) {
                lblProjectName.setText(activeP.getProjectInstanceName());
                lbLocation.setText(activeP.getProjectLocation());
            }
        }

        // Update progress bars
        updateProgress(remainingDays, totalDuration, pbDayRemaining, lblDayRemainPercent);
        updateProgress(doneTasks, totalTasks, pbCompletedTask, lblCompletedTaskPercent);
        updateProgress(earnedValue, plannedValue, pbEarnedValue, lblEarnedValuePercent);
    });
}
