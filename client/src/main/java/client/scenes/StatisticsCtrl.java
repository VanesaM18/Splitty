package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.ExpenseType;
import commons.Monetary;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

import javafx.scene.control.*;

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
        this.event = server.getEventById(ev.getInviteCode());
        initPieChart();
        initCost();
    }

    private void initCost() {
        Monetary expenseCost = new Monetary(0);
        for(Expense expense : event.getExpenses()) {
            expenseCost = Monetary.add(expenseCost, expense.getAmount());
        }
        cost.setText("Total cost: " + expenseCost.toString()
                + expenseCost.getCurrency().getSymbol());
    }

    private void initPieChart() {
        Double total = 0.0;
        for(Expense expense : server.getAllExpensesFromEvent(event)) {
            total += Double.parseDouble(expense.getAmount().toString())*expense.getTags().size();
        }
        for(ExpenseType tag : event.getTags()) {
            Double totalCost = getAmount(tag);
            PieChart.Data slice = new PieChart.Data(tag.getName(), totalCost);
            pie.getData().add(slice);
            double percentage = (totalCost / total) * 100;
            slice.setName(String.format("%s - %.1f%% (%.2f"+ "\u20AC)", slice.getName(), percentage, slice.getPieValue()));
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
//        pie = new PieChart();
//        pie.setData(FXCollections.observableArrayList());

    }
}
