package client.Controllers;

import dependencies.Collection.StudyGroup;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Controller {

    public final Callable<Integer> blank = () -> 0;

    protected void updateTableView(ArrayList<StudyGroup> groups) {
    }
}
