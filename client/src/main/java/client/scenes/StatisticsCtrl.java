package client.scenes;

import client.utils.ResourceManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.ExpenseType;
import commons.Monetary;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;

public class StatisticsCtrl {
    private Event event;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private PieChart pie;
    @FXML
    private Label cost;

    /**
     * Controller responsible for handling the editing tags
     * functionality.
     *
     * @param server   An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main
     *                 controller.
     */
    @Inject
    public StatisticsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Initialize the event data
     *
     * @param ev the event
     */
    public void setEvent(Event ev) {
        this.event = ev;
        pie.getData().clear();
        initPieChart();
        initCost();
    }

    private void initCost() {
        ResourceManager resourceManager = new ResourceManager(mainCtrl);
        String totalCost = resourceManager.getStringForKey("content_total_cost");
        Monetary expenseCost = new Monetary(0);
        for(Expense expense : event.getExpenses()) {
            expenseCost = Monetary.add(expenseCost, expense.getAmount());
        }
        cost.setText(totalCost + " " + expenseCost.toString()
                + expenseCost.getCurrency().getSymbol());
    }

    private void initPieChart() {
        pie.setLegendVisible(false);
        Double total = 0.0;
        for(Expense expense : server.getAllExpensesFromEvent(event)) {
            total += Double.parseDouble(expense.getAmount().toString())*expense.getTags().size();
        }
        for(ExpenseType tag : event.getTags()) {
            Double totalCost = getAmount(tag);
            if(totalCost == 0.0) continue;
            PieChart.Data slice = new PieChart.Data(tag.getName(), totalCost);
            pie.getData().add(slice);
            double percentage = (totalCost / total) * 100;
            slice.setName(String.format("%s - %.1f%% (%.2f"+ "\u20AC)",
                    slice.getName(), percentage, slice.getPieValue()));
            slice.getNode().setStyle("-fx-pie-color: " + tag.getColor() +";");
        }
    }

    private Double getAmount(ExpenseType tag) {
        Monetary value = new Monetary(0);
        for(Expense expense : server.getAllExpensesFromEvent(event)) {
            for (ExpenseType expenseType : expense.getTags()) {
                if(expenseType.getName().equals(tag.getName()))
                    value = Monetary.add(value, expense.getAmount());
            }
        }
        return Double.parseDouble(value.toString());
    }

    /**
     * Goes back to the event overview.
     */
    public void back() {
        mainCtrl.showOverviewEvent(event);
        pie.getData().clear();
    }

    /**
     * Refreshes the data
     */
    public void refresh() {
        if (this.event != null) {
            this.event = server.getEventById(this.event.getInviteCode());
            setEvent(this.event);
        }
    }

    /**
     * Event handler for pressing a key.
     *
     * @param e the key that is pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                back();
                break;
            case ESCAPE:
                back();
                break;
            default:
                break;
        }
    }
}
