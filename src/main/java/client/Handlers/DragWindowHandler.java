//package client.Handlers;
//
//import javafx.event.EventHandler;
//import javafx.scene.input.MouseEvent;
//import javafx.stage.Stage;
//
//public class DragWindowHandler implements EventHandler<MouseEvent> {
//    private Stage primaryStage; //primaryStage is the Stage in the start method header
//    private double oldStageX;
//    private double oldStageY;
//    private double oldScreenX;
//    private double oldScreenY;
//
//    public DragWindowHandler(Stage primaryStage) { //Constructor
//        this.primaryStage = primaryStage;
//    }
//
//    @Override
//    public void handle(MouseEvent e) {
//        if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {    //Mouse down event
//            this.oldStageX = this.primaryStage.getX();
//            this.oldStageY = this.primaryStage.getY();
//            this.oldScreenX = e.getScreenX();
//            this.oldScreenY = e.getScreenY();
//
//        } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {  //Mouse dragging event
//            this.primaryStage.setX(e.getScreenX() - this.oldScreenX + this.oldStageX);
//            this.primaryStage.setY(e.getScreenY() - this.oldScreenY + this.oldStageY);
//        }
//    }
//
//}
//
//
