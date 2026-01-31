package IPPSystem.Controllers;


import IPPSystem.Utils.session;
import javafx.fxml.FXML;

public class createSupervisorController {

    @FXML
    private void handleCancel() {
        session.getInstance().getNavigationController().closeModal();
    }

    @FXML
    private void handleCreateEngineer() {
        session.getInstance()
                .getNavigationController()
                .showModal("createSupervisorModal.fxml");
    }
}

