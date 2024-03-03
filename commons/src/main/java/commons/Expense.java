package commons;

public class Expense {
    private Participant participant;
    private String description;
    private double price;
    private String date;
    private Tags tag;

    public Expense(Participant participant, String description, double price, String date, Tags tag) {
        this.participant = participant;
        this.description = description;
        this.price = price;
        this.date = date;
        this.tag = tag;
    }

    public Participant getParticipant() {
        return participant;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public Tags getTag() {
        return tag;
    }

    public void changePrice(double newPrice){
        this.price = newPrice;
    }

    public void split(){
        Event event = new Event();

    }


}
